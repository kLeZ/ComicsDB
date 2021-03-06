package it.d4nguard.comics.beans;

import it.d4nguard.michelle.utils.Money;

import java.io.Serializable;

public class Volume implements Serializable
{
	private static final long serialVersionUID = -2233198130283684217L;

	private Long id;
	private Long comicId;
	private String name;
	private String serie;
	private Editor editor;
	private boolean last;
	private Money price;

	public Volume()
	{

	}

	public Volume(final Long id, final String name)
	{
		this.id = id;
		this.name = name;
	}

	public Volume(final Long id, final String name, final String serie, final Editor editor, final boolean last, final Money price)
	{
		this.id = id;
		this.name = name;
		this.serie = serie;
		this.editor = editor;
		this.last = last;
		this.price = price;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public Long getComicId()
	{
		return comicId;
	}

	public void setComicId(final Long comicId)
	{
		this.comicId = comicId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getSerie()
	{
		return serie;
	}

	public void setSerie(final String serie)
	{
		this.serie = serie;
	}

	public Editor getEditor()
	{
		return editor;
	}

	public void setEditor(final Editor editor)
	{
		this.editor = editor;
	}

	public boolean isLast()
	{
		return last;
	}

	public void setLast(final boolean last)
	{
		this.last = last;
	}

	public Money getPrice()
	{
		return price;
	}

	public void setPrice(final Money price)
	{
		this.price = price;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (comicId == null ? 0 : comicId.hashCode());
		result = prime * result + (editor == null ? 0 : editor.hashCode());
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (last ? 1231 : 1237);
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (price == null ? 0 : price.hashCode());
		result = prime * result + (serie == null ? 0 : serie.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Volume)) return false;
		final Volume other = (Volume) obj;
		if (comicId == null)
		{
			if (other.comicId != null) return false;
		}
		else if (!comicId.equals(other.comicId)) return false;
		if (editor == null)
		{
			if (other.editor != null) return false;
		}
		else if (!editor.equals(other.editor)) return false;
		if (id == null)
		{
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (last != other.last) return false;
		if (name == null)
		{
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (price == null)
		{
			if (other.price != null) return false;
		}
		else if (!price.equals(other.price)) return false;
		if (serie == null)
		{
			if (other.serie != null) return false;
		}
		else if (!serie.equals(other.serie)) return false;
		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Volume [id=");
		builder.append(id);
		builder.append(", comicId=");
		builder.append(comicId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", serie=");
		builder.append(serie);
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
