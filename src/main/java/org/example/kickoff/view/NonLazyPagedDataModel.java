package org.example.kickoff.view;

import static java.lang.Math.min;
import static org.primefaces.model.SortOrder.DESCENDING;

import java.util.List;

import org.omnifaces.persistence.model.dto.SortFilterPage;

public class NonLazyPagedDataModel<T> extends PagedDataModel<T> {

	private static final long serialVersionUID = 1L;

	private List<T> allData;

	public NonLazyPagedDataModel(List<T> allData) {
		super("id", DESCENDING);
		this.allData = allData;
	}

	@Override
	public List<T> load(SortFilterPage page, boolean countNeedsUpdate) {
		setRowCount(allData.size());
		return allData.subList(min(getRowCount(), page.getOffset()), min(getRowCount(), page.getOffset() + page.getLimit()));
	}

}