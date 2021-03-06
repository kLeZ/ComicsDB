package it.d4nguard.michelle.utils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericsUtils
{
	private static final HashMap<String, Class<?>> primitives = new HashMap<String, Class<?>>();
	static
	{
		primitives.put("boolean", Boolean.class);
		primitives.put("byte", Byte.class);
		primitives.put("char", Character.class);
		primitives.put("double", Double.class);
		primitives.put("float", Float.class);
		primitives.put("int", Integer.class);
		primitives.put("long", Long.class);
		primitives.put("short", Short.class);
	}

	public static <T> T valueOf(final Class<T> valueType, final String value)
	{
		return unsafeValueOf(valueType, value, null);
	}

	public static <T> T valueOf(final Class<T> valueType, final String value, final Object defaultValue)
	{
		return unsafeValueOf(valueType, value, defaultValue);
	}

	@SuppressWarnings("unchecked")
	private static <T> T unsafeValueOf(final Class<T> valueType, final String value, final Object defaultValue)
	{
		T ret = null;
		if (valueType.isInstance(value)) ret = (T) value;
		else if (isPrimitiveOrPrimitiveWrapper(valueType) && !valueType.equals(Character.class)) try
		{
			// Character has only valueOf(char)
			// All primitives have valueOf(String) except Character
			ret = (T) valueType.getMethod("valueOf", String.class).invoke(null, value);
		}
		catch (final IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (final NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (final SecurityException e)
		{
			e.printStackTrace();
		}
		else if (valueType.equals(Character.class))
		{
			final String v = String.valueOf(value);
			ret = (T) new Character(v.isEmpty() ? 0 : v.charAt(0));
		}
		else if (valueType.equals(String.class)) ret = (T) String.valueOf(value);

		if (ret == null) ret = (T) defaultValue;
		return ret;
	}

	/**
	 * @param type
	 * @return
	 */
	public static boolean isPrimitiveOrPrimitiveWrapper(final Class<?> type)
	{
		return type.isPrimitive() || primitives.values().contains(type);
	}

	public static Class<?> getFieldType(final Class<?> fieldContainer, final String fieldName)
	{
		Class<?> t = null;
		final Field[] fields = fieldContainer.getDeclaredFields();
		for (final Field field : fields)
			if (field.getName().equalsIgnoreCase(fieldName))
			{
				if (field.getType().isPrimitive()) t = primitives.get(field.getType().getName());
				else t = field.getType();
				break;
			}
		return t;
	}

	public static <T> T safeGetter(final T value, final Class<T> type)
	{
		T ret = null;
		try
		{
			ret = value == null ? type.newInstance() : value;
		}
		catch (final InstantiationException e)
		{
			final String fmt = "Type %s cannot be instantiated. Please ensure this call is well-formed.";
			throw new RuntimeException(String.format(fmt, type.getName()));
		}
		catch (final IllegalAccessException e)
		{
			final String fmt = "Type %s cannot be instantiated. Please ensure this call is well-formed.";
			throw new RuntimeException(String.format(fmt, type.getName()));
		}
		return ret;
	}

	/**
	 * Get the underlying class for a type, or null if the type is a variable
	 * type.
	 * 
	 * @param type
	 *            the type
	 * @return the underlying class
	 */
	public static Class<?> getClass(final Type type)
	{
		if (type instanceof Class) return (Class<?>) type;
		else if (type instanceof ParameterizedType) return getClass(((ParameterizedType) type).getRawType());
		else if (type instanceof GenericArrayType)
		{
			final Type componentType = ((GenericArrayType) type).getGenericComponentType();
			final Class<?> componentClass = getClass(componentType);
			if (componentClass != null) return Array.newInstance(componentClass, 0).getClass();
			else return null;
		}
		else return null;
	}

	/**
	 * Get the actual type arguments a child class has used to extend a generic
	 * base class.
	 * 
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	public static <T> List<Class<?>> getTypeArguments(final Class<T> baseClass, final Class<? extends T> childClass)
	{
		final Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass))
			if (type instanceof Class) type = ((Class<?>) type).getGenericSuperclass();
			else
			{
				final ParameterizedType parameterizedType = (ParameterizedType) type;
				final Class<?> rawType = (Class<?>) parameterizedType.getRawType();

				final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++)
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);

				if (!rawType.equals(baseClass)) type = rawType.getGenericSuperclass();
			}

		// finally, for each actual type argument provided to baseClass, determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) actualTypeArguments = ((Class<?>) type).getTypeParameters();
		else actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		final List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments)
		{
			while (resolvedTypes.containsKey(baseType))
				baseType = resolvedTypes.get(baseType);
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}
}
