package opengl;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

import util.FileReader;

public class Model {
	
	private Scanner scanner;
	private String source;
	
	private ArrayList<Float> vertices;
	private ArrayList<String> faces;
	private ArrayList<Float> normals;
	private ArrayList<Float> textures;
	private float[] colours;
	private float[] vertex;
	private float[] texture;
	private float[] normal;
	private int[] face;
	private int[] vertexIndex;

	private String deLimiterFloat = " ";
	private String deLimiterExp = "e";
	private String deLimiterFace = "/";
	
	private Pattern patternFloat;
	private Pattern patternExp;
	private Pattern patternFace;
	private float tempFloat;
	private int tempInt;
	
	private String line;
	private String[] lineStrings;
	private String[] floatStrings;
	private String type;
	
	private boolean hasTextures = false;
	private boolean hasNormals = false;
	
	public Model(String objFile) throws Exception {
		source = FileReader.getContents(objFile);
		scanner = new Scanner(source);
		patternFloat = Pattern.compile(deLimiterFloat);
		patternExp = Pattern.compile(deLimiterExp);
		patternFace = Pattern.compile(deLimiterFace);
		vertices = new ArrayList<Float>();
		faces = new ArrayList<String>();
		normals = new ArrayList<Float>();
		textures = new ArrayList<Float>();
		genVertexArray();
	}
	
	public void genVertexArray() {
		while(scanner.hasNextLine()) {
			line = scanner.nextLine();
			type = line.length() <= 1 ? line : line.substring(0, 2);
			if(type.contentEquals("v ")) {
				lineStrings = patternFloat.split(line.substring(2));
				for(String s: lineStrings) {
					if(Pattern.matches(".*e.*", s)) {
						floatStrings = patternExp.split(s);
						tempFloat = Float.parseFloat(floatStrings[0])*((float) Math.pow(10, Float.parseFloat(floatStrings[1])));
					}
					else {
						tempFloat = Float.parseFloat(s);
					}
					vertices.add(tempFloat);
				}
			}
			
			else if(type.contentEquals("vn") || type.contentEquals("vt")) {
				
				lineStrings = patternFloat.split(line.substring(3));
				for(String s: lineStrings) {
					tempFloat = Float.parseFloat(s);
					if(type.contentEquals("vn")) {
						normals.add(tempFloat);
						if(!hasNormals)
							hasNormals = true;
					}
					else {
						textures.add(tempFloat);
						if(!hasTextures)
							hasTextures = true;
					}
				}
			}
			else if(line.charAt(0) == 'f') {
				lineStrings = patternFloat.split(line.substring(2));
				for(String s: lineStrings) {
					faces.add(s);
				}
			}
		}
		vertex = new float[vertices.size()];
		normal = new float[faces.size()*3];
		texture = new float[faces.size()*2];
		vertexIndex = new int[faces.size()];
		for(int i = 0; i < vertex.length; i++) {
			vertex[i] = vertices.get(i);
		}
		
//		int count = 0;
//		for(String s: faces) {
//			lineStrings = patternFace.split(s);
//			
//			int vIndex = Integer.parseInt(lineStrings[0]);
//			vertexIndex[count] = vIndex-1;
//			count++;
//		}
		int countNormals = 0;
		int countTextures = 0;
		for(int i = 0; i < faces.size(); i++) {
			lineStrings = patternFace.split(faces.get(i));
			int vIndex = Integer.parseInt(lineStrings[0]);
			vertexIndex[i] = vIndex-1;
			if(hasNormals) {
				int nIndex = Integer.parseInt(lineStrings[2]);
				normal[(vIndex-1)*3] = normals.get((nIndex-1)*3);
				normal[(vIndex-1)*3+1] = normals.get((nIndex-1)*3+1);
				normal[(vIndex-1)*3+2] = normals.get((nIndex-1)*3+2);
				countNormals += 3;
			}
			if(hasTextures) {
				int tIndex = Integer.parseInt(lineStrings[1]);
				texture[(tIndex-1)*2] = textures.get((tIndex-1)*2);
				texture[(tIndex-1)*2+1] = textures.get((tIndex-1)*2+1);
				countTextures += 2;
			}
//			System.out.println(vertexIndex[i]);
//			System.out.println(normal[countNormals-3]);
//			System.out.println(normal[countNormals-2]);
//			System.out.println(normal[countNormals-1]);
//			System.out.println(texture[countTextures-2]);
//			System.out.println(texture[countTextures-1]);
//			System.out.println("\n");
		}
	}
	
	public float[] getColours() {
		return colours;
	}
	
	public float[] getVertices() {
		return vertex;
	}
	
	public int[] getFaces() {
		return face;
	}
	
	public float[] getNormals() {
		return normal;
	}
	
	public float[] getTextures() {
		return texture;
	}
	
	public int[] getIndices() {
		return vertexIndex;
	}
}