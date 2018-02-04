package com.feed_the_beast.ftbutilities.data.backups;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Comparator;

/**
 * @author LatvianModder
 */
public class Backup
{
	public static final Comparator<Backup> COMPARATOR = Comparator.comparingLong(o -> o.time);

	public final long time;
	public final String fileId;
	public final int index;
	public final boolean success;

	public Backup(long t, String f, int i, boolean s)
	{
		time = t;
		fileId = f;
		index = i;
		success = s;
	}

	public Backup(JsonObject o)
	{
		this(o.get("time").getAsLong(), o.get("file").getAsString(), o.get("index").getAsInt(), o.get("success").getAsBoolean());
	}

	public JsonObject toJsonObject()
	{
		JsonObject o = new JsonObject();
		o.addProperty("time", time);
		o.addProperty("file", fileId);
		o.addProperty("index", index);
		o.addProperty("success", success);
		return o;
	}

	public int hashCode()
	{
		return Long.hashCode(time);
	}

	public String toString()
	{
		return fileId;
	}

	public boolean equals(Object o)
	{
		return o == this || (o instanceof Backup && ((Backup) o).time == time);
	}

	public File getFile()
	{
		return new File(Backups.INSTANCE.backupsFolder, fileId);
	}
}