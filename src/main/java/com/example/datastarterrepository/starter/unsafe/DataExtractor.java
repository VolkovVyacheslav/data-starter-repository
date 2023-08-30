package com.example.datastarterrepository.starter.unsafe;



import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.RowSet;

public interface DataExtractor {
    Dataset<Row> readData(String pathToData, ConfigurableApplicationContext context);
}
