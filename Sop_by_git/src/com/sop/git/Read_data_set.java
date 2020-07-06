package com.sop.git;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Read_data_set {
	// This will reference one line at a time
	public String line = null;
	int set = 12;
	public int matrix[][] = new int[set][];

	public int[][] read(String fileName) {

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			int row = 0;
			List<String> lineContent;

			while ((line = bufferedReader.readLine()) != null && !line.trim().equals("")) {
				lineContent = new ArrayList(Arrays.asList(line.split(" ")));
				matrix[row] = new int[lineContent.size()];
				for (int j = 0; j < lineContent.size(); j++) {

					matrix[row][j] = Integer.parseInt(lineContent.get(j));
				}
				lineContent.clear();
				row++;

			}

			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}

		return matrix;
	}
}
