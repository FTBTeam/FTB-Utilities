package latmod.ftbu.mod.handlers;

import java.io.File;
import java.util.Arrays;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.*;
import ftb.lib.api.config.ConfigListRegistry;
import latmod.ftbu.api.EventLMWorldServer;
import latmod.ftbu.mod.FTBUTicks;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

public class FTBUWorldEventHandler
{
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent e)
	{
		if(!e.world.isRemote && e.side == Side.SERVER && e.phase == TickEvent.Phase.END && e.type == TickEvent.Type.WORLD)
			FTBUTicks.update();
	}
	
	@SubscribeEvent
	public void worldLoaded(net.minecraftforge.event.world.WorldEvent.Load e)
	{
		if(FTBLib.isServer() && e.world.provider.dimensionId == 0 && e.world instanceof WorldServer)
		{
			ConfigListRegistry.reloadAll();
			
			File latmodFolder = new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/");
			NBTTagCompound tagWorldData = LMNBTUtils.readMap(new File(latmodFolder, "LMWorld.dat"));
			if(tagWorldData == null) tagWorldData = new NBTTagCompound();
			LMWorldServer.inst = new LMWorldServer((WorldServer)e.world, latmodFolder);
			LMWorldServer.inst.load(tagWorldData);
			
			new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.PRE).post();
			
			NBTTagCompound tagPlayers = LMNBTUtils.readMap(new File(latmodFolder, "LMPlayers.dat"));
			if(tagPlayers != null && tagPlayers.hasKey("Players"))
			{
				LMPlayerServer.lastPlayerID = tagPlayers.getInteger("LastID");
				LMWorldServer.inst.readPlayersFromServer(tagPlayers.getCompoundTag("Players"));
			}
			
			for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
				LMWorldServer.inst.players.get(i).toPlayerMP().setPlayer(null);
			
			new EventLMWorldServer.Loaded(LMWorldServer.inst, Phase.POST).post();
		}
	}
	
	@SubscribeEvent
	public void worldSaved(net.minecraftforge.event.world.WorldEvent.Save e)
	{
		if(FTBLib.isServer() && e.world.provider.dimensionId == 0 && e.world instanceof WorldServer)
		{
			new EventLMWorldServer.Saved(LMWorldServer.inst).post();
			
			NBTTagCompound tag = new NBTTagCompound();
			LMWorldServer.inst.save(tag);
			LMNBTUtils.writeMap(new File(LMWorldServer.inst.latmodFolder, "LMWorld.dat"), tag);
			
			tag = new NBTTagCompound();
			NBTTagCompound players = new NBTTagCompound();
			LMWorldServer.inst.writePlayersToServer(players);
			tag.setTag("Players", players);
			tag.setInteger("LastID", LMPlayerServer.lastPlayerID);
			LMNBTUtils.writeMap(new File(LMWorldServer.inst.latmodFolder, "LMPlayers.dat"), tag);
			
			// Export player list //
			
			try
			{
				FastList<String> l = new FastList<String>();
				int[] list = LMWorldServer.inst.getAllPlayerIDs();
				Arrays.sort(list);
				
				for(int i = 0; i < list.length; i++)
				{
					LMPlayer p = LMWorldServer.inst.getPlayer(list[i]);
					
					StringBuilder sb = new StringBuilder();
					sb.append(LMStringUtils.fillString("" + p.playerID, ' ', 6));
					sb.append(LMStringUtils.fillString(p.getName(), ' ', 21));
					sb.append(p.uuidString);
					l.add(sb.toString());
				}
				
				LMFileUtils.save(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/LMPlayers.txt"), l);
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
	}
	
	@SubscribeEvent
	public void onMobSpawned(net.minecraftforge.event.entity.EntityJoinWorldEvent e)
	{
		if(!e.world.isRemote && !isEntityAllowed(e.entity))
		{ e.entity.setDead(); e.setCanceled(true); }
	}
	
	private boolean isEntityAllowed(Entity e)
	{
		if(e instanceof EntityPlayer) return true;
		
		if(FTBUConfigGeneral.isEntityBanned(e.getClass()))
			return false;
		
		if(FTBUConfigGeneral.safeSpawn.get() && Claims.isInSpawnF(e.dimension, e.posX, e.posZ))
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
		if(!Claims.allowExplosion(dim, cx, cz)) e.setCanceled(true);
	}
	
}