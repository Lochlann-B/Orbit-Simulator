package maths;

public class Matrix4f {
	
	private float[][] m = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};

	public Matrix4f() {}
	
	public Matrix4f(Matrix4f matrix) {
		m = matrix.getArray().clone();
	}
	
	public Matrix4f(float[][] vals) {
		m = vals.clone();
	}
	
	public Matrix4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		m[0][0] = m00;
		m[0][1] = m01;
		m[0][2] = m02;
		m[0][3] = m03;
		m[1][0] = m10;
		m[1][1] = m11;
		m[1][2] = m12;
		m[1][3] = m13;
		m[2][0] = m20;
		m[2][1] = m21;
		m[2][2] = m22;
		m[2][3] = m23;
		m[3][0] = m30;
		m[3][1] = m31;
		m[3][2] = m32;
		m[3][3] = m33;
	}
	
	public void setIdentity() {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(i == j) {
					m[i][j] = 1f;
				} else {
					m[i][j] = 0f;
				}
			}
		}
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
		m[3][col] = values[3];
	}
	
	public Matrix4f Multiply(Matrix4f rightMatrix) {
		float[][] out = new float[4][4];
		float[][] left = m.clone();
		float[][] right = rightMatrix.getArray().clone();
		
		//Multiply left's rows by right's columns
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				out[i][j] = left[i][0]*right[0][j] + left[i][1]*right[1][j] + left[i][2]*right[2][j] + left[i][3]*right[3][j];
			}
		}
		
		return new Matrix4f(out);
	}
	
	public Vector4f Multiply(Vector4f rightVector) {
		return new Vector4f(m[0][0] * rightVector.x + m[0][1] * rightVector.y + m[0][2] * rightVector.z + m[0][3] * rightVector.w, 
				m[1][0] * rightVector.x + m[1][1] * rightVector.y + m[1][2] * rightVector.z + m[1][3] * rightVector.w,
				m[2][0] * rightVector.x + m[2][1] * rightVector.y + m[2][2] * rightVector.z + m[2][3] * rightVector.w,
				m[3][0] * rightVector.x + m[3][1] * rightVector.y + m[3][2] * rightVector.z + m[3][3] * rightVector.w
				);		
	}
	
	public Matrix4f getInverse() throws Exception {
		//Get the 3x3 matrices in order to obtain cofactors and determinant
		Matrix3f[] matrices = new Matrix3f[16];
		boolean incX = false;
		for(int i = 0; i < 16; i++) {
			float[][] tempM = new float[3][3];
			int countX = 0;
			int countY = 0;
			for(int x = 0; x < 4; x++) {
				incX = false;
				for(int y = 0; y < 4; y++) {
					if(x != i/4 && y != i%4) {
						tempM[countX][countY] = m[x][y];
						countY++;
						incX = true;
					}
				}
				countY = 0;
				if(incX)
					countX++;
			}
			matrices[i] = new Matrix3f(tempM);
		}
		
		float determinant = getDeterminant(matrices[0], matrices[1], matrices[2], matrices[3]);
		if(determinant == 0) {
			throw new Exception("Cannot find inverse! (Determinant = 0)");
		}
		
		//Obtain matrix of cofactors (determinants of row and column eliminated matrices)
		float[][] cofactorArray = new float[4][4];
		int sixteenCount = 0;
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				cofactorArray[i][j] = (float) Math.pow(-1, i+j) * matrices[sixteenCount].getDeterminant();
				sixteenCount++;
			}
		}
		
		//Obtain adjoint and apply A^-1 = adj(A)/det(A)
		Matrix4f adjoint = new Matrix4f(cofactorArray).getTranspose();
		adjoint.mul(1/determinant);
		return adjoint;
	}
	
	public void mul(float f) {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				m[i][j] *= f;
			}
		}
	}
	
	public Matrix4f scale(float f) {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(i == j && i != 3 && j != 3)
					m[i][j] *= f;
			}
		}
		return this;
	}
	
	public Matrix4f translate(Vector3f offset) {
		Matrix4f temp = new Matrix4f();
		temp.setIdentity();
		temp.setValue(0, 3, offset.x);
		temp.setValue(1, 3, offset.y);
		temp.setValue(2, 3, offset.z);
		return temp.Multiply(this);
	}
	
	public Matrix4f translate(float x, float y, float z) {
		Matrix4f temp = new Matrix4f();
		temp.setIdentity();
		temp.setValue(0, 3, x);
		temp.setValue(1, 3, y);
		temp.setValue(2, 3, z);
		return temp.Multiply(this);
	}
	
	public Matrix4f rotate(Vector3f rotation) {
		float cX = (float) Math.cos(rotation.x);
		float cY = (float) Math.cos(rotation.y);
		float cZ = (float) Math.cos(rotation.z);
		float sX = (float) Math.sin(rotation.x);
		float sY = (float) Math.sin(rotation.y);
		float sZ = (float) Math.sin(rotation.z);
		Matrix4f xRotation = new Matrix4f(1f, 0f, 0f, 0f, 0f, cX, -sX, 0f, 0f, sX, cX, 0f, 0f, 0f, 0f, 1f);
		Matrix4f yRotation = new Matrix4f(cY, 0f, sY, 0f, 0f, 1f, 0f, 0f, -sY, 0f, cY, 0f, 0f, 0f, 0f, 1f);
		Matrix4f zRotation = new Matrix4f(cZ, -sZ, 0f, 0f, cZ, sZ, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f);
		Matrix4f XYZRotation = xRotation.Multiply(yRotation.Multiply(zRotation));
		XYZRotation.Multiply(this);
		return this;
	}
	
	public static Matrix4f getProjectionMatrix(float fov, float width, float height, float near, float far) {
		Matrix4f projectionMatrix = new Matrix4f();
		float aspect = width/height;
		projectionMatrix.setValue(0, 0, 1/(aspect*(float)Math.tan(fov/2)));
		projectionMatrix.setValue(1, 1,(float) (1/Math.tan(fov/2)));
		projectionMatrix.setValue(2, 2, -(near + far)/(far - near));
		projectionMatrix.setValue(3, 2, -2*(near * far)/(far - near));
		projectionMatrix.setValue(2, 3, -1);
		return projectionMatrix;
	}
	
	//Method for smtn else idr check transformation class for deets
	
	public Matrix4f getTranspose() {
		//Reflect matrix by its leading diagonal
		float[][] transpose = new float[4][4];
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				transpose[i][j] = m[j][i];
			}
		}
		return new Matrix4f(transpose);
	}
	
	private float getDeterminant(Matrix3f m00, Matrix3f m01, Matrix3f m02, Matrix3f m03) {
		return m[0][0]*m00.getDeterminant() - m[0][1]*m01.getDeterminant() + m[0][2]*m02.getDeterminant() - m[0][3]*m03.getDeterminant();
	}
	
	
	public void setMatrix(Matrix4f matrix) {
		m = matrix.getArray().clone();
	}
	
	public float[][] getArray() {
		return m;
	}
	
	public float get(int row, int col) {
		return m[row][col];
	}
	
	public float[] getSingleArray() {
		float[] f = new float[16];
		int count = 0;
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				f[count] = m[i][j];
				count++;
			}
		}
		return f;
	}
	
}
