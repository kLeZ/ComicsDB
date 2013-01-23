package it.d4nguard.comics.beans.mappers.bo;

public interface BOMapper<BO, BEAN>
{
	BO mapBean(BEAN bean);

	BEAN mapBO(BO bo);
}
