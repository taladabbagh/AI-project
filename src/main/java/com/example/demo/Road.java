package com.example.demo;

public class Road {
    private String city;
    private double distance;

    public Road(String city, double distance) {
        this.city = city;
        this.distance = distance;
    }

    public String getCity() {
        return city;
    }

    public double getDistance() {
        return distance;
    }
}