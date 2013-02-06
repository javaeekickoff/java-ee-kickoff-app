package org.example.kickoff.model;

public abstract class BaseEntity<T extends Number> {

	public abstract T getId();

	public abstract void setId(T id);

	@Override
	public int hashCode() {
		return (getId() != null)
			? (getClass().hashCode() + getId().hashCode())
			: super.hashCode();
	}

	@Override
	public boolean equals(Object other) {
        return (other != null && getClass() == other.getClass() && getId() != null)
            ? getId().equals(((BaseEntity<?>) other).getId())
            : (other == this);
	}

}