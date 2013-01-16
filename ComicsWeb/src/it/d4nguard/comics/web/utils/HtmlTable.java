package it.d4nguard.comics.web.utils;

import it.d4nguard.comics.utils.DataColumn;
import it.d4nguard.comics.utils.DataRow;
import it.d4nguard.comics.utils.DataTable;

import org.apache.commons.lang.text.StrBuilder;

public class HtmlTable implements HtmlElement
{
	private DataTable data;

	/**
	 * 
	 */
	public HtmlTable(DataTable data)
	{
		this.data = data;
	}

	@Override
	public String render()
	{
		StrBuilder sb = new StrBuilder();
		sb.appendln("<table>");

		sb.appendln("\t<thead>");
		sb.appendln("\t\t<tr>");
		for (DataColumn<?> col : data.getColumns())
		{
			sb.append("\t\t\t<th>").append(col.getName()).appendln("</th>");
		}
		sb.appendln("\t\t</tr>");
		sb.appendln("\t</thead>");

		sb.appendln("\t<tbody>");
		for (DataRow row : data.getRows())
		{
			sb.appendln("\t\t<tr>");
			for (Object cell : row.getCells())
			{
				sb.append("\t\t\t<td>").append(cell).appendln("</td>");
			}
			sb.appendln("\t\t</tr>");
		}
		sb.appendln("\t</tbody>");

		sb.appendln("</table>");
		return sb.toString();
	}
}
