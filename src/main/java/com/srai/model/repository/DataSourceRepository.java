package com.srai.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srai.model.DataSource;


@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long>{

}
