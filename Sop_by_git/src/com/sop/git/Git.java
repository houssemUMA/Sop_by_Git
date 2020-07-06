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
	private int[] set_profit;
	public int[] points_profit = new int[pointsNumber];
	public int[] points_cluster = new int[pointsNumber];
	private int[][] set_cluster;
	private int set_data[][];
	public double[][] points = new double[pointsNumber][2];
	public double m_Distance[][] = new double[pointsNumber][pointsNumber];
	List<Integer> randomSet;

	public static void main(String[] args) {
		Git test = new Git();
		test.run_one_time();
	}

	public void run_one_time() {
		read_data();
		fillDistanceMatrix();
		prepare_data();
		generateRandomPath();
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
		// fill_set_profit is : each cluster has its profit
		fill_set_profit();
		// fill_set_cluster is : each cluster has its vertices
		fill_set_cluster();
		// the index of the array is the id point and the value of each case is the
		// profit
		fill_points_profit();
		// the index of the array is the id point and the value of each case is the
		// cluster id
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

}
