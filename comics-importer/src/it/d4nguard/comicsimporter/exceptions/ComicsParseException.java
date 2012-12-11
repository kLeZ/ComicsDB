package it.d4nguard.comicsimporter.exceptions;

public class ComicsParseException extends Exception
{
	private static final long serialVersionUID = -6975454143191862640L;

	public ComicsParseException()
	{
	}

	public ComicsParseException(final String message)
	{
		super(message);
	}

	public ComicsParseException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public ComicsParseException(final Throwable cause)
	{
		super(cause);
	}
}
