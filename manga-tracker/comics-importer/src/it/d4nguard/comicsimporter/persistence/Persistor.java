package it.d4nguard.comicsimporter.persistence;

import java.util.Collection;

public interface Persistor<E>
{
	int drop(E elem);

	int dropMany(Collection<E> elems);

	E pop(CharSequence filter);

	Collection<E> popMany(CharSequence filter);

	int push(E elem);

	int pushMany(Collection<E> elems);
}
