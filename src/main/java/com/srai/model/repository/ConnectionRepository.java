package com.srai.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srai.model.ConnectionEstablish;


@Repository
public interface ConnectionRepository extends JpaRepository<ConnectionEstablish, Long> {

}
