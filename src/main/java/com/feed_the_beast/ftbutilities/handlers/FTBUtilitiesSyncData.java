package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ISyncData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesSyncData implements ISyncData
{
	@Override
	public NBTTagCompound writeSyncData(EntityPlayerMP player, ForgePlayer forgePlayer)
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (FTBUtilitiesUniverseData.shutdownTime > 0L)
		{
			nbt.setLong("ShutdownTime", FTBUtilitiesUniverseData.shutdownTime - System.currentTimeMillis());
		}

		return nbt;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readSyncData(NBTTagCompound nbt)
	{
		FTBUtilitiesClientEventHandler.readSyncData(nbt);
	}
}