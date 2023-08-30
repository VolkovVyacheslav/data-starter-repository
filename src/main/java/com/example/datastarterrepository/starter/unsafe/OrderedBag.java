package com.example.datastarterrepository.starter.unsafe;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class OrderedBag<T> {
    private List<T> list;

    public OrderedBag(T[] args){
        this.list = new ArrayList<T>(asList(args));
    }

    public T tekeAndRemove(){
        return list.remove(0);
    }

    public int size(){
        return list.size();
    }

}
