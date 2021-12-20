package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import app.AppItem;
import app.Planet;
import opengl.Mesh;

public class LoadSystem {
	
	private static String currentLine;
	private static HashMap<Integer, Integer> pRelationship = new HashMap<Integer, Integer>();
	private static double[] properties = new double[12];
	private static boolean hasNextLine = true;
	private static int count = -1;
	private static Planet currentPlanet;
	
	public static int loadSystem(String location, ArrayList<Planet> pList, Mesh mesh) {
		hasNextLine = true;
		count = -1;
		pRelationship.clear();

		try {
			BufferedReader reader = new BufferedReader(new FileReader("./Systems/" + location + ".txt"));
			pList.clear();
			while(hasNextLine) {
				try {
					currentLine = reader.readLine();
					if(currentLine.charAt(0) == 'p' && currentLine.length() < 2) {
						currentPlanet = new Planet(new AppItem(mesh));
						count = -1;
					}
					if(currentLine.charAt(0) == ',') {
						currentPlanet.setProperties(properties);
						pList.add(currentPlanet.getID(), currentPlanet);
					}
					if(currentLine.charAt(0) == 'E') {
						hasNextLine = false;
						break;
					}
					
					if(currentLine.length() > 3) {
						count++;
						properties[count] = Double.parseDouble(currentLine.substring(3));
					}
					if(count == 1) {
						pRelationship.put((int) properties[0], (int) properties[1]);
					}
					
					
					
				}
				catch(IOException e) {
					hasNextLine = false;
					e.printStackTrace();
				}
			}
			pList.get(0).getAppItem().setPosition(0, 0, 0);
			for(Planet p : pList) {
				p.setParent(pList.get(pRelationship.get(p.getID())));
				if(p.getID() != 0)
					p.move(1);
			}
			
			reader.close();
			return 0;
		}
		catch(IOException e) {
			System.out.println("OOOOOOOOOOOOOO");
			e.printStackTrace();
			return -1;
		}
	}
}
