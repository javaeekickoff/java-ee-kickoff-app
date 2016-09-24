package org.example.kickoff.business.test;

import javax.ejb.Stateless;

import org.example.kickoff.business.service.BaseEntityService;

@Stateless
public class TestEntityService extends BaseEntityService<Long, TestEntity> {

}