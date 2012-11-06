package it.d4nguard.comicsimporter.utils.io;

import java.io.PipedInputStream;

/**
 * PipedInputStream subclass that allows buffer size to be set to
 * a value larger than the default 1024 bytes.
 */
public class AdjustableBufferPipedInputStream extends PipedInputStream
{
	public AdjustableBufferPipedInputStream(final int bufSize)
	{
		super();
		buffer = new byte[bufSize];
	}
}
