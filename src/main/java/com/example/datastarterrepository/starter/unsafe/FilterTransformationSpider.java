package com.example.datastarterrepository.starter.unsafe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("findBy")
@RequiredArgsConstructor
public class FilterTransformationSpider implements TransformationSpider{

    private final Map<String, FilterTransformation> transformationMap;

    @Override
    public SparkTransfomation createTransformation(List<String> remainingWords, Set<String> fieldNames) {
        String fieldName = WordsMatcher.FindAndRemoveMatchingPiecesIfExist(fieldNames, remainingWords);
        String filterName = WordsMatcher.FindAndRemoveMatchingPiecesIfExist(transformationMap.keySet(), remainingWords);
       return transformationMap.get(filterName);
    }
}
