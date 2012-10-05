package it.d4nguard.comicsimporter.beans;

import it.d4nguard.comicsimporter.utils.Money;
import it.d4nguard.comicsimporter.utils.StringUtils;

import java.io.Serializable;

import org.w3c.dom.Element;

public class Volume implements Serializable
{
	private static final long serialVersionUID = 1788104556294682013L;

	private String name;
	private String serie;
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

	public String getSerie()
	{
		return serie;
	}

	public void setSerie(String serie)
	{
		this.serie = serie;
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

	public static Volume createVolume(Element volumeElem)
	{
		Volume volume = new Volume(StringUtils.clean(volumeElem.getAttribute("nome")));
		if (volumeElem.hasAttribute("serie"))
		{
			volume.setSerie(volumeElem.getAttribute("serie"));
		}
		volume.setEditor(volumeElem.getAttribute("editore"));
		volume.setPrice(new Money(volumeElem.getAttribute("prezzo")));
		volume.setLast(new Boolean(volumeElem.getAttribute("ultimo")));
		return volume;
	}
}
