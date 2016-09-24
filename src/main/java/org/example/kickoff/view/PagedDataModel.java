package org.example.kickoff.view;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableList;
import static org.omnifaces.utils.Lang.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omnifaces.persistence.model.BaseEntity;
import org.omnifaces.persistence.model.dto.SortFilterPage;
import org.omnifaces.utils.collection.PartialResultList;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public abstract class PagedDataModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 1L;

	private String sortField;
	private String sortOrder;
	private final List<String> filterableFields;
	private Map<String, Object> filters;
	private Map<String, Object> mappedFilters;
	private boolean filterWithAND;
	private List<T> filteredValue;
	private List<T> selection;

	public PagedDataModel(String defaultSortField, SortOrder defaultSortOrder, String... filterableFields) {
		sortField = defaultSortField;
		sortOrder = defaultSortOrder.name();
		this.filterableFields = filterableFields != null ? unmodifiableList(asList(filterableFields)) : emptyList();
		filters = new HashMap<>();
		setRowCount(-1);
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		mappedFilters = new HashMap<>(filters);
		mappedFilters.putAll(this.filters);
		Map<String, Object> remappedFilters = remapGlobalFilter(mappedFilters);

		if (sortField != null) {
			setSortField(sortField);
			setSortOrder(sortOrder.name());
		}

		boolean countNeedsUpdate = getRowCount() <= 0 || !remappedFilters.equals(mappedFilters);
		mappedFilters = remappedFilters;
		filterWithAND = !filters.containsKey("globalFilter");

		List<T> list = load(new SortFilterPage(first, pageSize, getSortField(), getSortOrder(), filterableFields, mappedFilters, filterWithAND), countNeedsUpdate);

		if (countNeedsUpdate && list instanceof PartialResultList) {
			setRowCount(((PartialResultList<T>) list).getEstimatedTotalNumberOfResults());
		}

		return list;
	}

	public abstract List<T> load(SortFilterPage page, boolean countNeedsUpdate);

	@Override
	public Object getRowKey(T entity) {
		return ((BaseEntity<?>) entity).getId();
	}

	@Override
	public T getRowData(String rowKey) {
		return load(new SortFilterPage(0, 1, getSortField(), getSortOrder(), emptyList(), singletonMap("id", rowKey), true), false).get(0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getWrappedData() {
		return (List<T>) super.getWrappedData();
	}

	private Map<String, Object> remapGlobalFilter(Map<String, Object> filters) {
		Map<String, Object> mappedFilters = new HashMap<>(filters);
		Object globalFilter = mappedFilters.remove("globalFilter");
		mappedFilters.values().remove(null);

		for (String filterableField : filterableFields) {
			if (!"globalFilter".equals(filterableField)) {
				Object filterableFieldValue = filters.get(filterableField);

				if (!isEmpty(filterableFieldValue)) {
					mappedFilters.put(filterableField, filterableFieldValue);
				}
				else if (!isEmpty(globalFilter)) {
					mappedFilters.put(filterableField, globalFilter);
				}
			}
		}

		return mappedFilters;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public List<String> getFilterableFields() {
		return filterableFields;
	}

	public Map<String, Object> getFilters() {
		return filters;
	}

	public boolean isFilterWithAND() {
		return filterWithAND;
	}

	public List<T> getFilteredValue() {
		return filteredValue;
	}

	public void setFilteredValue(List<T> filteredValue) {
		this.filteredValue = filteredValue;
	}

	public List<T> getSelection() {
		return selection;
	}

	public void setSelection(List<T> selection) {
		this.selection = selection;
	}

}