package it.d4nguard.michelle.utils.data;

import it.d4nguard.michelle.utils.Money;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MoneyJsonSerializer implements JsonSerializer<Money>
{
	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(Money arg0, Type arg1, JsonSerializationContext arg2)
	{
		return new JsonPrimitive(arg0.toString());
	}
}
