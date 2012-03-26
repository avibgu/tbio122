package math;

import main.Dimensions;

import javax.vecmath.Vector3f;

public class MatrixManipulation {
	private Dimensions dimensions;
	
	public MatrixManipulation(Dimensions dimenstions){
		this.dimensions = dimenstions;
	}
	
	/**
	 * do Lorentz transformation on the given vector.
	 * 
	 * @param vector the vector to activate the lorentz transformation
	 * @param axis the axis
	 * @param degree the degree
	 * 
	 * @return the vector3f after lorentz transformation 
	 */
	public Vector3f LorentzTransformation(Vector3f vector,char axis, double degree)
	{
		double[][] t = new double[][]{{vector.x},{vector.y},{vector.z}};
		double[][] uMatrix = getLorentzUMatrix(axis, degree);
		double[][] ans = matrixMultiply(uMatrix, t);
		Vector3f newVector = new Vector3f();
		newVector.x = new Double(ans[0][0]).floatValue();
		newVector.y = new Double(ans[1][0]).floatValue();
		newVector.z = new Double(ans[2][0]).floatValue();
		
		return newVector;
	}
	
	/** 
    * Return the U matrix for the Lorentz Transformation, see more on
    * <a>http://en.wikipedia.org/wiki/Lorentz_transformation</a> 
    * @param axis the char of the axis: 'x' / 'y' / 'z' only (<b>case sensitive</b>) - doesn't matter if dimension set to 2D 
    * @param degree the degree for rotating around the axis. <b>positive side is counter clockwise</b>
    * @return the U matrix for the Lorentz Transformation if the degree is zero the return
    * an empty matrix in size [0][0]. in case of an error returns null 
    */ 
	private double[][] getLorentzUMatrix(char axis,double degree)
	{
		if (degree == 0)
			return new double[0][0];
		double[][] ans;
		
		if (this.dimensions == Dimensions.TWO)
		{
			ans = new double[][]{{Math.cos(degree),-Math.sin(degree),0},{Math.sin(degree),Math.cos(degree),0},{0,0,0}};
			fixZeros(ans);
			return ans;
		}

		switch (axis)
		{
			case 'x':
				ans = new double[][]{{1,0,0},{0,Math.cos(degree),-Math.sin(degree)},{0,Math.sin(degree),Math.cos(degree)}};
				break;
			case 'y':
				ans = new double[][]{{Math.cos(degree),0,Math.sin(degree)},{0,1,0},{-Math.sin(degree),0,Math.cos(degree)}};
				break;
			case 'z':
				ans = new double[][]{{Math.cos(degree),-Math.sin(degree),0},{Math.sin(degree),Math.cos(degree),0},{0,0,1}};
				break;
			default:
				ans = null;
				break;
		}
		fixZeros(ans);
		return ans;
	}
	
	/**
	 * Matrix multiply.
	 * 
	 * @param A the first Matrix to multiply
	 * @param B the second Matrix to multiply
	 * 
	 * @return the answer for the cross of A x B
	 */
	private double[][] matrixMultiply(double[][] A, double[][] B) {
		if (A[0].length != B.length) //the matrix A & B cannot be multiplied
			throw new RuntimeException("Dimentions of matrices do not match\n"+"A[0].length = "+A[0].length+"\n"+"B.length = "+B.length);
		double[][] ans = new double[A.length][B[0].length];
		int rows = ans.length;
		int columns = ans[0].length;
		int n = B.length;
		for (int i = 0; i < rows; i++) 
		{
			for (int j = 0; j < columns; j++) 
			{
				for (int r=0;r<n;r++)
				{
					ans[i][j] += A[i][r]*B[r][j];
				}
			}
		}
		return ans;
	}
	
	
	/**
	 * Fix zeros. this method round numbers the smallest then 1.0E-10 to zero
	 * 
	 * @param matrix the matrix to fix the numbers in it
	 */
	private void fixZeros(double[][] matrix)
	{
		if (matrix == null)
			return;
		int rows = matrix.length;
		int columns = matrix[0].length;		
		for (int i = 0; i < rows; i++) 
		{
			for (int j = 0; j < columns; j++) 
			{
				if (Math.abs(matrix[i][j]) <= 1.0E-7)
					matrix[i][j] = 0;
			}
		}
	}

}
