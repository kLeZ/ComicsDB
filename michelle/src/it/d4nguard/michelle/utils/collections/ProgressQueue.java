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

	public Progress send(long timeElapsedForLastOperation, float progressIndex, int operationWeight, String operationName, String statusMessage)
	{
		Progress p = new Progress(timeElapsedForLastOperation, progressIndex, operationWeight, operationName, statusMessage);
		add(p);
		return p;
	}
}
