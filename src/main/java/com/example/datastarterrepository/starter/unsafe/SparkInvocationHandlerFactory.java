package com.example.datastarterrepository.starter.unsafe;

import com.example.datastarterrepository.Source;
import com.example.datastarterrepository.repository.SparkRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Component
@RequiredArgsConstructor
public class SparkInvocationHandlerFactory {

    private final DataExtractorResolver extractorResolver;

    private final Map<String, TransformationSpider> spiderMap;

    private final Map<String, Finalizer> finalizerMap;
    @Setter
    private ConfigurableApplicationContext realContext;

    public SparkInvocationHandler create(Class<? extends SparkRepository> repoInterface){
       Class<?> modelClass = getModelClass(repoInterface);
       Set<String> fieldNames = getFieldNames(modelClass);
       String pathToData = modelClass.getAnnotation(Source.class).value();
       DataExtractor dataExtractor = extractorResolver.resolver(pathToData);
       Map<Method, List<SparkTransfomation>> transformationChain = new HashMap<>();
       Map<Method, Finalizer> method2Finalizer =  new HashMap<>();
       Method[] methods = repoInterface.getMethods();
        for (Method method : methods) {
            TransformationSpider currentSpider = null;
            List<SparkTransfomation> transfomations = new ArrayList<>();

            List<String> methodWords = new ArrayList<>(asList(method.getName().split("(?=\\p{Upper})")));
            while(methodWords.size() > 1){
                String strategyName = WordsMatcher.FindAndRemoveMatchingPiecesIfExist(spiderMap.keySet(), methodWords);
                if(!strategyName.isEmpty()){
                    currentSpider = spiderMap.get(strategyName);
                }
                transfomations.add(currentSpider.createTransformation(methodWords, fieldNames));
            }
            String finalizerName = "collect";
            if(methodWords.size() == 1){
                finalizerName = methodWords.get(0);
            }
            transformationChain.put(method, transfomations);
            method2Finalizer.put(method, finalizerMap.get(finalizerName));
        }
        return SparkInvocatiomHandlerImpl.builder()
                .modelClass(modelClass)
                .pathToData(pathToData)
                .finalizerMap(method2Finalizer)
                .transformationChain(transformationChain)
                .dataExtractor(dataExtractor)
                .context(realContext)
                .build();
    }

    private Set<String> getFieldNames(Class<?> modelClass) {
       return Arrays.stream(modelClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Collections.class.isAssignableFrom(field.getType()))
                .map(Field::getName)
                .collect(Collectors.toSet());

    }

    private Class<?> getModelClass(Class<? extends SparkRepository> repoInterface){
        ParameterizedType genericInterface = (ParameterizedType) repoInterface.getGenericInterfaces()[0];
        Class<?> modelClass = (Class<?>) genericInterface.getActualTypeArguments()[0];
        return modelClass;
    }

}
