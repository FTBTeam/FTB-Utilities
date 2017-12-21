package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ISyncData;
import com.feed_the_beast.ftbu.client.CachedClientData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class FTBUSyncData implements ISyncData
{
	@Override
	public NBTTagCompound writeSyncData(EntityPlayerMP player, ForgePlayer forgePlayer)
	{
		return new NBTTagCompound();
	}

	@Override
	public void readSyncData(NBTTagCompound nbt)
	{
		CachedClientData.clear();
	}
}