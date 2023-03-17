package com.example.homework0309;

public class Person
{
    private String name;
    private int age;
    private int index;

    public Person( int index, String name, int age) {
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
