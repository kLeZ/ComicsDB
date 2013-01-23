package it.d4nguard.comics.utils.data;

public class DataColumn<T>
{
	private String name;
	private Class<T> type;

	public DataColumn(String name, Class<T> type)
	{
		this.name = name;
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public Class<T> getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DataColumn [name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
}
