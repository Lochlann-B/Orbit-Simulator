package opengl;

//import org.joml.Math;
//import org.joml.Matrix4f;
//import org.joml.Vector3f;
//import org.joml.Vector4f;

import maths.Matrix4f;
import maths.Vector3f;
import maths.Vector4f;
import app.AppItem;
import input.Camera;

public class Transformation {
	
	private Matrix4f projectionMatrix;
	private Matrix4f worldMatrix;
	private Matrix4f viewMatrix;
	private Shader shader;
	
	public Transformation(Shader shader) {
		this.shader = shader;
		
		projectionMatrix = new Matrix4f();
		worldMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
	}
	
	public Matrix4f getProjectionMatrix(float fov, float width, float height, float near, float far) {
		//projectionMatrix.setPerspective(fov, width/height, near, far);
		projectionMatrix = Matrix4f.getProjectionMatrix(fov, width, height, near, far);
		return projectionMatrix;
	}
	
	public Matrix4f getViewMatrix(Camera camera) {
		Vector3f cameraPos = camera.getPosition();
		//Vector3f cameraRot = camera.getRotation();
		viewMatrix.setIdentity();
		viewMatrix = viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		return viewMatrix;
	}
	
	public Matrix4f getModelViewMatrix(AppItem appitem, Matrix4f viewMatrix) {
		float rotX = (float) Math.toRadians(-appitem.getRotation().x);
		float rotY = (float) Math.toRadians(-appitem.getRotation().y);
		float rotZ = (float) Math.toRadians(-appitem.getRotation().z);
		Vector3f rotation = new Vector3f(rotX,rotY,rotZ);
		Matrix4f copyView = new Matrix4f(viewMatrix);
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		modelMatrix = modelMatrix.translate(appitem.getPosition()).rotate(rotation).scale(appitem.getScale());
		//return copyView.mul(modelMatrix);
		return copyView.Multiply(modelMatrix);
	}
	
	public Matrix4f getParticleModelViewMatrix(Particle particle, Matrix4f viewMatrix) {
		Matrix4f copyView = new Matrix4f(viewMatrix);
		Matrix4f particleModelMatrix = new Matrix4f();
		particleModelMatrix.setIdentity();
		particleModelMatrix = particleModelMatrix.translate(particle.getPosition()).scale(particle.getScale());
		//return copyView.mul(particleModelMatrix);
		return copyView.Multiply(particleModelMatrix);
	}
	
	public void PointLightTransform(PointLight pointLight) {
		//Copy instance of pointLight and change into view coordinates, and upload to shader program
		PointLight copyPointLight = new PointLight(pointLight);
		Matrix4f lightModelMatrix = new Matrix4f();
		lightModelMatrix.setIdentity();
		lightModelMatrix = lightModelMatrix.translate(pointLight.getPosition());
		Vector4f pl = new Vector4f(copyPointLight.getPosition(), 1f);
		//pl.mul(lightModelMatrix).mul(viewMatrix);
		Vector4f pos = lightModelMatrix.Multiply(viewMatrix).Multiply(pl);
		//copyPointLight.setPosition(new Vector3f(pl.x, pl.y, pl.z));
		copyPointLight.setPosition(new Vector3f(pos.x, pos.y, pos.z));
		shader.setUniform("pointLight", copyPointLight);
	}
	
}
