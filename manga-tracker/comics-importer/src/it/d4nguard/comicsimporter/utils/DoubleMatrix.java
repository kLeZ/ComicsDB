package it.d4nguard.comicsimporter.utils;

/*************************************************************************
 * Compilation: javac DoubleMatrix.java
 * Execution: java DoubleMatrix
 * A bare-bones immutable data type for M-by-N matrices.
 *************************************************************************/

/**
 * DoubleMatrix.java
 * Last updated: Wed Feb 9 09:20:16 EST 2011.
 * Below is the syntax highlighted version of DoubleMatrix.java from §9.5
 * Numerical Linear Algebra.
 * 
 * @author Copyright © 2000–2011, Robert Sedgewick and Kevin Wayne.
 */
public final class DoubleMatrix extends Matrix<Double>
{
	// create M-by-N matrix of 0's
	public DoubleMatrix(int M, int N)
	{
		super(M, N);
	}

	// create matrix based on 2d array
	public DoubleMatrix(Double[][] d)
	{
		super(d);
	}

	// copy constructor
	private DoubleMatrix(Matrix<Double> A)
	{
		this(A.data);
	}

	// create and return a random M-by-N matrix with values between 0 and 1
	public static Matrix<Double> random(int M, int N)
	{
		DoubleMatrix A = new DoubleMatrix(M, N);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				A.data[i][j] = Math.random();
			}
		}
		return A;
	}

	// create and return the N-by-N identity matrix
	public static Matrix<Double> identity(int N)
	{
		DoubleMatrix I = new DoubleMatrix(N, N);
		for (int i = 0; i < N; i++)
		{
			I.data[i][i] = new Double(1);
		}
		return I;
	}

	// create and return the transpose of the invoking matrix
	@Override
	public Matrix<Double> transpose()
	{
		DoubleMatrix A = new DoubleMatrix(N, M);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				A.data[j][i] = data[i][j];
			}
		}
		return A;
	}

	// return C = A + B
	@Override
	public Matrix<Double> plus(Matrix<Double> B)
	{
		DoubleMatrix A = this;
		if ((B.M != A.M) || (B.N != A.N)) { throw new RuntimeException("Illegal matrix dimensions."); }
		DoubleMatrix C = new DoubleMatrix(M, N);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				C.data[i][j] = A.data[i][j] + B.data[i][j];
			}
		}
		return C;
	}

	// return C = A - B
	@Override
	public Matrix<Double> minus(Matrix<Double> B)
	{
		DoubleMatrix A = this;
		if ((B.M != A.M) || (B.N != A.N)) { throw new RuntimeException("Illegal matrix dimensions."); }
		DoubleMatrix C = new DoubleMatrix(M, N);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				C.data[i][j] = A.data[i][j] - B.data[i][j];
			}
		}
		return C;
	}

	// does A = B exactly?
	@Override
	public boolean eq(Matrix<Double> B)
	{
		DoubleMatrix A = this;
		if ((B.M != A.M) || (B.N != A.N)) { throw new RuntimeException("Illegal matrix dimensions."); }
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				if (A.data[i][j] != B.data[i][j]) { return false; }
			}
		}
		return true;
	}

	// return C = A * B
	@Override
	public Matrix<Double> times(Matrix<Double> B)
	{
		DoubleMatrix A = this;
		if (A.N != B.M) { throw new RuntimeException("Illegal matrix dimensions."); }
		DoubleMatrix C = new DoubleMatrix(A.M, B.N);
		for (int i = 0; i < C.M; i++)
		{
			for (int j = 0; j < C.N; j++)
			{
				for (int k = 0; k < A.N; k++)
				{
					C.data[i][j] += (A.data[i][k] * B.data[k][j]);
				}
			}
		}
		return C;
	}

	// return x = A^-1 b, assuming A is square and has full rank
	@Override
	public Matrix<Double> solve(Matrix<Double> rhs)
	{
		if ((M != N) || (rhs.M != N) || (rhs.N != 1)) { throw new RuntimeException("Illegal matrix dimensions."); }

		// create copies of the data
		DoubleMatrix A = new DoubleMatrix(this);
		DoubleMatrix b = new DoubleMatrix(rhs);

		// Gaussian elimination with partial pivoting
		for (int i = 0; i < N; i++)
		{
			// find pivot row and swap
			int max = i;
			for (int j = i + 1; j < N; j++)
			{
				if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
				{
					max = j;
				}
			}
			A.swap(i, max);
			b.swap(i, max);

			// singular
			if (A.data[i][i] == 0.0) { throw new RuntimeException("DoubleMatrix is singular."); }

			// pivot within b
			for (int j = i + 1; j < N; j++)
			{
				b.data[j][0] -= (b.data[i][0] * A.data[j][i]) / A.data[i][i];
			}

			// pivot within A
			for (int j = i + 1; j < N; j++)
			{
				double m = A.data[j][i] / A.data[i][i];
				for (int k = i + 1; k < N; k++)
				{
					A.data[j][k] -= A.data[i][k] * m;
				}
				A.data[j][i] = 0.0;
			}
		}

		// back substitution
		DoubleMatrix x = new DoubleMatrix(N, 1);
		for (int j = N - 1; j >= 0; j--)
		{
			double t = 0.0;
			for (int k = j + 1; k < N; k++)
			{
				t += A.data[j][k] * x.data[k][0];
			}
			x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
		}
		return x;
	}

	// print matrix to standard output
	@Override
	public String show()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				sb.append(String.format("%9.4f ", data[i][j]));
			}
			sb.append(System.getProperty("line.separator"));
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	// test client
	public static void main(String[] args)
	{
		Double[][] d = { { new Double(1), new Double(2), new Double(3) }, { new Double(4), new Double(5), new Double(6) }, { new Double(9), new Double(1), new Double(3) } };
		Matrix<Double> D = new DoubleMatrix(d);
		D.show();
		System.out.println();

		Matrix<Double> A = DoubleMatrix.random(5, 5);
		A.show();
		System.out.println();

		A.swap(1, 2);
		A.show();
		System.out.println();

		Matrix<Double> B = A.transpose();
		B.show();
		System.out.println();

		Matrix<Double> C = DoubleMatrix.identity(5);
		C.show();
		System.out.println();

		A.plus(B).show();
		System.out.println();

		B.times(A).show();
		System.out.println();

		// shouldn't be equal since AB != BA in general
		System.out.println(A.times(B).eq(B.times(A)));
		System.out.println();

		Matrix<Double> b = DoubleMatrix.random(5, 1);
		b.show();
		System.out.println();

		Matrix<Double> x = A.solve(b);
		x.show();
		System.out.println();

		A.times(x).show();
	}
}
