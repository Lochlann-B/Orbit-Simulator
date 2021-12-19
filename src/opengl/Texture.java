package opengl;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.FileInputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Texture {
	
	private FileInputStream file;
	private PNGDecoder decoder;
	private ByteBuffer buf;
	private int textureId;
	
	public Texture(String filename) throws Exception {
		file = new FileInputStream(filename);
		try {
			decoder = new PNGDecoder(file);
			//Allocate 4 bytes per pixel (RGBA)
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
	
			//2nd arg specifies how many bytes per line
			decoder.decode(buf, decoder.getWidth()*4, PNGDecoder.Format.RGBA); 
			buf.flip();
		}
		finally {
			file.close();
		}
	}
	
	public void createGlTexture() {
		textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexEnvf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);	
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		}
	
	public int getId() {
		return textureId;
	}
	
}