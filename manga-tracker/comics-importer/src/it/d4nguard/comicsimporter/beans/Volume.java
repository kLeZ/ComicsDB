package it.d4nguard.comicsimporter.beans;

import it.d4nguard.comicsimporter.utils.Money;

import java.io.Serializable;

public class Volume implements Serializable
{
	private static final long serialVersionUID = 1788104556294682013L;

	private String name;
	private String serie;
	private String editor;
	private boolean last;
	private Money price;

	public Volume(final String name)
	{
		this.name = name;
	}

	public Volume(final String name, final String editor, final boolean last, final Money price)
	{
		this.name = name;
		this.editor = editor;
		this.last = last;
		this.price = price;
	}

	public String getEditor()
	{
		return editor;
	}

	public String getName()
	{
		return name;
	}

	public Money getPrice()
	{
		return price;
	}

	public String getSerie()
	{
		return serie;
	}

	public boolean isLast()
	{
		return last;
	}

	public void setEditor(final String editor)
	{
		this.editor = editor;
	}

	public void setLast(final boolean last)
	{
		this.last = last;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setPrice(final Money price)
	{
		this.price = price;
	}

	public void setSerie(final String serie)
	{
		this.serie = serie;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Volume [name=");
		builder.append(name);
		if ((serie != null) && !serie.isEmpty())
		{
			builder.append(", serie=");
			builder.append(serie);
		}
		builder.append(", editor=");
		builder.append(editor);
		builder.append(", last=");
		builder.append(last);
		builder.append(", price=");
		builder.append(price);
		builder.append("]");
		return builder.toString();
	}
}
