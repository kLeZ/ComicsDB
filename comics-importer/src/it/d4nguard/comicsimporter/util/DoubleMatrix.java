package it.d4nguard.comicsimporter.util;

import org.apache.log4j.Logger;

/*************************************************************************
 * Compilation: javac DoubleMatrix.java
 * Execution: java DoubleMatrix
 * A bare-bones immutable data type for M-by-N matrices.
 *************************************************************************/
public final class DoubleMatrix extends Matrix<Double>
{
	private static Logger log = Logger.getLogger(DoubleMatrix.class);

	// create and return the N-by-N identity matrix
	public static Matrix<Double> identity(final int N)
	{
		final DoubleMatrix I = new DoubleMatrix(N, N);
		for (int i = 0; i < N; i++)
		{
			I.data[i][i] = new Double(1);
		}
		return I;
	}

	// test client
	public static void main(final String[] args)
	{
		final Double[][] d =
		{
		{ new Double(1), new Double(2), new Double(3) },
		{ new Double(4), new Double(5), new Double(6) },
		{ new Double(9), new Double(1), new Double(3) } };
		final Matrix<Double> D = new DoubleMatrix(d);
		D.show();

		final Matrix<Double> A = DoubleMatrix.random(5, 5);
		A.show();

		A.swap(1, 2);
		A.show();

		final Matrix<Double> B = A.transpose();
		B.show();

		final Matrix<Double> C = DoubleMatrix.identity(5);
		C.show();

		A.plus(B).show();

		B.times(A).show();

		// shouldn't be equal since AB != BA in general
		log.info(A.times(B).eq(B.times(A)));

		final Matrix<Double> b = DoubleMatrix.random(5, 1);
		b.show();

		final Matrix<Double> x = A.solve(b);
		x.show();

		A.times(x).show();
	}

	// create and return a random M-by-N matrix with values between 0 and 1
	public static Matrix<Double> random(final int M, final int N)
	{
		final DoubleMatrix A = new DoubleMatrix(M, N);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				A.data[i][j] = Math.random();
			}
		}
		return A;
	}

	// create matrix based on 2d array
	public DoubleMatrix(final Double[][] d)
	{
		super(d);
	}

	// create M-by-N matrix of 0's
	public DoubleMatrix(final int M, final int N)
	{
		super(M, N);
	}

	// copy constructor
	private DoubleMatrix(final Matrix<Double> A)
	{
		this(A.data);
	}

	// does A = B exactly?
	@Override
	public boolean eq(final Matrix<Double> B)
	{
		final DoubleMatrix A = this;
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

	// return C = A - B
	@Override
	public Matrix<Double> minus(final Matrix<Double> B)
	{
		final DoubleMatrix A = this;
		if ((B.M != A.M) || (B.N != A.N)) { throw new RuntimeException("Illegal matrix dimensions."); }
		final DoubleMatrix C = new DoubleMatrix(M, N);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				C.data[i][j] = A.data[i][j] - B.data[i][j];
			}
		}
		return C;
	}

	// return C = A + B
	@Override
	public Matrix<Double> plus(final Matrix<Double> B)
	{
		final DoubleMatrix A = this;
		if ((B.M != A.M) || (B.N != A.N)) { throw new RuntimeException("Illegal matrix dimensions."); }
		final DoubleMatrix C = new DoubleMatrix(M, N);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				C.data[i][j] = A.data[i][j] + B.data[i][j];
			}
		}
		return C;
	}

	// print matrix to standard output
	@Override
	public String show()
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				sb.append(String.format("%9.4f ", data[i][j]));
			}
			sb.append(System.getProperty("line.separator"));
		}
		log.info(sb.toString());
		return sb.toString();
	}

	// return x = A^-1 b, assuming A is square and has full rank
	@Override
	public Matrix<Double> solve(final Matrix<Double> rhs)
	{
		if ((M != N) || (rhs.M != N) || (rhs.N != 1)) { throw new RuntimeException("Illegal matrix dimensions."); }

		// create copies of the data
		final DoubleMatrix A = new DoubleMatrix(this);
		final DoubleMatrix b = new DoubleMatrix(rhs);

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
				final double m = A.data[j][i] / A.data[i][i];
				for (int k = i + 1; k < N; k++)
				{
					A.data[j][k] -= A.data[i][k] * m;
				}
				A.data[j][i] = 0.0;
			}
		}

		// back substitution
		final DoubleMatrix x = new DoubleMatrix(N, 1);
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

	// return C = A * B
	@Override
	public Matrix<Double> times(final Matrix<Double> B)
	{
		final DoubleMatrix A = this;
		if (A.N != B.M) { throw new RuntimeException("Illegal matrix dimensions."); }
		final DoubleMatrix C = new DoubleMatrix(A.M, B.N);
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

	// create and return the transpose of the invoking matrix
	@Override
	public Matrix<Double> transpose()
	{
		final DoubleMatrix A = new DoubleMatrix(N, M);
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				A.data[j][i] = data[i][j];
			}
		}
		return A;
	}
}
