package org.example.kickoff.view.composite;

import static java.lang.Boolean.TRUE;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;
import static org.omnifaces.util.Ajax.update;
import static org.omnifaces.util.Components.getAttribute;
import static org.omnifaces.utils.Lang.coalesce;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.omnifaces.util.State;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@FacesComponent("inputLocalDate")
public class InputLocalDate extends UIInput implements NamingContainer {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final int DEFAULT_YEAR_RANGE = 100; // Default to -100y ~ +100y

	// Properties -----------------------------------------------------------------------------------------------------

	private SelectOneMenu day;
	private SelectOneMenu month;
	private SelectOneMenu year;

	// Variables ------------------------------------------------------------------------------------------------------

	private final State state = new State(getStateHelper());

	public InputLocalDate() {
		super.setRequired(getAttribute(this, "required") == TRUE);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	/**
	 * Invoked when JSF renders the input field.
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		LocalDate now = now();
		LocalDate min = coalesce(getAttribute(this, "min"), now.minusYears(DEFAULT_YEAR_RANGE));
		LocalDate max = coalesce(getAttribute(this, "max"), now.plusYears(DEFAULT_YEAR_RANGE));
		LocalDate value = (LocalDate) getValue();

		day.setValue(value != null ? value.getDayOfMonth() : null);
		month.setValue(value != null ? value.getMonthValue() : null);
		year.setValue(value != null ? value.getYear() : null);

		setYears(iterate(max.getYear(), i -> i - 1).limit(max.getYear() - min.getYear()).boxed().collect(toList()));
		setMonthsBasedOnCurrentYear(max);
		setDaysBasedOnCurrentMonth(max);

		super.encodeBegin(context);
	}

	private int setDaysBasedOnCurrentMonth(LocalDate max) {
		int maxDays = 31;

		if (max != null) {
			Integer currentMonth = (Integer) month.getValue();

			if (currentMonth != null) {
				Integer currentYear = (Integer) year.getValue();

				if (currentYear != null) {

					if (YearMonth.of(currentYear, currentMonth).equals(YearMonth.from(max))) {
						maxDays = max.getDayOfMonth();
					}
					else {
						maxDays = Month.of(currentMonth).length(Year.isLeap(currentYear));
					}
				}
			}
		}

		setDays(range(1, maxDays + 1).boxed().collect(toList()));
		return maxDays;
	}

	private int setMonthsBasedOnCurrentYear(LocalDate max) {
		int maxMonths = Month.values().length;

		if (max != null) {
			Integer currentYear = (Integer) year.getValue();

			if (currentYear != null && currentYear == max.getYear()) {
				maxMonths = max.getMonthValue();
			}
		}

		setMonths(range(1, maxMonths + 1).boxed().collect(toList()));
		return maxMonths;
	}

	/**
	 * Invoked when month dropdown is changed.
	 */
	public void updateDaysIfNecessary() {
		int oldMaxDays = getDays().size();
		int newMaxDays = setDaysBasedOnCurrentMonth(getAttribute(this, "max"));

		if (oldMaxDays != newMaxDays) {
			Integer currentDay = (Integer) day.getValue();

			if (currentDay != null && currentDay > newMaxDays) {
				day.setValue(newMaxDays);
			}

			update(day.getClientId());
		}
	}

	/**
	 * Invoked when year dropdown is changed.
	 */
	public void updateMonthsIfNecessary() {
		int oldMaxMonths = getMonths().size();
		int newMaxMonths = setMonthsBasedOnCurrentYear(getAttribute(this, "max"));

		if (oldMaxMonths != newMaxMonths) {
			Integer currentMonth = (Integer) month.getValue();

			if (currentMonth != null && currentMonth > newMaxMonths) {
				month.setValue(newMaxMonths);
			}

			update(month.getClientId());
		}

		updateDaysIfNecessary();
	}

	/**
	 * Invoked when form is submitted.
	 */
	@Override
	public Object getSubmittedValue() {
		return year.getSubmittedValue() + "-" + month.getSubmittedValue() + "-" + day.getSubmittedValue();
	}

	@Override
	protected Object getConvertedValue(FacesContext context, Object submittedValue) throws ConverterException {
		try {
			String[] yearMonthAndDay = ((String) submittedValue).split("-");
			return LocalDate.of(Integer.valueOf(yearMonthAndDay[0]), Integer.valueOf(yearMonthAndDay[1]), Integer.valueOf(yearMonthAndDay[2]));
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setValid(boolean valid) {
		day.setValid(valid);
		month.setValid(valid);
		year.setValid(valid);
		super.setValid(valid);
	}

	// Getters/setters ------------------------------------------------------------------------------------------------

	public SelectOneMenu getDay() {
		return day;
	}

	public void setDay(SelectOneMenu day) {
		this.day = day;
	}

	public List<Integer> getDays() {
		return state.get("days");
	}

	public void setDays(List<Integer> days) {
		state.put("days", days);
	}

	public SelectOneMenu getMonth() {
		return month;
	}

	public void setMonth(SelectOneMenu month) {
		this.month = month;
	}

	public List<Integer> getMonths() {
		return state.get("months");
	}

	public void setMonths(List<Integer> months) {
		state.put("months", months);
	}

	public SelectOneMenu getYear() {
		return year;
	}

	public void setYear(SelectOneMenu year) {
		this.year = year;
	}

	public List<Integer> getYears() {
		return state.get("years");
	}

	public void setYears(List<Integer> years) {
		state.put("years", years);
	}

}