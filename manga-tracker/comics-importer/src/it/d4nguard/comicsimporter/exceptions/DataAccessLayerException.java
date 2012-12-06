package it.d4nguard.comicsimporter.exceptions;

/**
 * Represents Exceptions thrown by the Data Access Layer.
 */
public class DataAccessLayerException extends RuntimeException
{
	private static final long serialVersionUID = 180958402355795286L;

	public DataAccessLayerException()
	{
	}

	public DataAccessLayerException(String message)
	{
		super(message);
	}

	public DataAccessLayerException(Throwable cause)
	{
		super(cause);
	}

	public DataAccessLayerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
