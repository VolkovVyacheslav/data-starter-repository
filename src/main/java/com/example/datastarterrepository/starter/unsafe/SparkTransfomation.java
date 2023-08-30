package com.example.datastarterrepository.starter.unsafe;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface SparkTransfomation {
    Dataset<Row> transform(Dataset<Row> dataset);
}
