package it.d4nguard.michelle.utils;

import java.io.Serializable;

public class Progress implements Serializable
{
	private static final long serialVersionUID = -3556902291413269483L;

	private long timeElapsedForLastOperation;
	private float progressIndex;
	private int operationWeight;
	private String operationName;
	private String statusMessage;

	public Progress()
	{
	}

	public Progress(final long timeElapsedForLastOperation, final float progressIndex, final int operationWeight, final String operationName, final String statusMessage)
	{
		this.timeElapsedForLastOperation = timeElapsedForLastOperation;
		this.progressIndex = progressIndex;
		this.operationWeight = operationWeight;
		this.operationName = operationName;
		this.statusMessage = statusMessage;
	}

	public long getTimeElapsedForLastOperation()
	{
		return timeElapsedForLastOperation;
	}

	public void setTimeElapsedForLastOperation(final long timeElapsedForLastOperation)
	{
		this.timeElapsedForLastOperation = timeElapsedForLastOperation;
	}

	public float getProgressIndex()
	{
		return progressIndex;
	}

	public void setProgressIndex(final float progressIndex)
	{
		this.progressIndex = progressIndex;
	}

	public int getOperationWeight()
	{
		return operationWeight;
	}

	public void setOperationWeight(final int operationWeight)
	{
		this.operationWeight = operationWeight;
	}

	public String getOperationName()
	{
		return operationName;
	}

	public void setOperationName(final String operationName)
	{
		this.operationName = operationName;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(final String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Progress [timeElapsedForLastOperation=");
		builder.append(timeElapsedForLastOperation);
		builder.append(", progressIndex=");
		builder.append(progressIndex);
		builder.append(", operationWeight=");
		builder.append(operationWeight);
		builder.append(", operationName=");
		builder.append(operationName);
		builder.append(", statusMessage=");
		builder.append(statusMessage);
		builder.append("]");
		return builder.toString();
	}
}
