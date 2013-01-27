package it.d4nguard.michelle.utils.data;

public interface BOMapper<BO, BEAN>
{
	BO mapBean(BEAN bean);

	BEAN mapBO(BO bo);
}
