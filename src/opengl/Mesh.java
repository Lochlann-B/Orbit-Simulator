package opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import maths.Vector3f;

public class Mesh {
	private int vertexVboId;
	private int vaoId;
	private int indexVboId;
	//private int colourVboId;
	private int vertexCount;
	private int normalVboId;
	private int textureVboId;
	private Texture texture;
	private Vector3f colour = new Vector3f(1f,0f,0f);
	private Material material;
	
	public Mesh(float[] vertices, int[] indices, float[] normals, float[] texCoords, Material material) {
		//float[] colours
		
		this.material = material;
		
		FloatBuffer verticesBuffer = null;
		IntBuffer indexBuffer = null;
		FloatBuffer normalsBuffer = null;
		FloatBuffer textureBuffer = null;
		try {
			vertexCount = indices.length;
			//Allocate memory to vertex buffer and assign array to it.
			//Since we have 'put' our array into the vertex buffer
			//we need to change the mode from 'reading' to 'writing' (hence flip).
			//It does this by trimming the buffer and then setting
			//the position to zero.

			
			//Create vao and bind to it
			vaoId = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoId);

			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
			verticesBuffer.put(vertices).flip();
			vertexVboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

			//Same thing but with indices buffer object
			indexBuffer = MemoryUtil.memAllocInt(indices.length);
			indexBuffer.put(indices).flip();
			indexVboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

			//texture coordinates
			textureBuffer = MemoryUtil.memAllocFloat(texCoords.length);
			textureBuffer.put(texCoords).flip();
			textureVboId = GL15.glGenBuffers();
//			GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, textureVboId);
//			GL15.glBufferData(GL31.GL_TEXTURE_BUFFER, texCoords, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texCoords, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

			//normals
			normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
			normalsBuffer.put(normals).flip();
			normalVboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normals, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(2);
			GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
			

			//Unbind the vao
			GL30.glBindVertexArray(0);
		} finally {
			if(verticesBuffer != null) 
				MemoryUtil.memFree(verticesBuffer);
			if(indexBuffer != null) 
				MemoryUtil.memFree(indexBuffer);
			if(textureBuffer != null) 
				MemoryUtil.memFree(textureBuffer);
			if(normalsBuffer != null) 
				MemoryUtil.memFree(normalsBuffer);
		}
		
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
//	public void setColour(float r, float g, float b) {
//		colour.x = r;
//		colour.y = g;
//		colour.z = b;
//		material.setTextureStatus(0);
//	}
	
	public Vector3f getColour() {
		return colour;
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public int getVboId() {
		return vertexVboId;
	}
	
	public int getIndexVboId() {
		return indexVboId;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void cleanup() {
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vertexVboId);
		GL15.glDeleteBuffers(indexVboId);
		GL15.glDeleteBuffers(normalVboId);
		GL15.glDeleteBuffers(textureVboId);
		//GL15.glDeleteBuffers(colourVboId);
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}

	public void render(int colourStatus) {
		GL30.glBindVertexArray(getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		if(colourStatus == 0) {
			GL20.glActiveTexture(GL20.GL_TEXTURE0);
			GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getId());
		}
		GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL20.glDisableVertexAttribArray(0);		
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
	}
}