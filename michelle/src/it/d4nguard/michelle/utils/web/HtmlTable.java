package it.d4nguard.michelle.utils.web;

import it.d4nguard.michelle.utils.data.DataColumn;
import it.d4nguard.michelle.utils.data.DataRow;
import it.d4nguard.michelle.utils.data.DataTable;

public class HtmlTable implements HtmlElement
{
	private static final String LS = System.getProperty("line.separator");
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
		StringBuilder sb = new StringBuilder();
		sb.append("<table>").append(LS);

		sb.append("\t<thead>").append(LS);
		sb.append("\t\t<tr>").append(LS);
		for (DataColumn<?> col : data.getColumns())
		{
			sb.append("\t\t\t<th>").append(col.getName()).append("</th>").append(LS);
		}
		sb.append("\t\t</tr>").append(LS);
		sb.append("\t</thead>").append(LS);

		sb.append("\t<tbody>").append(LS);
		for (DataRow row : data.getRows())
		{
			sb.append("\t\t<tr>").append(LS);
			for (Object cell : row.getCells())
			{
				sb.append("\t\t\t<td>").append(cell).append("</td>").append(LS);
			}
			sb.append("\t\t</tr>").append(LS);
		}
		sb.append("\t</tbody>").append(LS);

		sb.append("</table>").append(LS);
		return sb.toString();
	}
}
