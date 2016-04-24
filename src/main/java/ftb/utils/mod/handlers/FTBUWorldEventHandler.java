package ftb.utils.mod.handlers;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.FTBLib;
import ftb.lib.LMNBTUtils;
import ftb.lib.api.EventFTBSync;
import ftb.lib.api.item.LMInvUtils;
import ftb.utils.api.EventLMPlayerServer;
import ftb.utils.api.EventLMWorldServer;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.FTBU;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.mod.config.FTBUConfigLogin;
import ftb.utils.net.MessageAreaUpdate;
import ftb.utils.net.MessageLMPlayerLoggedIn;
import ftb.utils.world.Backups;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunks;
import latmod.lib.LMFileUtils;
import latmod.lib.LMJsonUtils;
import latmod.lib.MathHelperLM;
import latmod.lib.util.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FTBUWorldEventHandler // FTBLIntegration
{
	@SubscribeEvent
	public void syncData(EventFTBSync e)
	{
		if(e.world.side.isServer())
		{
			EntityPlayerMP ep = (EntityPlayerMP) e.player;
			LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
			
			boolean first_login = (p == null);
			boolean send_all = false;
			
			if(first_login)
			{
				p = new LMPlayerServer(ep.getGameProfile());
				LMWorldServer.inst.playerMap.put(p.getProfile().getId(), p);
				send_all = true;
			}
			else if(!p.getProfile().getName().equals(ep.getGameProfile().getName()))
			{
				p.setProfile(ep.getGameProfile());
				send_all = true;
			}
			
			p.setPlayer(ep);
			
			// post
			
			p.refreshStats();
			
			new EventLMPlayerServer.LoggedIn(p, ep, first_login).post();
			new MessageLMPlayerLoggedIn(p, first_login, true).sendTo(send_all ? null : ep);
			for(EntityPlayerMP ep1 : FTBLib.getAllOnlinePlayers(ep))
				new MessageLMPlayerLoggedIn(p, first_login, false).sendTo(ep1);
			
			if(first_login)
			{
				for(ItemStack is : FTBUConfigLogin.starting_items.items)
				{
					LMInvUtils.giveItem(ep, is);
				}
			}
			
			//new MessageLMPlayerInfo(p.playerID).sendTo(null);
			
			for(IChatComponent c : FTBUConfigLogin.motd.components)
			{
				ep.addChatMessage(c);
			}
			
			Backups.hadPlayer = true;
			
			p.checkNewFriends();
			new MessageAreaUpdate(p, p.getPos(), 3, 3).sendTo(ep);
			ServerBadges.sendToPlayer(ep);
			
			FTBUChunkEventHandler.instance.markDirty(null);
			
			// sync data //
			
			NBTTagCompound tag = new NBTTagCompound();
			LMWorldServer.inst.writeDataToNet(tag, p, e.login);
			e.syncData.setTag("FTBU", tag);
		}
		else
		{
			FTBU.proxy.syncData(e);
		}
	}
	
	@SubscribeEvent
	public void worldLoaded(net.minecraftforge.event.world.WorldEvent.Load e)
	{
		if(e.world instanceof WorldServer) FTBUChunkEventHandler.instance.markDirty(e.world);
	}
	
	@SubscribeEvent
	public void worldSaved(net.minecraftforge.event.world.WorldEvent.Save e)
	{
		if(e.world.provider.dimensionId == 0 && e.world instanceof WorldServer)
		{
			new EventLMWorldServer.Saved(LMWorldServer.inst).post();
			
			JsonObject group = new JsonObject();
			LMWorldServer.inst.save(group, Phase.PRE);
			
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound players = new NBTTagCompound();
			LMWorldServer.inst.writePlayersToServer(players);
			tag.setTag("Players", players);
			LMNBTUtils.writeMap(new File(LMWorldServer.inst.latmodFolder, "LMPlayers.dat"), tag);
			
			LMWorldServer.inst.save(group, Phase.POST);
			LMJsonUtils.toJson(new File(LMWorldServer.inst.latmodFolder, "LMWorld.json"), group);
			
			group = new JsonObject();
			LMWorldServer.inst.claimedChunks.save(group);
			LMJsonUtils.toJson(new File(LMWorldServer.inst.latmodFolder, "ClaimedChunks.json"), group);
			
			// Export player list //
			
			try
			{
				ArrayList<String> l = new ArrayList<>();
				
				for(LMPlayer p : LMWorldServer.inst.playerMap.values())
				{
					StringBuilder sb = new StringBuilder();
					sb.append(p.getStringUUID());
					sb.append(':');
					sb.append(' ');
					sb.append(p.getProfile().getName());
					l.add(sb.toString());
				}
				
				Collections.sort(l);
				
				LMFileUtils.save(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/LMPlayers.txt"), l);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void onMobSpawned(net.minecraftforge.event.entity.EntityJoinWorldEvent e)
	{
		if(!e.world.isRemote && !isEntityAllowed(e.entity))
		{
			e.entity.setDead();
			e.setCanceled(true);
		}
	}
	
	private boolean isEntityAllowed(Entity e)
	{
		if(e instanceof EntityPlayer) return true;
		
		if(FTBUConfigGeneral.isEntityBanned(e.getClass())) return false;
		
		if(FTBUConfigGeneral.safe_spawn.getAsBoolean() && ClaimedChunks.isInSpawnD(e.dimension, e.posX, e.posZ))
		{
			if(e instanceof IMob) return false;
			else if(e instanceof EntityChicken && e.riddenByEntity != null) return false;
		}
		
		return true;
	}
	
	@SubscribeEvent
	public void onExplosionStart(net.minecraftforge.event.world.ExplosionEvent.Start e)
	{
		if(e.world.isRemote) return;
		int dim = e.world.provider.dimensionId;
		int cx = MathHelperLM.chunk(e.explosion.explosionX);
		int cz = MathHelperLM.chunk(e.explosion.explosionZ);
		if(!LMWorldServer.inst.claimedChunks.allowExplosion(dim, cx, cz)) e.setCanceled(true);
	}
	
}