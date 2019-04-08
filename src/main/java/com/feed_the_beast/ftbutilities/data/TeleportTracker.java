package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Arrays;
import java.util.Collections;

public class TeleportTracker implements INBTSerializable<NBTTagCompound>
{
	private TeleportLog[] logs;
	private IPermissionHandler permissionHandler;

	public TeleportTracker()
	{
		this(PermissionAPI.getPermissionHandler());
	}

	public TeleportTracker(IPermissionHandler permissionHandler)
	{
		this.logs = new TeleportLog[TeleportType.values().length];
		this.permissionHandler = permissionHandler;
	}

	public void logTeleport(TeleportType teleportType, BlockDimPos from, long worldTime)
	{
		logs[teleportType.ordinal()] = new TeleportLog(teleportType, from, worldTime);
	}

	public TeleportLog getLastDeath()
	{
		return logs[TeleportType.RESPAWN.ordinal()];
	}

	private TeleportLog[] getSortedLogs()
	{
		TeleportLog[] toSort = Arrays.stream(logs).filter((l) -> l != null).toArray(TeleportLog[]::new);
		Arrays.sort(toSort, Collections.reverseOrder());
		return toSort;
	}

	// Returns latest available according to permissions.
	public TeleportLog getLastAvailableLog(GameProfile gameProfile)
	{
		for (TeleportLog l : getSortedLogs())
		{
			if (permissionHandler.hasPermission(gameProfile, l.teleportType.getPermission(), null))
			{
				return l;
			}
		}
		return null;
	}

	public TeleportLog getLastLog()
	{
		TeleportLog[] logs = getSortedLogs();
		return logs[0];
	}

	public void clearLog(TeleportType teleportType)
	{
		logs[teleportType.ordinal()] = null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		for (int i = 0; i < logs.length; i++)
		{
			nbt.setTag(String.valueOf(i), logs[i].serializeNBT());
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}
		for (int i = 0; i < logs.length; i++)
		{
			logs[i] = new TeleportLog(nbt.getCompoundTag(String.valueOf(i)));
		}
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (int i = 0; i < logs.length; i++)
		{
			final TeleportLog l = logs[i];
			builder.append(l.teleportType.toString() + ":" + l.getBlockDimPos());
			if (i != logs.length - 1)
			{
				builder.append(",");
			}
		}
		builder.append("}");
		return builder.toString();
	}
}
