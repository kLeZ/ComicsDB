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

	public Progress(long timeElapsedForLastOperation, float progressIndex, int operationWeight, String operationName, String statusMessage)
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

	public void setTimeElapsedForLastOperation(long timeElapsedForLastOperation)
	{
		this.timeElapsedForLastOperation = timeElapsedForLastOperation;
	}

	public float getProgressIndex()
	{
		return progressIndex;
	}

	public void setProgressIndex(float progressIndex)
	{
		this.progressIndex = progressIndex;
	}

	public int getOperationWeight()
	{
		return operationWeight;
	}

	public void setOperationWeight(int operationWeight)
	{
		this.operationWeight = operationWeight;
	}

	public String getOperationName()
	{
		return operationName;
	}

	public void setOperationName(String operationName)
	{
		this.operationName = operationName;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
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
