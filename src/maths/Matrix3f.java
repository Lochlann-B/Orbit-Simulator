package maths;

public class Matrix3f {
	
	private float[][] m = {{0,0,0},{0,0,0},{0,0,0}};

	public Matrix3f() {}
	
	public Matrix3f(Matrix3f matrix) {
		m = matrix.getArray().clone();
	}
	
	public Matrix3f(float[][] vals) {
		m = vals.clone();
	}
	
	public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
		m[0][0] = m00;
		m[0][1] = m01;
		m[0][2] = m02;
		m[1][0] = m10;
		m[1][1] = m11;
		m[1][2] = m12;
		m[2][0] = m20;
		m[2][1] = m21;
		m[2][2] = m22;
	}
	
	public void setValue(int row, int col, float f) {
		m[row][col] = f;
	}
	
	public void setRow(int row, float[] values) {
		m[row] = values;
	}
	
	public void setCol(int col, float[] values) {
		m[0][col] = values[0];
		m[1][col] = values[1];
		m[2][col] = values[2];
	}
	
	public Matrix3f Multiply(Matrix3f rightMatrix) {
		float[][] out = new float[3][3];
		float[][] left = m.clone();
		float[][] right = rightMatrix.getArray().clone();
		
		//Multiply left's rows by right's columns
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				out[i][j] = left[i][0]*right[0][j] + left[i][1]*right[1][j] + left[i][2]*right[2][j];
			}
		}
		
		return new Matrix3f(out);
	}
	
	//Method for inverting
	
	//public Matrix4f getInverse() {
	//	
	//}
	
	//Method for getting projection matrix
	
	//public static Matrix4f getProjectionMatrix() {
	//	
	//}
	
	//Method for smtn else idr check transformation class for deets
	
	//Method for getting a transpose
	
	public Matrix3f getTranspose() {
		//Reflect matrix by its leading diagonal
		float[][] transpose = new float[3][3];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				transpose[i][j] = m[j][i];
			}
		}
		return new Matrix3f(transpose);
	}
	
	//Method for getting a determinant
	
	public float getDeterminant() {
		//Calculated by hand
		return m[0][0]*(m[1][1]*m[2][2]-m[1][2]*m[2][1]) - m[0][1]*(m[1][0]*m[2][2] - m[1][2]*m[2][0]) + m[0][2]*(m[1][0]*m[2][1]-m[1][1]*m[2][0]);
	}
	

	public void setMatrix(Matrix3f matrix) {
		m = matrix.getArray().clone();
	}
	
	public float[][] getArray() {
		return m;
	}
}
