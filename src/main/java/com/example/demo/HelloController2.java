package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloController2 {

    private static final String CITIES_FILE = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/Cities.csv";

    @FXML
    private Pane Pane;

    @FXML
    private TextArea aDist;

    @FXML
    private TextArea aPath;

    @FXML
    private ComboBox<String> comboS;

    @FXML
    private ComboBox<String> comboT;

    @FXML
    private TextArea gDist;

    @FXML
    private TextArea gPath;

    @FXML
    private ImageView imageV;

    @FXML
    private Pane imgPane;

    @FXML
    private Button resetBtn;

    @FXML
    private Button runB;

    private Map<String, double[]> cityCoordinates;

    @FXML
    public void initialize() {

        String imagePath = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/palestineMap.png"; // Replace with the actual image path
        Image image = new Image("file:" + imagePath);
        imageV.setImage(image);
        imageV.setPreserveRatio(true);

        imgPane.setPrefWidth(image.getWidth());
        imgPane.setPrefHeight(image.getHeight());

        loadCityCoordinates();
        plotCityCircles();

        // Initialize your controller logic here
        // For example, you can set event handlers for buttons or populate ComboBox options
        runB.setOnAction(event -> runAlgorithm());
        resetBtn.setOnAction(event -> resetFields());
        populateComboBoxes();

    }

    private void populateComboBoxes() {
        List<String> cityNames = new ArrayList<>(cityCoordinates.keySet());
        comboS.getItems().addAll(cityNames);
        comboT.getItems().addAll(cityNames);
    }

    private void loadCityCoordinates() {
        cityCoordinates = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CITIES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city = parts[0].trim();
                double x = Double.parseDouble(parts[1].trim());
                double y = Double.parseDouble(parts[2].trim());
                cityCoordinates.put(city, new double[]{x, y});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void plotCityCircles() {
        imgPane.getChildren().clear();
        for (Map.Entry<String, double[]> entry : cityCoordinates.entrySet()) {
            String city = entry.getKey();
            double[] coordinates = entry.getValue();
            double x = coordinates[0];
            double y = coordinates[1];
            Circle circle = new Circle(x, y, 5);
            circle.setFill(Color.RED);
            imgPane.getChildren().add(circle);
        }
    }

    private void runAlgorithm() {
        // Get selected source and goal cities from combo boxes
        String sourceCity = comboS.getValue();
        String goalCity = comboT.getValue();

        // Run the algorithms and get the results
        GreedyAlgorithm.readInputFiles();
        AStarAlgorithm.readInputFiles();
        double greedyDistance = 0.0;
        double aStarDistance = 0.0;
        StringBuilder greedyPath = new StringBuilder();
        StringBuilder aStarPath = new StringBuilder();

        // Run the Greedy algorithm
        List<String> greedyPathList = GreedyAlgorithm.findPath(sourceCity, goalCity);
        if (greedyPathList != null) {
            for (String city : greedyPathList) {
                greedyPath.append(city).append(" -> ");
            }
            greedyPath.delete(greedyPath.length() - 4, greedyPath.length());
            greedyDistance = calculatePathDistance(greedyPathList);
        } else {
            greedyPath.append("No path found.");
        }

        // Run the A* algorithm
        List<String> aStarPathList = AStarAlgorithm.findPath(sourceCity, goalCity);
        if (aStarPathList != null) {
            for (String city : aStarPathList) {
                aStarPath.append(city).append(" -> ");
            }
            aStarPath.delete(aStarPath.length() - 4, aStarPath.length());
            aStarDistance = calculatePathDistance(aStarPathList);
        } else {
            aStarPath.append("No path found.");
        }

        // Update the text areas with the results
        aDist.setText(String.valueOf(aStarDistance));
        aPath.setText(aStarPath.toString());
        gDist.setText(String.valueOf(greedyDistance));
        gPath.setText(greedyPath.toString());
    }

    private double calculatePathDistance(List<String> path) {
        double distance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String city1 = path.get(i);
            String city2 = path.get(i + 1);
            distance += GreedyAlgorithm.getStreetDistance(city1, city2);
        }
        return distance;
    }
    private void resetFields() {
        // Clear the text areas and reset combo boxes and image view
        aDist.clear();
        aPath.clear();
        comboS.getSelectionModel().clearSelection();
        comboT.getSelectionModel().clearSelection();
        gDist.clear();
        gPath.clear();
        // ...
    }
}
