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

//     circle.setOnMouseClicked(event -> handleCircleClick(city));
//
//             circle.setOnMouseClicked(event -> {
//             String selectedBuilding = (String) circle.getUserData();
//             if (comboS.getValue() == null) {
//             // No source building selected yet
//             comboS.setValue(selectedBuilding);
//             circle.setFill(Color.BLUE);
//             } else if (comboT.getValue() == null && !selectedBuilding.equals(comboS.getValue())) {
//             // No target building selected and a source building is selected
//             comboT.setValue(selectedBuilding);
//             circle.setFill(Color.GREEN);
//             } else if (!selectedBuilding.equals(comboS.getValue()) && !selectedBuilding.equals(comboT.getValue())) {
//             reset();
//             }
//             });