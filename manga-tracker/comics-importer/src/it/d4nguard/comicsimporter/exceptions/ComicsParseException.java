package it.d4nguard.comicsimporter.exceptions;

public class ComicsParseException extends Exception
{
	private static final long serialVersionUID = -6975454143191862640L;

	public ComicsParseException()
	{
	}

	public ComicsParseException(String message)
	{
		super(message);
	}

	public ComicsParseException(Throwable cause)
	{
		super(cause);
	}

	public ComicsParseException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
