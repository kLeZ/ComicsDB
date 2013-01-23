package it.d4nguard.comics.utils.collections;

import it.d4nguard.comics.utils.GenericsUtils;

import java.lang.reflect.Array;

/**
 * @author kLeZ-hAcK
 */
public abstract class Matrix<T>
{
	protected final int M; // number of rows
	protected final int N; // number of columns
	protected final T[][] data; // M-by-N array

	// create M-by-N matrix of 0's
	@SuppressWarnings("unchecked")
	public Matrix(final int M, final int N)
	{
		this.M = M;
		this.N = N;
		data = ((T[][]) Array.newInstance(getGenericTypeClass(), M, N));
	}

	// create matrix based on 2d array
	@SuppressWarnings("unchecked")
	public Matrix(final T[][] data)
	{
		M = data.length;
		N = data[0].length;
		this.data = ((T[][]) Array.newInstance(getGenericTypeClass(), M, N));
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				this.data[i][j] = data[i][j];
	}

	/**
	 * does A = B exactly?
	 * 
	 * @param B
	 * @return
	 */
	public abstract boolean eq(Matrix<T> B);

	protected Class<?> getGenericTypeClass()
	{
		return GenericsUtils.getTypeArguments(Matrix.class, getClass()).get(0);
	}

	/**
	 * return C = A - B
	 * 
	 * @param B
	 * @return
	 */
	public abstract Matrix<T> minus(Matrix<T> B);

	/**
	 * return C = A + B
	 * 
	 * @param B
	 * @return
	 */
	public abstract Matrix<T> plus(Matrix<T> B);

	/**
	 * print matrix to standard output
	 */
	public abstract String show();

	/**
	 * return x = A^-1 b, assuming A is square and has full rank
	 * 
	 * @param rhs
	 * @return
	 */
	public abstract Matrix<T> solve(Matrix<T> rhs);

	// swap rows i and j
	protected void swap(final int i, final int j)
	{
		final T[] temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	/**
	 * return C = A * B
	 * 
	 * @param B
	 * @return
	 */
	public abstract Matrix<T> times(Matrix<T> B);

	/**
	 * create and return the transpose of the invoking matrix
	 * 
	 * @return
	 */
	public abstract Matrix<T> transpose();
}
