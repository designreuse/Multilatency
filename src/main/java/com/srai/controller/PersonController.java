package com.srai.controller;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.srai.model.Person;
import com.srai.model.repository.PersonRepository;

/** Simple controller to illustrate templates. */
@RestController
@RequestMapping(value = "/person")
public class PersonController {

  /** Person repository. */
  @Autowired
  private transient PersonRepository repository;

  /**
   * Person retriever.
   * @return Person
   */
  @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
  @ResponseBody public ResponseEntity<?> getPerson(@PathVariable final Long personId) {
    final Optional<Person> person = repository.findById(personId);
    if (person == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(person);
  }

  /**
   * Person creation.
   * @return Person
   */
  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody public Object savePerson(@RequestBody final Person person) {
    Person persistedPerson = repository.save(person);
    return persistedPerson;
  }

}
