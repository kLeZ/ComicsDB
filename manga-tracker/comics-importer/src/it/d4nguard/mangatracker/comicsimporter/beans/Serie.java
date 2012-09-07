package it.d4nguard.mangatracker.comicsimporter.beans;

import it.d4nguard.mangatracker.comicsimporter.utils.Money;

import java.util.ArrayList;

public class Serie extends ArrayList<Volume>
{
	private static final long serialVersionUID = -5364853841641256846L;

	private boolean complete;
	private boolean completeInCountry;

	public Serie(boolean complete, boolean completeInCountry)
	{
		this.complete = complete;
		this.completeInCountry = completeInCountry;
	}

	public boolean isComplete()
	{
		return complete;
	}

	public void setComplete(boolean complete)
	{
		this.complete = complete;
	}

	public boolean isComplete_in_country()
	{
		return completeInCountry;
	}

	public void setComplete_in_country(boolean completeInCountry)
	{
		this.completeInCountry = completeInCountry;
	}

	public boolean add(String name, String editor, boolean last, Money price)
	{
		return add(new Volume(name, editor, last, price));
	}

	public boolean contains(String name)
	{
		Volume ret = null;
		for (Volume v : this)
		{
			if (v.getName().contentEquals(name))
			{
				ret = v;
				break;
			}
		}
		return ret != null;
	}
}
