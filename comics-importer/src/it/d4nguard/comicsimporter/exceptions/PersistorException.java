package it.d4nguard.comicsimporter.exceptions;

/**
 * Represents Exceptions thrown by the Data Access Layer.
 */
public class PersistorException extends RuntimeException
{
	private static final long serialVersionUID = 180958402355795286L;

	public PersistorException()
	{
	}

	public PersistorException(final String message)
	{
		super(message);
	}

	public PersistorException(final Throwable cause)
	{
		super(cause);
	}

	public PersistorException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
