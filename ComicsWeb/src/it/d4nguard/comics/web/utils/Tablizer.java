package it.d4nguard.comics.web.utils;

import it.d4nguard.comics.utils.DataTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class Tablizer<T> implements HtmlElement
{
	private Class<T> clazz;

	public Tablizer(Class<T> clazz)
	{
		this.clazz = clazz;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comics.web.utils.HtmlElement#render()
	 */
	@Override
	public String render()
	{
		return new HtmlTable(getDataFromClass(clazz)).render();
	}

	public static DataTable getDataFromClass(Class<?> clazz)
	{
		DataTable table = new DataTable();
		table.insertColumn("Type", String.class);
		table.insertColumn("Field", String.class);
		table.insertColumn("Structure", String.class);

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields)
		{
			String type = "";
			if (field.getGenericType() instanceof ParameterizedType)
			{
				ParameterizedType pt = (ParameterizedType) field.getGenericType();
				Class<?> typec = (Class<?>) pt.getRawType();
				Class<?> typegen = (Class<?>) pt.getActualTypeArguments()[0];
				type = String.format("%s Of %s", typec.getSimpleName(), typegen.getSimpleName());
			}
			else
			{
				type = field.getType().getSimpleName();
			}
			table.add(type, field.getName(), clazz.getSimpleName());
		}
		return table;
	}
}
