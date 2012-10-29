package it.d4nguard.comicsimporter.persistence;

import java.util.Collection;

public interface Persistor
{
	<E> E pop(CharSequence filter);

	<E> Collection<E> popMany(CharSequence filter);

	<E> int push(E elem);

	<E> int pushMany(Collection<E> elems);

	<E> int drop(E elem);

	<E> int dropMany(Collection<E> elems);
}
