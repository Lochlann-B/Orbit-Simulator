package opengl;

import java.util.Scanner;
import java.util.regex.Pattern;

//import org.joml.Vector4f;
import maths.Vector4f;

import util.FileReader;

public class Material {

	private Vector4f ambience;
	private Vector4f diffuse;
	private Vector4f specular;
	private float reflectance;
	
	private String source;
	private String line;
	private String type;
	private Scanner scanner;
	
	private String deLimiterSpace = " ";
	private String[] lineStrings;
	private float[] floats = new float[3];
	
	private Pattern patternSpace;
	
	public Material(String file) throws Exception {
		ambience = new Vector4f();
		diffuse = new Vector4f();
		specular = new Vector4f();
		setMaterial(file);
	}
	
	public void setMaterial(String file) throws Exception {
		//Reads .mtl file and gets values from each line to give to ambeince, diffuse and specular vectors.
		source = FileReader.getContents(file);
		scanner = new Scanner(source);
		patternSpace = Pattern.compile(deLimiterSpace);
		
		while(scanner.hasNextLine()) {
			line = scanner.nextLine();
			type = line.length() < 2 ? line : line.substring(0, 2);
			if(type.contentEquals("Ka") || type.contentEquals("Kd") || type.contentEquals("Ks")) {
				lineStrings = patternSpace.split(line.substring(3));
				for(int i = 0; i < lineStrings.length; i++) {
					floats[i] = Float.parseFloat(lineStrings[i]);
				}
			}
			else if(type.contentEquals("Ns")) {
				reflectance = Float.parseFloat(line.substring(3));
			}

			switch(type) {
				case "Ka":
					ambience = new Vector4f(floats[0], floats[1], floats[2], 0);
					break;
				case "Kd":
					diffuse = new Vector4f(floats[0], floats[1], floats[2], 0);
					break;
				case "Ks":
					specular = new Vector4f(floats[0], floats[1], floats[2], 0);
					break;
			}
		}
//		System.out.println("Ambience: " + String.valueOf(ambience.x) + ", " + String.valueOf(ambience.y) + ", " + String.valueOf(ambience.z) + ", " + String.valueOf(ambience.w));
//		System.out.println("Diffuse: " + String.valueOf(diffuse.x) + ", " + String.valueOf(diffuse.y) + ", " + String.valueOf(diffuse.z) + ", " + String.valueOf(diffuse.w));
//		System.out.println("Specular: " + String.valueOf(specular.x) + ", " + String.valueOf(specular.y) + ", " + String.valueOf(specular.z) + ", " + String.valueOf(specular.w));
//		System.out.println("Reflectance: " + String.valueOf(reflectance));
	}
	
	public void setAmbience(float r, float g, float b) {
		ambience.x = r;
		ambience.y = g;
		ambience.z = b;
	}
	
	public void setDiffuse(float r, float g, float b) {
		diffuse.x = r;
		diffuse.y = g;
		diffuse.z = b;
	}
	
	public void setSpecular(float r, float g, float b) {
		specular.x = r;
		specular.y = g;
		specular.z = b;
	}
	
	public void setReflectance(float r) {
		reflectance = r;
	}
	
	public Vector4f getDiffuse() {
		return diffuse;
	}
	
	public Vector4f getSpecular() {
		return specular;
	}
	
	public Vector4f getAmbience() {
		return ambience;
	}
	
	public float getReflectance() {
		return reflectance;
	}
	
}