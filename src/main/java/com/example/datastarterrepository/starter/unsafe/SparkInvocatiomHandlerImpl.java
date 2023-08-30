package com.example.datastarterrepository.starter.unsafe;

import lombok.Builder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Builder
public class SparkInvocatiomHandlerImpl implements SparkInvocationHandler {

    private Class<?> modelClass;
    private String pathToData;
    private DataExtractor dataExtractor;
    private Map<Method, List<SparkTransfomation>> transformationChain;
    private Map<Method, Finalizer> finalizerMap;

    private ConfigurableApplicationContext context;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Dataset<Row> dataset = dataExtractor.readData(pathToData, context);
        List<SparkTransfomation> transfomations = transformationChain.get(method);
        for (SparkTransfomation transfomation : transfomations) {
            dataset = transfomation.transform(dataset);
        }
        Finalizer finalazer = finalizerMap.get(method);
        Object retVal =  finalazer.doAction(dataset);
        return retVal;
    }
}
