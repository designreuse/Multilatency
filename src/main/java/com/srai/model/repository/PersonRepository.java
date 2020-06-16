package com.srai.model.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srai.model.Person;

import java.util.List;

/** Repository manager for Person. */
@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

  /** Get collections of Person by name. */
//  List<Person> findByLastName(@Param("name") String name);

}
