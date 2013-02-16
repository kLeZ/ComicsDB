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

	public ProgressRunnable(ProgressQueue progressQueue)
	{
		this.progressQueue = progressQueue;
	}

	public Progress send(long timeElapsedForLastOperation, float progressIndex, int operationWeight, String operationName, String statusMessage)
	{
		return progressQueue.send(timeElapsedForLastOperation, progressIndex, operationWeight, operationName, statusMessage);
	}
}
