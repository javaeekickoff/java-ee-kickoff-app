package org.example.kickoff.model;

public abstract class BaseEntity<T extends Number> {

	public abstract T getId();

	public abstract void setId(T id);
	
	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		BaseEntity<?> other = (BaseEntity<?>) obj;
		if (getId() == null) {
			return other.getId() == null ? this == other : false;
		}

		return getId().equals(other.getId());
	}

}
