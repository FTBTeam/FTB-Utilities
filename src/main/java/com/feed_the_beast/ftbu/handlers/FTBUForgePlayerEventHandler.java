package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.ForgePlayerEvent;
import com.feed_the_beast.ftbl.api.item.LMInvUtils;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.badges.ServerBadges;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.feed_the_beast.ftbu.world.Backups;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUForgePlayerEventHandler
{
	@SubscribeEvent
	public void addPlayerData(ForgePlayerEvent.AttachCapabilities event)
	{
		if(event.player.getSide().isServer())
		{
			event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), new FTBUPlayerDataMP(event.player));
		}
		else
		{
			event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), new FTBUPlayerDataSP(event.player));
		}
	}
	
	@SubscribeEvent
	public void onDataSynced(ForgePlayerEvent.Sync event)
	{
		if(event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
		{
			FTBUPlayerData data = event.player.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null);
			
			if(event.player.getSide().isServer())
			{
				NBTTagCompound tag = new NBTTagCompound();
				data.writeSyncData(tag, event.self);
				event.data.setTag("FTBU", tag);
			}
			else
			{
				data.readSyncData(event.data.getCompoundTag("FTBU"), event.self);
			}
		}
	}
	
	@SubscribeEvent
	public void onLoggedIn(ForgePlayerEvent.LoggedIn event)
	{
		if(event.player.getSide().isServer() && event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
		{
			EntityPlayerMP ep = event.player.toPlayerMP().getPlayer();
			
			if(event.first)
			{
				if(FTBUConfigModules.starting_items.getAsBoolean())
				{
					for(ItemStack is : FTBUConfigLogin.starting_items.items)
					{
						LMInvUtils.giveItem(ep, is);
					}
				}
			}
			
			if(FTBUConfigModules.motd.getAsBoolean())
			{
				FTBUConfigLogin.motd.components.forEach(ep::addChatMessage);
			}
			
			Backups.hadPlayer = true;
			ServerBadges.sendToPlayer(ep);
			
			new MessageAreaUpdate(event.player.toPlayerMP(), event.player.toPlayerMP().getPos(), 1).sendTo(ep);
			FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	@SubscribeEvent
	public void onLoggedOut(ForgePlayerEvent.LoggedOut event)
	{
		if(event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
		{
			FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
}