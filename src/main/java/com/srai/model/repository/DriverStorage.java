package com.srai.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srai.model.DriverDetail;


@Repository
public interface DriverStorage extends JpaRepository<DriverDetail, Long>{

}
