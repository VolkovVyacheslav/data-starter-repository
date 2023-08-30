package com.example.datastarterrepository.repository;

import com.example.datastarterrepository.model.Criminal;

import java.util.List;

public interface CriminalRepository extends SparkRepository<Criminal> {

    List<Criminal> findByNumberBetween(int min, int max);
}
