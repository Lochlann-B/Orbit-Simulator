package opengl;

import java.util.ArrayList;

/*
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
*/
import maths.*;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import app.AppItem;
import app.ParticleGenerator;
import app.Planet;
import gui.Gui;
import input.Camera;
import input.MouseInput;
import util.FileReader;

public class Renderer {

	private Shader shader;
	private ParticleShader particleShader;
	private Matrix4f projectionMatrix;
	private static final float fov = (float) Math.toRadians(50.0);
	private static final float z_near = 0.01f;
	private static final float z_far = 10000.0f;
	private Transformation transformation;
	private int[] windowCoords = {0,0,0,0};
	private Vector4f viewMousePos;
	private Window window;
	private MouseInput mouseInput;
	private Camera camera;
	private Matrix4f inverseProjection;
	
	public Renderer() {}
	
	public void init(Window window, MouseInput mouseInput, Camera camera) throws Exception {
		// This function allows us to use our shader program with
		// OpenGL and creates our vao and vbo and assign our float array to it.
		// Also defines how we interpret our float array with use of an attrib array
		viewMousePos = new Vector4f(0,0,0,0);
		shader = new Shader();
		shader.createVertexShader(FileReader.getContents("/opengl/vertex.vs"));
		shader.createFragmentShader(FileReader.getContents("/opengl/fragment.fs"));
		shader.link();
		shader.createUniform("projectionMatrix");
		shader.createUniform("worldMatrix");
		
		shader.createUniform("texture_sampler");
		
		shader.createUniform("colour");
		shader.createUniform("useColour");
		
		shader.createMaterialUniform("material");
		shader.createPointLightUniform("pointLight");
		shader.createUniform("specularPower");
		
		transformation = new Transformation(shader);
		
		particleShader = new ParticleShader();
		particleShader.createVertexShader(FileReader.getContents("/opengl/particleV.vs"));
		particleShader.createFragmentShader(FileReader.getContents("/opengl/particleF.fs"));
		particleShader.link();
		particleShader.createUniform("projectionMatrix");
		particleShader.createUniform("worldMatrix");
		particleShader.createUniform("texture_sampler");
		particleShader.createUniform("colour");
		
		this.window = window;
		this.camera = camera;
		this.mouseInput = mouseInput;
		
		//projectionMatrix = transformation.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);
		//Matrix4f copyProjectionMatrix = new Matrix4f(projectionMatrix);
		//inverseProjection = copyProjectionMatrix.invert();
		
		projectionMatrix = Matrix4f.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);
		inverseProjection = projectionMatrix.getInverse();
		
		//for(int i = 0; i < 4; i++) {
		//	
		//	System.out.println(projectionMatrix.get(i, 0) + ",blep " + projectionMatrix.get(i, 1) + ", " + projectionMatrix.get(i, 2) + ", " + projectionMatrix.get(i, 3));
		//}

	}
	
	public void render(ArrayList<Planet> planets, PointLight pointLight, Texture texture, ArrayList<ParticleGenerator> pGenList, Gui gui) throws Exception {
		clear();
		if (window.getResizeStatus()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResizeStatus(false);
			
			//projectionMatrix = transformation.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);
			//Matrix4f copyProjectionMatrix = new Matrix4f(projectionMatrix);
			//inverseProjection = copyProjectionMatrix.invert();
			
			projectionMatrix = Matrix4f.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);
			inverseProjection = projectionMatrix.getInverse();
		}
		
		Matrix4f viewMatrix = transformation.getViewMatrix(camera);

		
		
		

		
		
		//Convert mouse coordinates to world-view coordinates
		windowCoords[2] = window.getWidth();
		windowCoords[3] = window.getHeight();
		Vector4f mousePos = new Vector4f((float) mouseInput.getCurrentPos().x, (float) mouseInput.getCurrentPos().y, camera.getPosition().z, 1);
		mousePos.set((float) (mousePos.x/(0.5*window.getWidth()))-1, (float) (mousePos.y/(0.5*window.getHeight()))-1, mousePos.z, mousePos.w);
		//viewMousePos = projectionMatrix.unproject(mousePos, windowCoords, viewMousePos);
		//Matrix4f inverseProjection = projectionMatrix.invert();
		
		
		//viewMousePos = mousePos.mul(inverseProjection);
		viewMousePos = inverseProjection.Multiply(mousePos);
		
		viewMousePos.set(viewMousePos.x*camera.getPosition().z + camera.getPosition().x, (viewMousePos.y*camera.getPosition().z - camera.getPosition().y), 0, 0);
		//pGenList.get(0).setPosition(viewMousePos.x, -viewMousePos.y, 0);
		//pGenList.get(0).setInitialColour(0f, 0f, 3.0f);
		//pGenList.get(0).setFinalColour(0f, 3.0f, 2.0f);
		GL20.glUseProgram(shader.getShaderId());
		shader.bind();
		
		//shader.setUniform("pointLight", pointLight);
		
		//projectionMatrix = transformation.getProjectionMatrix(fov, window.getWidth(), window.getHeight(), z_near, z_far);

		shader.setUniform("projectionMatrix", projectionMatrix);
		
		shader.setUniform("texture_sampler", 0);
		
		shader.setUniform("specularPower", 40f);
		
		for(Planet planet : planets) {	
			

			
			if(!planet.isExploding()) {

				AppItem object = planet.getAppItem();
				Matrix4f worldMatrix = transformation.getModelViewMatrix(object, viewMatrix).getTranspose();
				//System.out.println("WORLD MATRIX: ");
				//for(int i = 0; i < 4; i++) {
				//	System.out.println(worldMatrix.get(i, 0) + ", " + worldMatrix.get(i, 1) + ", " + worldMatrix.get(i, 2) + ", " + worldMatrix.get(i, 3));
				//	//System.out.println(projectionMatrix.get(i, 0) + ", " + projectionMatrix.get(i, 1) + ", " + projectionMatrix.get(i, 2) + ", " + projectionMatrix.get(i, 3));
				//}
	
				transformation.PointLightTransform(pointLight);
				if(object.getColourStatus() == 1) {
					shader.setUniform("colour", object.getColour());
				}
				/*
				if(object.getScale() != 10) {
					object.setPosition(viewMousePos.x, -viewMousePos.y, 0);
				}
				*/
		//			double length = Math.sqrt(camera.getPosition().z*camera.getPosition().z + Math.pow(object.getPosition().x - camera.getPosition().x,  2));
	//			double viewScale = object.getScale()*Math.cos(90 + Math.asin(object.getScale()/length) - Math.asin(camera.getPosition().z/length));
				//camera.getPosition().z*Math.tan(Math.asin(object.getScale()/camera.getPosition().z))
				
				shader.setUniform("worldMatrix", worldMatrix);
				
				shader.setUniform("material", object.getMesh().getMaterial());
				shader.setUniform("useColour", object.getColourStatus());
				if(planet.isSelected()) {
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					object.getMesh().render(object.getColourStatus());
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
					

					drawEllipse(planet, projectionMatrix, worldMatrix, viewMatrix, planets, gui);
					
				}
				else {
					object.getMesh().render(object.getColourStatus());
				}
			}
		}
		
		shader.unbind();

		GL20.glUseProgram(particleShader.getShaderId());
		particleShader.bind();
		
		particleShader.setUniform("projectionMatrix", projectionMatrix);
		particleShader.setUniform("texture_sampler", 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		//any thing from here goes in a loop of all particles...
		for(int j = 0; j < pGenList.size(); j++) {
			for(int i = 0; i < pGenList.get(j).getList().size(); i++) {
			Particle particle = pGenList.get(j).getList().get(i);
				Matrix4f particleWorldMatrix = transformation.getParticleModelViewMatrix(particle, viewMatrix).getTranspose();
				particleShader.setUniform("colour", particle.getColour());
				particleShader.setUniform("worldMatrix", particleWorldMatrix);
				particle.getMesh().render();
			}
		}
		//...until here
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_BLEND);
		particleShader.unbind();
		
		GL11.glDepthMask(true);
		
	}
	
	private void drawEllipse(Planet p, Matrix4f projectionMatrix, Matrix4f worldMatrix, Matrix4f viewMatrix, ArrayList<Planet> planets, Gui gui) {
		Vector3f focalPoint = new Vector3f(planets.get(p.getParent().getID()).getAppItem().getPosition());
		float cx = (float) (focalPoint.x - p.getEccentricity()*p.getSemiMajorDiameter()*Math.cos(p.getAngle()));
		float cy = (float) (focalPoint.y - p.getEccentricity()*p.getSemiMajorDiameter()*Math.sin(p.getAngle()));
		float ax = (float) (cx + p.getSemiMajorDiameter()*Math.cos(p.getAngle()));
		float ay = (float) (cy + p.getSemiMajorDiameter()*Math.sin(p.getAngle()));
		
		Vector4f center = new Vector4f(cx, cy, 0, 1);
		Vector4f apoapsis = new Vector4f(ax, ay, 0, 1);

		Vector4f transformedCenter = projectionMatrix.getTranspose().Multiply(viewMatrix.Multiply(center));
		Vector4f transformedapoapsis = projectionMatrix.getTranspose().Multiply(viewMatrix.Multiply(apoapsis));

		float tcx = 0.5f*window.getWidth()*((transformedCenter.x/transformedCenter.w)+1);
		float tcy = -0.5f*window.getHeight()*((transformedCenter.y/transformedCenter.w)-1);
		float tax = 0.5f*window.getWidth()*((transformedapoapsis.x/transformedapoapsis.w)+1);
		float tay = -0.5f*window.getHeight()*((transformedapoapsis.y/transformedapoapsis.w)-1);
		
		float xAxis = tax - tcx;
		float yAxis = tay - tcy;
		float semiMajor = (float) Math.sqrt(xAxis*xAxis + yAxis*yAxis);
	
		gui.drawEllipse(semiMajor, (float) (semiMajor*Math.sqrt(1-p.getEccentricity()*p.getEccentricity())), tcx, tcy, (float) p.getAngle());
	}
	
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}
	
	public Vector2f getWorldMousePosition() {
		windowCoords[2] = window.getWidth();
		windowCoords[3] = window.getHeight();
		Vector4f mousePos = new Vector4f((float) mouseInput.getCurrentPos().x, (float) mouseInput.getCurrentPos().y, camera.getPosition().z, 1);
		mousePos.set((float) (mousePos.x/(0.5*window.getWidth()))-1, (float) (mousePos.y/(0.5*window.getHeight()))-1, mousePos.z, mousePos.w);
		viewMousePos = inverseProjection.Multiply(mousePos);
		
		viewMousePos.set(viewMousePos.x*camera.getPosition().z + camera.getPosition().x, (viewMousePos.y*camera.getPosition().z - camera.getPosition().y), 0, 0);
		return new Vector2f(viewMousePos.x, viewMousePos.y);
	}
	
	public void cleanup() {
		if(shader != null)
			shader.cleanup();
		if(particleShader != null)
			particleShader.cleanup();
	}
	
}