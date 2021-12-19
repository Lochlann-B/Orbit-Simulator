package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import app.Planet;

public class SaveSystem {
	
	public static HashMap<Integer, String> property = new HashMap<Integer, String>();
	
	public static void init() {
		property.put(0, "id ");
		property.put(1, "pi ");
		property.put(2, "ma ");
		property.put(3, "ec ");
		property.put(4, "sm ");
		property.put(5, "th ");
		property.put(6, "dr ");
		property.put(7, "an ");
		property.put(8, "ir ");
		property.put(9, "ig ");
		property.put(10, "ib ");
		property.put(11, "is ");
	}
	
	public static void saveSystem(String systemName, ArrayList<Planet> pList) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./Systems/" + systemName + ".txt"));
			for(Planet p : pList) {
				double[] planetProperties = p.getProperties();
				writer.write("p\n");
				for(int i = 0; i < 12; i++) {
					writer.write(property.get(i) + planetProperties[i]);
					writer.newLine();
				}
				writer.write(",");
				writer.newLine();
			}
			writer.write("E");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
