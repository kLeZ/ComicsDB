package it.d4nguard.michelle.utils.data;

import it.d4nguard.michelle.utils.Money;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MoneyJsonDeserializer implements JsonDeserializer<Money>
{
	/* (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public Money deserialize(final JsonElement arg0, final Type arg1, final JsonDeserializationContext arg2) throws JsonParseException
	{
		return new Money(arg0.getAsJsonObject().get("value").getAsString());
	}
}
