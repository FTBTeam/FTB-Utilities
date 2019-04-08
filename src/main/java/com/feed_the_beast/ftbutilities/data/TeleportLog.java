package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Comparator;

public class TeleportLog implements INBTSerializable<NBTTagCompound>, Comparable<TeleportLog>
{
	private static final String NBT_KEY_X = "x";
	private static final String NBT_KEY_Y = "y";
	private static final String NBT_KEY_Z = "z";
	private static final String NBT_KEY_DIMENSION = "dimension";
	private static final String NBT_KEY_TELEPORT_TYPE = "teleport_type";
	private static final String NBT_KEY_CREATED_AT = "created_at";

	private static Comparator<TeleportLog> comparator = Comparator.comparing((log) -> log == null ? null : log.getCreatedAt(), Comparator.nullsFirst(Long::compareTo));

	public TeleportType teleportType;

	private BlockDimPos from;
	private long createdAt;

	public TeleportLog(NBTTagCompound nbt)
	{
		deserializeNBT(nbt);
	}

	public TeleportLog(TeleportType teleportType, BlockDimPos from, long createdAt)
	{
		this.teleportType = teleportType;
		this.from = from;
		this.createdAt = createdAt;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger(NBT_KEY_DIMENSION, from.dim);
		nbt.setInteger(NBT_KEY_X, from.posX);
		nbt.setInteger(NBT_KEY_Y, from.posY);
		nbt.setInteger(NBT_KEY_Z, from.posZ);
		nbt.setInteger(NBT_KEY_TELEPORT_TYPE, teleportType.ordinal());
		nbt.setLong(NBT_KEY_CREATED_AT, createdAt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		final int posX, posY, posZ, dimension, teleportType;
		posX = nbt.getInteger(NBT_KEY_X);
		posY = nbt.getInteger(NBT_KEY_Y);
		posZ = nbt.getInteger(NBT_KEY_Z);
		dimension = nbt.getInteger(NBT_KEY_DIMENSION);
		teleportType = nbt.getInteger(NBT_KEY_TELEPORT_TYPE);
		this.from = new BlockDimPos(posX, posY, posZ, dimension);
		this.teleportType = TeleportType.values()[teleportType];
	}

	public BlockDimPos getBlockDimPos()
	{
		return this.from;
	}

	public TeleporterDimPos teleporter()
	{
		return getBlockDimPos().teleporter();
	}

	public long getCreatedAt()
	{
		return this.createdAt;
	}

	@Override
	public int compareTo(TeleportLog o)
	{
		return comparator.compare(this, o);
	}
}
