/**
 * 
 */
package it.d4nguard.michelle.utils.io;

import it.d4nguard.michelle.utils.Progress;
import it.d4nguard.michelle.utils.collections.ProgressQueue;

/**
 * @author kLeZ-hAcK
 */
public abstract class ProgressRunnable implements Runnable
{
	private final ProgressQueue progressQueue;
	private volatile boolean mustStop = false;

	public ProgressRunnable(final ProgressQueue progressQueue)
	{
		this.progressQueue = progressQueue;
	}

	public synchronized void setMustStop(final boolean mustStop)
	{
		this.mustStop = mustStop;
	}

	public synchronized boolean isMustStop()
	{
		return mustStop;
	}

	public Progress send(final long timeElapsedForLastOperation, final float progressIndex, final int operationWeight, final String operationName, final String statusMessage)
	{
		return progressQueue.send(timeElapsedForLastOperation, progressIndex, operationWeight, operationName, statusMessage);
	}
}
