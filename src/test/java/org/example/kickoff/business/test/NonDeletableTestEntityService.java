package org.example.kickoff.business.test;

import javax.ejb.Stateless;

import org.example.kickoff.business.EntityService;

@Stateless
public class NonDeletableTestEntityService extends EntityService<Long, NonDeletableTestEntity> {

}
