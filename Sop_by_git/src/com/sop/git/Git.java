package com.sop.git;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Git {
	private int set_number;
	private int pointsNumber = 52;
	private double tMax = 1616;
	private double total_path_distance;
	private int total_path_profit;
	//B is a parameter that adjusts the size of the granular neighborhood
	private double parameter_B=20;
	// m is the number of clusters used in the first solution
	private int m = 0;
	// z_prime is the first path distance
	private double z_prime=0;
	// v is the granularity 
	private double v =0;
	// we save best score here
	private double best_score_solution=0.0;
	// we save here all clusters used in the solution
	private int clusters_used;
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
	List<Integer> solution = new ArrayList<Integer>();
// we save here the best solution
	List<Integer> best_solution = new ArrayList<Integer>();



	public static void main(String[] args) {
		Git test = new Git();
		test.run_one_time();
		test.generateRandomPath();
		test.iterated();
	}
	
	public void iterated() {

		float sec;
		long start = System.currentTimeMillis();

		do {
			System.out.println("******************************");
			initial_solution_steps();
			run_local_search_algorithm();
			clear_functions();
			
			System.out.println();
			long end = System.currentTimeMillis();
			sec = (end - start) / 1000F;
		} while (sec < 0.1);
		
		
	}

	public void clear_functions() {
//		randomSet.clear();
		randomPath.clear();
		firstPath.clear();
		solution.clear();
	}

	public void run_one_time() {
		read_data();
		fillDistanceMatrix();
		prepare_data();
		
		}
	
	public void initial_solution_steps() {
		
		create_initial_solution();
		calculate_granularity();
	}
	
	public void run_local_search_algorithm() {
		solution.addAll(localSearch(firstPath));
		total_path_distance=caluculate_path_distance(solution);
		System.out.println("total_solution_distance_: "+total_path_distance);
		total_path_profit=caluculate_path_profit(solution);
		System.out.println("total_solution_profit_: "+total_path_profit);
		escape(return_clusters_path_for_escape(solution));
		compare_results( solution);
		System.out.println(" best_score "+best_score_solution);
		System.out.println(" best_solution:  "+best_solution);
		
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
//			System.out.println("long_path.get("+i+")"+long_path.get(i));
			if (i<set_number-1) {
				local_distance = local_distance + m_Distance[long_path.get(i)][long_path.get(i + 1)];
				i++;
			}
//			System.out.println("local_distance"+local_distance);
			
		}
		total_path_distance=caluculate_path_distance(short_path);
		total_path_profit=caluculate_path_profit(short_path);
		System.out.println("total_first_path_distance: "+total_path_distance);
		System.out.println("total_first_path_profit: "+total_path_profit);
		System.out.println("The short path is : "+short_path);
		z_prime=total_path_distance;
		m=short_path.size()-1;
	}

	
	public double caluculate_path_distance(List<Integer> path) {
		double distance_path = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			distance_path = distance_path + m_Distance[path.get(i)][path.get(i + 1)];
		}
		//System.out.println("The new distance is " + distance_path);
		return distance_path;
	}
	
	public int caluculate_path_profit(List<Integer> path) {
		int profit_path = 0;
		for (int i = 0; i < path.size() ; i++) {
			profit_path = profit_path + points_profit[path.get(i)] ;
		}
		//System.out.println("The new profit is " + profit_path);
		return profit_path;
	}

// next step is the local search


	
	public List<Integer> localSearch(List<Integer> startSolution) {

		List<Integer> solution = new ArrayList<Integer>(startSolution);
		double delta = 0;
		do {
			delta = move(solution);
		} while (delta < 0);
		System.out.println(solution);
		return solution;
	}
	
	public double move(List<Integer> solution) {
		boolean valid;
		double delta;

		for (int i = 1; i < solution.size() - 2; i++) {
			for (int j = i + 1; j < solution.size() - 1; j++) {
				delta = delta(solution, i, j);
//				System.out.println("we test between  " + solution.get(i) + " and " + solution.get(j));
//				System.out.println("delta is " + delta);
				if (delta < 0) {
					swapElements(solution, j, i);
//					System.out.println(solution);
					valid = valid_insert(solution.size() - 1, randomPath, solution);
//					System.out.println("Validity is " + valid);
					return delta;
				}

			}
		}
		return 0;
	}
	public double delta(List<Integer> path, int pI, int pJ) {
		double scoreOne = 0;
		double scoreTwo = 0;
		int i = pI;
		int j = pJ;
		double delta = 0;
		if ((j - i) == 1) {
			scoreOne = m_Distance[path.get(i - 1)][path.get(i)] + m_Distance[path.get(i)][path.get(j)]
					+ m_Distance[path.get(j)][path.get(j + 1)];
			scoreTwo = m_Distance[path.get(i - 1)][path.get(j)] + m_Distance[path.get(j)][path.get(i)]
					+ m_Distance[path.get(i)][path.get(j + 1)];
			if (m_Distance[path.get(i - 1)][path.get(j)] > v
					|| m_Distance[path.get(j)][path.get(i)] > v
					|| m_Distance[path.get(i)][path.get(j + 1)] > v) {
				scoreTwo = scoreOne + 1;
			}

		} else {

			scoreOne = m_Distance[path.get(i - 1)][path.get(i)] + m_Distance[path.get(i)][path.get(i + 1)]
					+ m_Distance[path.get(j - 1)][path.get(j)] + m_Distance[path.get(j)][path.get(j + 1)];

			scoreTwo = m_Distance[path.get(i - 1)][path.get(j)] + m_Distance[path.get(j)][path.get(i + 1)]
					+ m_Distance[path.get(j - 1)][path.get(i)] + m_Distance[path.get(i)][path.get(j + 1)];

			if (m_Distance[path.get(i - 1)][path.get(j)] > v
					|| m_Distance[path.get(j)][path.get(i + 1)] > v
					|| m_Distance[path.get(j - 1)][path.get(i)] > v
					|| m_Distance[path.get(i)][path.get(j + 1)] > v) {
				scoreTwo = scoreOne + 1;
			}

		}
		delta = scoreTwo - scoreOne;

		return delta;
	}

	protected void swapElements(List<Integer> path, int j, int i) {
		int temp = path.get(j);
		path.set(j, path.get(i));
		path.set(i, temp);
	}

	private boolean valid_insert(int last_index, List<Integer> long_path, List<Integer> short_path) {
		boolean output = false;
		double size_next_edge;
		double input_distance = caluculate_path_distance(short_path);
		if (last_index < set_number - 1) {
			size_next_edge = m_Distance[long_path.get(last_index)][long_path.get(last_index + 1)];
			if (size_next_edge <= tMax - input_distance) {
				output = true;
				short_path.add(long_path.get(last_index + 1));
			}
		}


		// This function return boolean value just for verification
		return output;
	}

private void calculate_granularity() {
	v=parameter_B*(z_prime/(set_number+m));
	System.out.println("Granularity is "+ v);
}

public void compare_results(List<Integer> path) {
	double score =caluculate_path_profit(path);
	System.out.println("the score before comparision "+ score);
	System.out.println("the best_score before comparision "+ best_score_solution);
	if (score > best_score_solution) {
		best_score_solution = score;
		verify_clear_list(best_solution);
		best_solution.addAll(path);

	}
	path.clear();

}

public List<Integer> return_clusters_path_for_escape(List<Integer> path) {
	int a;
	List<Integer> clusters = new ArrayList<Integer>();
	for (int i = 0; i < path.size(); i++) {
		a = points_cluster[path.get(i)];
		clusters.add(a);
	}
	clusters_used = clusters.size();
	System.out.println("clusters_used " + clusters_used);
	System.out.println("clusters_used are "+clusters);
	clusters.addAll(randomSet.subList(clusters.size(), set_number));
	System.out.println("clusters_used and not used are  "+clusters);
	return clusters;
}

public void escape(List<Integer> list) {
	int first_index = random_first_index(clusters_used);
	int second_index = random_second_index(set_number, clusters_used);
	List<Integer> removed_list = new ArrayList<Integer>();
	removed_list.addAll(list.subList(first_index, second_index + 1));
//	System.out.println(removed_list);
	Collections.shuffle(removed_list);
//	System.out.println(removed_list);
	list.subList(first_index, second_index + 1).clear();
	list.addAll(1, removed_list);
//	System.out.println(list);
	Collections.shuffle(list.subList(removed_list.size() + 1, list.size()));
	System.out.println("The list after ecape "+list);
	randomSet.clear();
    randomSet.addAll(list);
    System.out.println("randomSet after escape "+randomSet);
}

private int random_first_index(int max) {
	int selected;
	Random rand = new Random();
	selected = rand.nextInt(max) + 1;
//	System.out.println(selected);
	return selected;
}


private int random_second_index(int list_size, int min) {
	int selected;
	Random rand = new Random();
	if (list_size - min - 1 == 0) {
		selected = min;
	} else {
		selected = rand.nextInt(list_size - min - 1) + min + 1;
	}

//	System.out.println(selected);
	return selected;
}

}
