package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AStarAlgorithm {
    private static final String CITIES_FILE = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/Cities.csv";
    private static final String ROADS_FILE = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/Roads.csv";
    private static final String AIR_DISTANCE_FILE = "C:/Users/Tala Dabbagh/OneDrive/Desktop/AI/demo/src/main/resources/com/example/demo/AirDistance.csv";

    private static Map<String, List<Road>> graph;
    private static Map<String, Double> streetDistances;
    private static Map<String, Double> airDistances;

//    public static void main(String[] args) {
//        readInputFiles();
//        List<String> path = findPath("Safad", "Hebron");
//        if (path != null) {
//            System.out.println("Path found: " + path);
//        } else {
//            System.out.println("No path found.");
//        }
//    }

    public static void readInputFiles() {
        graph = new HashMap<>();
        streetDistances = new HashMap<>();
        airDistances = new HashMap<>();

        // Read Cities.csv file
        try (BufferedReader br = new BufferedReader(new FileReader(CITIES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city = parts[0].trim();
                double airDistance = Double.parseDouble(parts[1].trim());
                double roadDistance = Double.parseDouble(parts[2].trim());
                graph.put(city, new ArrayList<>());
                airDistances.put(city, airDistance);
                streetDistances.put(city, roadDistance);
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
                double roadDistance = Double.parseDouble(parts[2].trim());

                if (graph.containsKey(city1) && graph.containsKey(city2)) {
                    graph.get(city1).add(new Road(city2, roadDistance));
                    graph.get(city2).add(new Road(city1, roadDistance));
                    streetDistances.put(city1 + "-" + city2, roadDistance);
                    streetDistances.put(city2 + "-" + city1, roadDistance);
                } else {
                    System.err.println("Invalid city name in the Roads.csv file: " + city1 + " or " + city2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read AirDistance.csv file
        try (BufferedReader br = new BufferedReader(new FileReader(AIR_DISTANCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double airDistance = Double.parseDouble(parts[2].trim());
                airDistances.put(city1 + "-" + city2, airDistance);
                airDistances.put(city2 + "-" + city1, airDistance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> findPath(String source, String goal) {
        Set<String> visited = new HashSet<>();
        Map<String, String> cameFrom = new HashMap<>();
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();

        PriorityQueue<String> openSet = new PriorityQueue<>(Comparator.comparingDouble(city -> fScore.getOrDefault(city, Double.MAX_VALUE)));
        openSet.add(source);
        gScore.put(source, 0.0);
        fScore.put(source, calculateDistance(source, goal));

        while (!openSet.isEmpty()) {
            String current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            visited.add(current);

            for (Road road : graph.get(current)) {
                String neighborCity = road.getCity();
                double distance = road.getDistance();
                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + distance;

                if (visited.contains(neighborCity) && tentativeGScore >= gScore.getOrDefault(neighborCity, Double.MAX_VALUE)) {
                    continue; // Skip to the next neighbor
                }

                cameFrom.put(neighborCity, current);
                gScore.put(neighborCity, tentativeGScore);
                fScore.put(neighborCity, tentativeGScore + calculateDistance(neighborCity, goal));

                if (!openSet.contains(neighborCity)) {
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
        double airDistance = airDistances.getOrDefault(city, Double.MAX_VALUE);
        double roadDistance = streetDistances.getOrDefault(city + "-" + goal, Double.MAX_VALUE);
        return airDistance + roadDistance;
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
