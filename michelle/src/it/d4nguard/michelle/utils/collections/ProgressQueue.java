/**
 * 
 */
package it.d4nguard.michelle.utils.collections;

import it.d4nguard.michelle.utils.Progress;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author kLeZ-hAcK
 */
public class ProgressQueue extends ConcurrentLinkedQueue<Progress>
{
	private static final long serialVersionUID = -2104013124389712362L;

	public Progress send(final long timeElapsedForLastOperation, final float progressIndex, final int operationWeight, final String operationName, final String statusMessage)
	{
		final Progress p = new Progress(timeElapsedForLastOperation, progressIndex, operationWeight, operationName, statusMessage);
		add(p);
		return p;
	}
}
