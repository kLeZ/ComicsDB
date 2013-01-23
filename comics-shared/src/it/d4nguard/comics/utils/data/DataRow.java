package it.d4nguard.comics.utils.data;

import it.d4nguard.comics.utils.collections.CollectionsUtils;

import java.util.*;

public class DataRow
{
	private DataTable owner;
	private List<Object> cells;
	private Set<DataColumn<?>> columns;
	private Map<DataColumn<?>, Integer> cellDataPair;

	public DataRow(DataTable owner)
	{
		this.owner = owner;
		columns = owner.getColumns();
		cellDataPair = new LinkedHashMap<DataColumn<?>, Integer>();
	}

	public DataRow(DataTable owner, Object... values)
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
	public <T> T get(int index)
	{
		return (T) cells.get(index);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name)
	{
		T ret = null;
		Iterator<DataColumn<?>> it = columns.iterator();
		while (it.hasNext() && (ret == null))
		{
			DataColumn<?> curr = it.next();
			if (curr.getName().equals(name))
			{
				ret = (T) cells.get(cellDataPair.get(curr));
			}
		}
		return ret;
	}

	public void set(int index, Object element)
	{
		cells.set(index, element);
		ensureCells();
	}

	public void set(String column, Object element)
	{
		Iterator<DataColumn<?>> it = columns.iterator();
		while (it.hasNext())
		{
			DataColumn<?> curr = it.next();
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
			DataColumn<?> col = CollectionsUtils.get(columns, i);
			if (col.getType().isInstance(cells.get(i)) || (cells.get(i) == null))
			{
				cellDataPair.put(col, i);
			}
			else
			{
				String fmt = "Cell value '%s' at position %d do not agree with the type of its column (%s).";
				throw new RuntimeException(String.format(fmt, cells.get(i).toString(), i, col.getType().getName()));
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DataRow [cells=");
		builder.append(cells);
		builder.append("]");
		return builder.toString();
	}
}
