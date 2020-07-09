package com.sop.git;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Git {
	private int set_number;
	private int pointsNumber = 52;
	private double tMax = 1616;
//	private double distanceTraveled;
//	private int totalProfit;
	private int[] set_profit;
	public int[] points_profit = new int[pointsNumber];
	public int[] points_cluster = new int[pointsNumber];
	private int[][] set_cluster;
	private int set_data[][];
	public double[][] points = new double[pointsNumber][2];
	public double m_Distance[][] = new double[pointsNumber][pointsNumber];
	List<Integer> randomSet;
	List<Integer> randomPath = new ArrayList<Integer>();
	List<Integer> firstPath = new ArrayList<Integer>();


	public static void main(String[] args) {
		Git test = new Git();
		test.run_one_time();
	}

	public void run_one_time() {
		read_data();
		fillDistanceMatrix();
		prepare_data();
		generateRandomPath();
		create_initial_solution();
	}

	public void read_data() {
		read_txt_file();
		readCSVfile("one.csv");
	}

	public void read_txt_file() {
		Read_data_set fileReader = new Read_data_set();
		set_data = fileReader.read("two_p2.txt");
		set_number = set_data.length;

	}

	public void readCSVfile(String fileName) {
		int i = 0;
		File file = new File(fileName);
		try {
			Scanner inputStream = new Scanner(file);
			inputStream.next();
			while (inputStream.hasNext()) {
				String data = inputStream.next();
				String values[] = data.split(",");
				for (int j = 0; j < 2; j++) {
					points[i][j] = Double.parseDouble(values[j + 1]);
				}
				i++;
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void fillDistanceMatrix() {
		for (int i = 0; i < pointsNumber; i++) {
			for (int j = 0; j < pointsNumber; j++) {
				m_Distance[i][j] = calculateDistanceBetweenPoints(i, j);
			}
		}
	}

	private double calculateDistanceBetweenPoints(int a, int b) {
		double x1 = points[a][0];
		double x2 = points[b][0];
		double y1 = points[a][1];
		double y2 = points[b][1];
		return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public void prepare_data() {
		// fill_set_profit is : each cluster has its profit ****set_profit
		fill_set_profit();
		// fill_set_cluster is : each cluster has its vertices****set_cluster
		fill_set_cluster();
		/*
		 *  the index of the array is the id point and the value of each case is the
		 *  profit****points_profit
		 */
		fill_points_profit();
		/*
		 *  the index of the array is the id point and the value of each case is the
		 *  cluster id****points_cluster
		 */
		
		fill_points_cluster();
	}

	private void fill_set_profit() {
		set_profit = new int[set_number];
		for (int i = 0; i < set_number; i++) {
			set_profit[i] = set_data[i][1];
//			System.out.println(set_profit[i]);
		}
	}

	private void fill_set_cluster() {
		set_cluster = new int[set_number][];
		for (int i = 0; i < set_number; i++) {
			set_cluster[i] = new int[set_data[i].length - 2];
			for (int j = 0; j < (set_data[i].length - 2); j++) {
				set_cluster[i][j] = set_data[i][j + 2] - 1;

			}
		}
	}

	private void fill_points_profit() {
		int index;
		for (int i = 0; i < set_data.length; i++) {
			for (int j = 2; j < set_data[i].length; j++) {
				index = set_data[i][j] - 1;
				points_profit[index] = set_data[i][1];
			}

		}

	}

	private void fill_points_cluster() {
		int index;
		for (int i = 0; i < set_data.length; i++) {
			for (int j = 2; j < set_data[i].length; j++) {
				index = set_data[i][j] - 1;
				points_cluster[index] = i;

//				System.out.println("index " + index + "  " + points_cluster[index]);
			}

		}

	}

	public void generateRandomPath() {

		initialisePath();
		Collections.shuffle(randomSet.subList(1, set_number));
		System.out.println(randomSet);
	}

	private void initialisePath() {
		randomSet = new ArrayList<Integer>();
		for (int i = 0; i < set_number; i++) {
			randomSet.add(i);
		}
	}
	
	public void create_initial_solution() {
		generateInitialSolution();
		generate_first_path(randomPath, firstPath);

	}
	
	public void generateInitialSolution() {
		verify_clear_list(randomPath);
		randomPath.add(0);
		int index = 0;
		for (int i = 1; i < set_number; i++) {
			index = findNearstPoint(index, randomSet.get(i));
			randomPath.add(index);
		}

		System.out.println(randomPath);

	}
	
	private void verify_clear_list(List<Integer> list) {
		if (list.size() > 0) {
			list.clear();
		}
	}

	private int findNearstPoint(int pointA, int clusterB) {
		double distance;
//		int currentProfit;
		int index = set_cluster[clusterB][0];
		distance = m_Distance[pointA][index];
		for (int i = 1; i < set_cluster[clusterB].length; i++) {
			if (distance > m_Distance[pointA][set_cluster[clusterB][i]]) {
				distance = m_Distance[pointA][set_cluster[clusterB][i]];
				index = set_cluster[clusterB][i];
			}
		}
//		// We add the new distance to the total distance
//		distanceTraveled = distanceTraveled + distance;

//		currentProfit = set_profit[clusterB];
//		// we add the new profit to the total profit
//		totalProfit = totalProfit + currentProfit;
		return index;
	}
	
	public void generate_first_path(List<Integer> long_path, List<Integer> short_path) {
		verify_clear_list(short_path);
		double local_distance = 0;
		int i = 0;
		while (local_distance < tMax) {

			short_path.add(long_path.get(i));
			local_distance = local_distance + m_Distance[long_path.get(i)][long_path.get(i + 1)];
			i++;
		}
		caluculate_path_distance(short_path);
		caluculate_path_profit(short_path);
		System.out.println(short_path);
	}

	
	public double caluculate_path_distance(List<Integer> path) {
		double distance_path = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			distance_path = distance_path + m_Distance[path.get(i)][path.get(i + 1)];
		}
		System.out.println("The new distance is " + distance_path);
		return distance_path;
	}
	
	public int caluculate_path_profit(List<Integer> path) {
		int profit_path = 0;
		for (int i = 0; i < path.size() ; i++) {
			profit_path = profit_path + points_profit[path.get(i)] ;
		}
		System.out.println("The new profit is " + profit_path);
		return profit_path;
	}

// next step is the local search


}
