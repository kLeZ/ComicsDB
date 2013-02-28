package it.d4nguard.michelle.utils.data;

import it.d4nguard.michelle.utils.collections.CollectionsUtils;

import java.util.*;

public class DataRow
{
	private final DataTable owner;
	private List<Object> cells;
	private final Set<DataColumn<?>> columns;
	private final Map<DataColumn<?>, Integer> cellDataPair;

	public DataRow(final DataTable owner)
	{
		this.owner = owner;
		columns = owner.getColumns();
		cellDataPair = new LinkedHashMap<DataColumn<?>, Integer>();
	}

	public DataRow(final DataTable owner, final Object... values)
	{
		this.owner = owner;
		columns = owner.getColumns();
		cells = Arrays.asList(values);
		cellDataPair = new LinkedHashMap<DataColumn<?>, Integer>();
	}

	public List<Object> getCells()
	{
		return cells;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final int index)
	{
		return (T) cells.get(index);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final String name)
	{
		T ret = null;
		final Iterator<DataColumn<?>> it = columns.iterator();
		while (it.hasNext() && ret == null)
		{
			final DataColumn<?> curr = it.next();
			if (curr.getName().equals(name)) ret = (T) cells.get(cellDataPair.get(curr));
		}
		return ret;
	}

	public void set(final int index, final Object element)
	{
		cells.set(index, element);
		ensureCells();
	}

	public void set(final String column, final Object element)
	{
		final Iterator<DataColumn<?>> it = columns.iterator();
		while (it.hasNext())
		{
			final DataColumn<?> curr = it.next();
			if (curr.getName().equals(column))
			{
				cells.set(cellDataPair.get(curr), element);
				break;
			}
		}
	}

	public DataTable getOwnerTable()
	{
		return owner;
	}

	public void ensureCells()
	{
		for (int i = 0; i < cells.size(); i++)
		{
			final DataColumn<?> col = CollectionsUtils.get(columns, i);
			if (col.getType().isInstance(cells.get(i)) || cells.get(i) == null) cellDataPair.put(col, i);
			else
			{
				final String fmt = "Cell value '%s' at position %d do not agree with the type of its column (%s).";
				throw new RuntimeException(String.format(fmt, cells.get(i).toString(), i, col.getType().getName()));
			}
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("DataRow [cells=");
		builder.append(cells);
		builder.append("]");
		return builder.toString();
	}
}
