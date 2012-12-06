package it.d4nguard.comicsimporter;

public interface Commands
{
	public static final String SAVE_CACHE_CMD = "it.d4nguard.comicsimporter.Commands.save-cache";
	public static final String PERSIST_CMD = "it.d4nguard.comicsimporter.Commands.persist";
	public static final String LOAD_PERSISTENCE_CMD = "it.d4nguard.comicsimporter.Commands.load-persistence";
	public static final String SYNC_CMD = "it.d4nguard.comicsimporter.Commands.sync";
	public static final String WIPE_DB_CMD = "it.d4nguard.comicsimporter.Commands.wipe-db";
	public static final String CACHE_FILE_CMD = "it.d4nguard.comicsimporter.Commands.cache-file";
	public static final String PRINT_TITLES_CMD = "it.d4nguard.comicsimporter.Commands.print-titles";
	public static final String NUMBER_COMICS_CMD = "it.d4nguard.comicsimporter.Commands.number-comics";
	public static final String REFRESH_CACHE_FILE_CMD = "it.d4nguard.comicsimporter.Commands.refresh-cache-file";
}
