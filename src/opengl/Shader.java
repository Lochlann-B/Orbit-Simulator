package opengl;

import java.util.HashMap;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


//import org.joml.Matrix4f;
//import org.joml.Vector3f;
//import org.joml.Vector4f;

import maths.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class Shader {
	
	private HashMap<String, Integer> uniformMap;
	private final int shaderId;
	private int vertexShaderId;
	private int fragmentShaderId;
	
	public Shader() throws Exception {
		shaderId = GL20.glCreateProgram();
		if(shaderId == 0)
			throw new Exception("OpenGL error: Could not create shader!");
		
		uniformMap = new HashMap<>();
	}
	
	public void createUniform(String uniformName) throws Exception {
		int uniformLocation = GL20.glGetUniformLocation(shaderId, uniformName);
		if(uniformLocation < 0)
			throw new Exception("Cannot find a uniform location for: " + uniformName);
		uniformMap.put(uniformName, uniformLocation);
	}
	
	public void createVertexShader(String shaderSource) throws Exception {
		vertexShaderId = createShader(shaderSource, GL20.GL_VERTEX_SHADER);
	}
	
	public void createFragmentShader(String shaderSource) throws Exception {
		fragmentShaderId = createShader(shaderSource, GL20.GL_FRAGMENT_SHADER);
	}
	
	protected int createShader(String source, int type) throws Exception {
		int GLSLId = GL20.glCreateShader(type);
		if(GLSLId == 0)
			throw new Exception("Error creating shader. Type: " + type);
		
		//Gets source and compiles it
		GL20.glShaderSource(GLSLId, source);
		GL20.glCompileShader(GLSLId);

		//Retrieves information from compilation of shader code
		if(GL20.glGetShaderi(GLSLId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new Exception("Failed to compile shader program. Info: " + GL20.glGetShaderInfoLog(GLSLId));
		}
		
		//Attaches shader to program so it can be used
		GL20.glAttachShader(shaderId, GLSLId);
		
		return GLSLId;
	}
	
	public void link() throws Exception {
		//Links shader program to OpenGL
		GL20.glLinkProgram(shaderId);
		
		if(GL20.glGetProgrami(shaderId, GL20.GL_LINK_STATUS) == 0) 
			throw new Exception("Error linking shader code. Info: " + GL20.glGetProgramInfoLog(shaderId));
		
		if(vertexShaderId != 0)
			GL20.glDetachShader(shaderId, vertexShaderId);
		
		if(fragmentShaderId != 0)
			GL20.glDetachShader(shaderId, fragmentShaderId);
		
		GL20.glValidateProgram(shaderId);
		if(GL20.glGetProgrami(shaderId, GL20.GL_VALIDATE_STATUS) == 0)
			System.err.println("Warning in validating program. Info: " + GL20.glGetProgramInfoLog(shaderId, 1024));
	}
	
	public void setUniform(String uniformName, Matrix4f value) {
		Matrix4f copy = new Matrix4f(value);
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			//for(int i = 0; i < 4; i++) {
			//	for(int j = 0; j < 4; j++) {
			//		fb.put(copy.getArray()[i][j]);
			//	}
			//}
			fb.put(value.getSingleArray()).flip();
			//copy.get(0, fb);
			GL20.glUniformMatrix4fv(uniformMap.get(uniformName), false, fb);
		}
	}
	
	public void setUniform(String uniformName, int value) {
		GL20.glUniform1i(uniformMap.get(uniformName), value);
	}
	
	public void setUniform(String uniformName, float value) {
		GL20.glUniform1f(uniformMap.get(uniformName), value);
	}
	
	public void setUniform(String uniformName, Vector3f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(3);
			fb.put(0, value.x);
			fb.put(1, value.y);
			fb.put(2, value.z);
			//value.get(fb);
			GL20.glUniform3fv(uniformMap.get(uniformName), fb);
		}
	}
	
	public void setUniform(String uniformName, Vector4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(4);
			fb.put(0, value.x);
			fb.put(1, value.y);
			fb.put(2, value.z);
			fb.put(3, value.w);
			//value.get(fb);
			GL20.glUniform4fv(uniformMap.get(uniformName), fb);
		}
	}
	
	public void createPointLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".att.constant");
		createUniform(uniformName + ".att.linear");
		createUniform(uniformName + ".att.exponent");
	}
	
	public void createMaterialUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".ambience");
		createUniform(uniformName + ".specular");
		createUniform(uniformName + ".diffuse");
		createUniform(uniformName + ".reflectance");
	}
	
	public void setUniform(String uniformName, PointLight pointLight) {
		Attenuation att = pointLight.getAttenuation();
		setUniform(uniformName + ".colour", pointLight.getColour());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniform(uniformName + ".intensity", pointLight.getIntensity());
		setUniform(uniformName + ".att.constant", att.getConstant());
		setUniform(uniformName + ".att.linear", att.getLinear());
		setUniform(uniformName + ".att.exponent", att.getExponent());
		
		}
	
	public void setUniform(String uniformName, Material material) {
		setUniform(uniformName + ".ambience", material.getAmbience());
		setUniform(uniformName + ".diffuse", material.getDiffuse());
		setUniform(uniformName + ".specular", material.getSpecular());
		setUniform(uniformName + ".reflectance", material.getReflectance());
	}
	
	public int getShaderId() {
		return shaderId;
	}
	
	public void cleanup() {
		unbind();
		if(shaderId != 0) {
			GL20.glDeleteProgram(shaderId);
		}
	}
	
	public void bind() {
		GL20.glUseProgram(shaderId);
	}
	
	public void unbind() {
		GL20.glUseProgram(0);
	}
	
}
