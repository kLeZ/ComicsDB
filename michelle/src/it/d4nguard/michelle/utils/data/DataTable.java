package it.d4nguard.michelle.utils.data;

import it.d4nguard.michelle.utils.collections.CollectionsUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class DataTable
{
	private Set<DataRow> rows;
	private Set<DataColumn<?>> columns;

	public DataTable()
	{
		rows = new LinkedHashSet<DataRow>();
		columns = new LinkedHashSet<DataColumn<?>>();
	}

	public Set<DataRow> getRows()
	{
		return rows;
	}

	public Set<DataColumn<?>> getColumns()
	{
		return columns;
	}

	public DataRow get(int index)
	{
		return CollectionsUtils.get(rows, index);
	}

	public <T> void insertColumn(String name, Class<T> type)
	{
		columns.add(new DataColumn<T>(name, type));
	}

	public DataRow add(String column, Object element)
	{
		DataRow row = add(new Object[getColumns().size()]);
		row.set(column, element);
		return row;
	}

	public DataRow add(Object... values)
	{
		DataRow row = null;
		if (columns.isEmpty())
		{
			throw new RuntimeException("Columns not set, impossible to insert values in cells.");
		}
		else
		{
			row = new DataRow(this, values);
		}
		row.ensureCells();
		rows.add(row);
		return row;
	}

	public void remove(int index)
	{
		rows.remove(get(index));
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DataTable [rows=");
		builder.append(rows);
		builder.append(", columns=");
		builder.append(columns);
		builder.append("]");
		return builder.toString();
	}
}
