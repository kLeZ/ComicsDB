package it.d4nguard.comicsimporter.beans.mappers.bo;

public interface BOMapper<BO, BEAN>
{
	BO mapBean(BEAN bean);

	BEAN mapBO(BO bo);
}
