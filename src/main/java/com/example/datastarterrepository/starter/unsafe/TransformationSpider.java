package com.example.datastarterrepository.starter.unsafe;

import java.util.List;
import java.util.Set;

public interface TransformationSpider {

    SparkTransfomation createTransformation(List<String> remainingWords, Set<String> fieldNames);
}
