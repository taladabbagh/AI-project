package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

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
    private Pane imgPane;

    @FXML
    private ComboBox<String> comboS;

    @FXML
    private ComboBox<String> comboT;

    @FXML
    private TextArea aDist;

    @FXML
    private TextArea aPath;

    @FXML
    private TextArea gDist;

    @FXML
    private TextArea gPath;

    @FXML
    private Button resetBtn;

    @FXML
    private Button runB;

    private Map<String, double[]> cityCoordinates;

    @FXML
    public void initialize() {
        loadCityCoordinates();
        plotCityCircles();

        // Initialize your controller logic here
        // For example, you can set event handlers for buttons or populate ComboBox options
        runB.setOnAction(event -> runAlgorithm());
        resetBtn.setOnAction(event -> reset());
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

    private void drawPath(List<String> path, Color color) {
        Path pathLine = new Path();
        pathLine.setStroke(color);
        pathLine.setStrokeWidth(2.0);

        for (int i = 0; i < path.size() - 1; i++) {
            String city1 = path.get(i);
            String city2 = path.get(i + 1);
            double[] coordinates1 = cityCoordinates.get(city1);
            double[] coordinates2 = cityCoordinates.get(city2);
            double startX = coordinates1[0];
            double startY = coordinates1[1];
            double endX = coordinates2[0];
            double endY = coordinates2[1];

            if (i == 0) {
                pathLine.getElements().add(new MoveTo(startX, startY));
            }

            pathLine.getElements().add(new LineTo(endX, endY));
        }

        imgPane.getChildren().add(pathLine);
    }


    private void plotCityCircles() {
        imgPane.getChildren();
        for (Map.Entry<String, double[]> entry : cityCoordinates.entrySet()) {
            String city = entry.getKey();
            double[] coordinates = entry.getValue();
            double x = coordinates[0];
            double y = coordinates[1];
            Circle circle = new Circle(x, y, 5, Color.RED);
            circle.setOnMouseClicked(event -> {
                if (comboS.getValue() == null) {
                    comboS.setValue(city);
                    circle.setFill(Color.BLUE);
                }
                // Check if the target city is selected
                else if (comboT.getValue() == null && !comboS.getValue().isEmpty()) {
                    comboT.setValue(city);
                    circle.setFill(Color.GREEN);
                }
            });
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
            drawPath(greedyPathList, Color.YELLOW);
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
            drawPath(aStarPathList, Color.PURPLE);
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

    private void reset() {
        comboS.getSelectionModel().clearSelection();
        comboT.getSelectionModel().clearSelection();
        aDist.clear();
        aPath.clear();
        gDist.clear();
        gPath.clear();

        imgPane.getChildren().removeIf(node -> node instanceof Path);


        for (Node node : imgPane.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                circle.setFill(Color.RED);
            }
        }
    }
}
