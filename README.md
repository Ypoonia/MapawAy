#MapawAy

MetroApp is a Java-based simulation of a metro navigation system inspired by the Delhi Metro Map. It provides users with interactive functionalities such as listing stations, displaying the metro map, computing the shortest paths based on distance or travel time, and calculating journey fares based on the route taken.

## Features

- **List Stations:** View a complete list of metro stations.
- **Display Metro Map:** Visualize the network with station connections and corresponding distances.
- **Shortest Path Calculation:** 
  - **Distance-Based:** Compute the shortest distance between any two stations using Dijkstra’s algorithm.
  - **Time-Based:** Calculate the quickest route, taking into account the travel time between stations.
- **Route Details:** Retrieve the detailed path including the number of interchanges.
- **Fare Calculation:** Automatically calculate the fare based on the journey's distance or time, with adjustments for interchanges.
- **Menu-Driven Interface:** Easy-to-use command line interface to navigate through all functionalities.

## Project Structure

- **MetroApp.java:** Contains the entire source code including:
  - The `Graph_M` class which models the metro network.
  - Inner classes such as `Vertex`, `DijkstraPair`, and `Pair` to support graph operations and algorithms.
  - Methods to add/remove stations (vertices) and routes (edges).
  - Implementation of Dijkstra’s algorithm for finding the shortest paths.
  - A comprehensive menu system for user interaction.

## Prerequisites

- **Java Development Kit (JDK):** Ensure that you have JDK 8 or later installed. You can verify your installation by running:
  ```sh
  java -version
