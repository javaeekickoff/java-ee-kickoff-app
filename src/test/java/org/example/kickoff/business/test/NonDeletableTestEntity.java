package org.example.kickoff.business.test;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.example.kickoff.business.NonDeletable;
import org.example.kickoff.model.BaseEntity;

@Entity
@Table(name = "non_deletable_test_entity")
@NonDeletable
public class NonDeletableTestEntity extends BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String name;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
