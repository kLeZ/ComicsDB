package it.d4nguard.comicsimporter.util.io;

import java.io.*;

/**
 * Utility for making deep copies (vs. clone()'s shallow copies) of objects
 * in a memory efficient way. Objects are serialized in the calling thread and
 * de-serialized in another thread.
 * Error checking is fairly minimal in this implementation. If an object is
 * encountered that cannot be serialized (or that references an object
 * that cannot be serialized) an error is printed to System.err and
 * null is returned. Depending on your specific application, it might
 * make more sense to have copy(...) re-throw the exception.
 */
public class PipedDeepCopy
{
	/**
	 * Thread subclass that handles deserializing from a PipedInputStream.
	 */
	private static class Deserializer extends Thread
	{
		/**
		 * Object that we are deserializing
		 */
		private Object obj = null;

		/**
		 * Lock that we block on while deserialization is happening
		 */
		private Object lock = null;

		/**
		 * InputStream that the object is deserialized from.
		 */
		private PipedInputStream in = null;

		public Deserializer(final PipedInputStream pin) throws IOException
		{
			lock = new Object();
			in = pin;
			start();
		}

		/**
		 * Returns the deserialized object. This method will block until
		 * the object is actually available.
		 */
		public Object getDeserializedObject()
		{
			// Wait for the object to show up
			try
			{
				synchronized (lock)
				{
					while (obj == null)
						lock.wait();
				}
			}
			catch (final InterruptedException ie)
			{
				// If we are interrupted we just return null
			}
			return obj;
		}

		@Override
		public void run()
		{
			Object o = null;
			try
			{
				final ObjectInputStream oin = new ObjectInputStream(in);
				o = oin.readObject();
			}
			catch (final IOException e)
			{
				// This should never happen. If it does we make sure
				// that a the object is set to a flag that indicates
				// deserialization was not possible.
				e.printStackTrace();
			}
			catch (final ClassNotFoundException cnfe)
			{
				// Same here...
				cnfe.printStackTrace();
			}

			synchronized (lock)
			{
				if (o == null) obj = ERROR;
				else obj = o;
				lock.notifyAll();
			}
		}
	}

	/**
	 * Flag object used internally to indicate that deserialization failed.
	 */
	private static final Object ERROR = new Object();

	/**
	 * Returns a copy of the object, or null if the object cannot
	 * be serialized.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(final T orig)
	{
		Object obj = null;
		try
		{
			// Make a connected pair of piped streams
			final PipedInputStream in = new PipedInputStream();
			final PipedOutputStream pos = new PipedOutputStream(in);

			// Make a deserializer thread (see inner class below)
			final Deserializer des = new Deserializer(in);

			// Write the object to the pipe
			@SuppressWarnings("resource")
			final ObjectOutputStream out = new ObjectOutputStream(pos);
			out.writeObject(orig);

			// Wait for the object to be deserialized
			obj = des.getDeserializedObject();

			// See if something went wrong
			if (obj == ERROR) obj = null;
		}
		catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}

		return (T) obj;
	}
}
