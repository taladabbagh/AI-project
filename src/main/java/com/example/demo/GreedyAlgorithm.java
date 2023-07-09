package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GreedyAlgorithm {
    private static final String CITIES_FILE = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/Cities.csv";
    private static final String ROADS_FILE = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/Roads.csv";

    private static Map<String, List<Road>> graph;
    private static Map<String, Double> streetDistances;

    static double getStreetDistance(String city1, String city2) {
        String key = city1 + "-" + city2;
        return streetDistances.getOrDefault(key, 0.0);
    }

    // Inside GreedyAlgorithm class

    public static void readInputFiles() {
        graph = new HashMap<>();
        streetDistances = new HashMap<>();

        // Read Cities.csv file
        try (BufferedReader br = new BufferedReader(new FileReader(CITIES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city = parts[0].trim();
                graph.put(city, new ArrayList<>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read Roads.csv file
        try (BufferedReader br = new BufferedReader(new FileReader(ROADS_FILE))) {
            String line;
            boolean firstLine = true; // Flag to skip the header row
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the header row
                }

                String[] parts = line.split(",");
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());

                if (graph.containsKey(city1) && graph.containsKey(city2)) {
                    graph.get(city1).add(new Road(city2, distance));
                    graph.get(city2).add(new Road(city1, distance));
                    streetDistances.put(city1 + "-" + city2, distance);
                    streetDistances.put(city2 + "-" + city1, distance);
                } else {
                    System.err.println("Invalid city name in the Roads.csv file: " + city1 + " or " + city2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> findPath(String source, String goal) {
        Set<String> visited = new HashSet<>();
        Map<String, String> cameFrom = new HashMap<>();

        PriorityQueue<String> openSet = new PriorityQueue<>(Comparator.comparingDouble(city -> calculateDistance(city, goal)));
        openSet.add(source);

        while (!openSet.isEmpty()) {
            String current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            visited.add(current);

            for (Road road : graph.get(current)) {
                String neighborCity = road.getCity();

                if (!visited.contains(neighborCity)) {
                    cameFrom.put(neighborCity, current);
                    openSet.add(neighborCity);
                }
            }
        }

        return null; // No path found
    }

    private static List<String> reconstructPath(Map<String, String> cameFrom, String current) {
        List<String> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }

        Collections.reverse(path);
        return path;
    }

    private static double calculateDistance(String city, String goal) {
        String key = city + "-" + goal;
        return streetDistances.getOrDefault(key, Double.MAX_VALUE);
    }

    private static class Road {
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
}
