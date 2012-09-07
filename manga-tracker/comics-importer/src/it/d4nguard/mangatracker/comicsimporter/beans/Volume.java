package it.d4nguard.mangatracker.comicsimporter.beans;

import it.d4nguard.mangatracker.comicsimporter.utils.Money;

public class Volume
{
	private String name;
	private String editor;
	private boolean last;
	private Money price;

	public Volume(String name)
	{
		this.name = name;
	}

	public Volume(String name, String editor, boolean last, Money price)
	{
		this.name = name;
		this.editor = editor;
		this.last = last;
		this.price = price;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getEditor()
	{
		return editor;
	}

	public void setEditor(String editor)
	{
		this.editor = editor;
	}

	public boolean isLast()
	{
		return last;
	}

	public void setLast(boolean last)
	{
		this.last = last;
	}

	public Money getPrice()
	{
		return price;
	}

	public void setPrice(Money price)
	{
		this.price = price;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Volume [name=");
		builder.append(name);
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
