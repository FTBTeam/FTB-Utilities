package com.feed_the_beast.ftbutilities.events;

import com.feed_the_beast.ftbutilities.data.backups.Backup;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BackupEvent extends FTBUtilitiesEvent
{
	private final Backup backup;
	private final Exception error;

	public BackupEvent(Backup b, @Nullable Exception ex)
	{
		backup = b;
		error = ex;
	}

	public Backup getBackup()
	{
		return backup;
	}

	@Nullable
	public Exception getError()
	{
		return error;
	}
}