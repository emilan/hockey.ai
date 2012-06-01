package core;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class PlayerPath {
	Vector[] locations = new Vector[256];
	
	public static final double CLUB_LENGTH = 42;

	PlayerPath(String fileName) throws IOException {
		ArrayList<Vector> points = new ArrayList<Vector>();
		ArrayList<Integer> positions = new ArrayList<Integer>();
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String strLine;
		while ((strLine = br.readLine()) != null) {
			Scanner lineScan = new Scanner(strLine);
			lineScan.useLocale(Locale.US);
			double x = lineScan.nextDouble();
			double y = lineScan.nextDouble();
			points.add(new Vector(x, y));
			
			if (lineScan.hasNextInt())
				positions.add(lineScan.nextInt());
			else positions.add(-1);			
		}
		
		br.close();
		in.close();
		fstream.close();
		
		if (points.size() < 2)
			throw new RuntimeException("Too few points in file");
		
		Vector[] relative = new Vector[points.size()];
		double[] lengths = new double[points.size()];
		
		lengths[0] = 0;
		for (int i = 1; i < points.size(); i++) {
			relative[i - 1] = points.get(i).subtract(points.get(i - 1));
			lengths[i] = lengths[i - 1] + relative[i - 1].norm();
		}
		double totalLength = lengths[points.size() - 1];
		
		positions.set(0, 0);
		positions.set(positions.size() - 1, 255);
		for (int i = 1; i < points.size() - 1; i++)
			if (positions.get(i) == -1)
				positions.set(i, (int)(lengths[i] / totalLength * 255));
		
		int position = 0;
		for (int i = 0; i < positions.size() - 1; i++) {
			for (; position < positions.get(i + 1); position++) {
				double f = (1.0 * position - positions.get(i)) / (positions.get(i + 1) - positions.get(i));
				this.locations[position] = points.get(i).add(relative[i].multiply(f));
			}
		}
		this.locations[255] = points.get(points.size() - 1);
	}

	public Vector getCoordinate(int pos) {
		if (pos > 255 || pos < 0)
			return null;
		
		return locations[pos];
	}

	public int getClosestPosition(Vector vec) {
		double minDistance = Double.MAX_VALUE;
		int minDistanceIndex = -1;
		for (int i = 0; i < 255; i++) {
			double distance = locations[i].subtract(vec).normSquared();
			if (distance < minDistance) {
				minDistance = distance;
				minDistanceIndex = i;
			}
		}
		return minDistanceIndex;
	}
}
