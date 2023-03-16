package com.example.homework0309;

public class DataModel {
    private String name;
    private int age;
    private int index;

    public DataModel( int index, String name, int age) {
        this.index = index;
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
    public int getIndex(){
        return index;
    }
}
