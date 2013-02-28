package it.d4nguard.michelle.utils.web;

import it.d4nguard.michelle.utils.data.DataTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class Tablizer<T> implements HtmlElement
{
	private final Class<T> clazz;

	public Tablizer(final Class<T> clazz)
	{
		this.clazz = clazz;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.michelle.utils.web.HtmlElement#render()
	 */
	@Override
	public String render()
	{
		return new HtmlTable(getDataFromClass(clazz)).render();
	}

	public static DataTable getDataFromClass(final Class<?> clazz)
	{
		final DataTable table = new DataTable();
		table.insertColumn("Type", String.class);
		table.insertColumn("Field", String.class);
		table.insertColumn("Structure", String.class);

		final Field[] fields = clazz.getDeclaredFields();
		for (final Field field : fields)
			if (!field.getName().equalsIgnoreCase("id") && !field.getName().equalsIgnoreCase("serialVersionUID"))
			{
				String type = "";
				if (field.getGenericType() instanceof ParameterizedType)
				{
					final ParameterizedType pt = (ParameterizedType) field.getGenericType();
					final Class<?> typec = (Class<?>) pt.getRawType();
					final Class<?> typegen = (Class<?>) pt.getActualTypeArguments()[0];
					type = String.format("%s Of %s", typec.getSimpleName(), typegen.getSimpleName());
				}
				else type = field.getType().getSimpleName();
				table.add(type, field.getName(), clazz.getSimpleName());
			}
		return table;
	}
}
