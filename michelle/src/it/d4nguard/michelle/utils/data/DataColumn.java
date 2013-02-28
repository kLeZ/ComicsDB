package it.d4nguard.michelle.utils.data;

public class DataColumn<T>
{
	private final String name;
	private final Class<T> type;

	public DataColumn(final String name, final Class<T> type)
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
		final StringBuilder builder = new StringBuilder();
		builder.append("DataColumn [name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
}
