package opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class ParticleMesh {

	private int vaoId;
	private int textureVboId;
	private int vertexVboId;
	private int indexVboId;
	private int vertexCount;
	private Texture texture;
	
	public ParticleMesh(Texture texture) {
		this.texture = texture;
		FloatBuffer textureBuffer = null;
		FloatBuffer verticesBuffer = null;
		IntBuffer indexBuffer = null;
		float[] texCoords = {0f, 1.0f, 0f, 0f, 1.0f, 0f, 1.0f, 1.0f};
		float[] vertices = {-0.5f, -0.5f, 0f, -0.5f, 0.5f, 0f, 0.5f, 0.5f, 0f, 0.5f, -0.5f, 0f};
		int[] indices = {2, 3, 0, 0, 1, 2};
		vertexCount = indices.length;
		try  {
			vaoId = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoId);
			
			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
			verticesBuffer.put(vertices).flip();
			vertexVboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
			
			indexBuffer = MemoryUtil.memAllocInt(indices.length);
			indexBuffer.put(indices).flip();
			indexVboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
			
			textureVboId = GL30.glGenBuffers();
			
			textureBuffer = MemoryUtil.memAllocFloat(texCoords.length);
			textureBuffer.put(texCoords).flip();
			textureVboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texCoords, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
			
			GL30.glBindVertexArray(0);
		} finally {
			if(textureBuffer != null)
				MemoryUtil.memFree(textureBuffer);
			if(verticesBuffer != null)
				MemoryUtil.memFree(verticesBuffer);
			if(indexBuffer != null)
				MemoryUtil.memFree(indexBuffer);
		}
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public int getTextureVboId() {
		return textureVboId;
	}
	
	public void cleanup() {
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(textureVboId);
		GL15.glDeleteBuffers(vertexVboId);
		GL15.glDeleteBuffers(indexVboId);
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}
	
	public void render() {
		GL30.glBindVertexArray(vaoId);
		GL30.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		
		GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getId());
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
}
