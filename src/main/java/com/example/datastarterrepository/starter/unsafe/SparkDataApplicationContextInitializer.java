package com.example.datastarterrepository.starter.unsafe;

import com.example.datastarterrepository.repository.SparkRepository;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.Introspector;
import java.lang.reflect.Proxy;

public class SparkDataApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        AnnotationConfigApplicationContext tempContext = new AnnotationConfigApplicationContext(InternalConf.class);
        SparkInvocationHandlerFactory factory = tempContext.getBean(SparkInvocationHandlerFactory.class);
        factory.setRealContext(applicationContext);
        tempContext.close();
        registerSparkBeans(applicationContext);
        Reflections scanner = new Reflections(applicationContext.getEnvironment().getProperty("spark.packages-to-scan"));
        scanner.getSubTypesOf(SparkRepository.class).forEach(sparkRepositoryInterface -> {
            Object crang = Proxy.newProxyInstance(sparkRepositoryInterface.getClassLoader()
                    , sparkRepositoryInterface
                    ,ih);
            applicationContext.getBeanFactory().registerSingleton(Introspector.decapitalize(sparkRepositoryInterface.getSimpleName()),
                    crang);
        });
    }

    private void registerSparkBeans(ConfigurableApplicationContext applicationContext) {
        SparkSession sparkSession = SparkSession.builder().master("local[*]").appName(applicationContext.getEnvironment()
                .getProperty("spark.app-name")).getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(sparkSession.sparkContext());
        applicationContext.getBeanFactory().registerSingleton("sparkSession", sparkSession);
        applicationContext.getBeanFactory().registerSingleton("sparkContext", sc);
    }

}
