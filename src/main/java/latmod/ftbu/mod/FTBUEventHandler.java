package latmod.ftbu.mod;
import java.io.File;
import java.util.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.*;
import latmod.ftbu.core.item.ICreativeSafeItem;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.ISecureTile;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.claims.*;
import latmod.ftbu.mod.cmd.CmdMotd;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FTBUEventHandler // FTBUTickHandler
{
	public static final FTBUEventHandler instance = new FTBUEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(!(e.player instanceof EntityPlayerMP)) return;
		EntityPlayerMP ep = (EntityPlayerMP)e.player;
		
		LMPlayerServer p = LMWorld.server.getPlayer(ep);
		
		boolean first = (p == null);
		boolean sendAll = false;
		
		if(first)
		{
			p = new LMPlayerServer(LMWorld.server, LMPlayerServer.nextPlayerID(), ep.getGameProfile());
			LMWorld.server.players.add(p);
			sendAll = true;
		}
		else
		{
			if(!p.getName().equals(ep.getGameProfile().getName()))
			{
				p.setName(p.gameProfile.getName());
				sendAll = true;
			}
		}
		
		p.setPlayer(ep);
		p.updateLastSeen();
		
		new LMPlayerEvent.LoggedIn(p, ep, first).post();
		MessageLM.sendTo(sendAll ? null : ep, new MessageLMWorldUpdate(LMWorld.server.worldID));
		IServerConfig.Registry.updateConfig(ep, null);
		MessageLM.sendTo(null, new MessageLMPlayerLoggedIn(p, first));
		MessageLM.sendTo(null, p.getInfo());
		
		if(first)
		{
			FastList<ItemStack> items = FTBUConfig.login.getStartingItems(ep.getUniqueID());
			if(items != null && !items.isEmpty()) for(ItemStack is : items)
				InvUtils.giveItem(ep, is);
		}
		
		/*p.sendInfo(null);
		for(LMPlayer p1 : LMPlayer.map.values)
			p1.sendInfo(ep);
		*/
		
		MessageLM.sendTo(ep, p.getInfo());
		CmdMotd.printMotd(ep);
		Backups.shouldRun = true;
	}
	
	@SubscribeEvent
	public void playerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e)
	{
		LMPlayerServer p = LMWorld.server.getPlayer(e.player);
		
		if(p != null && e.player instanceof EntityPlayerMP)
		{
			p.updateLastSeen();
			
			for(int i = 0; i < 4; i++)
				p.lastArmor[i] = e.player.inventory.armorInventory[i];
			p.lastArmor[4] = e.player.inventory.getCurrentItem();
			
			new LMPlayerEvent.LoggedOut(p, (EntityPlayerMP)e.player).post();
			MessageLM.sendTo(null, new MessageLMPlayerLoggedOut(p));
			MessageLM.sendTo(null, p.getInfo());
			p.setPlayer(null);
			Backups.shouldRun = true;
		}
	}
	
	@SubscribeEvent
	public void worldLoaded(net.minecraftforge.event.world.WorldEvent.Load e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			IServerConfig.Registry.load();
			
			LoadLMDataEvent e1 = new LoadLMDataEvent(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/"), EventLM.Phase.PRE);
			
			{
				NBTTagCompound tag = NBTHelper.readMap(e1.getFile("LMWorld.dat"));
				if(tag == null) tag = new NBTTagCompound();
				LMWorld.server = new LMWorldServer(tag.hasKey("UUID") ? LatCoreMC.getUUIDFromString(tag.getString("UUID")) : UUID.randomUUID());
				LMWorld.server.load(tag);
			}
			
			e1.post();
			
			{
				NBTTagCompound tag = NBTHelper.readMap(e1.getFile("LMPlayers.dat"));
				if(tag != null && tag.hasKey("Players"))
				{
					LMPlayerServer.lastPlayerID = tag.getInteger("LastID");
					LMWorld.server.readPlayersFromServer(tag.getCompoundTag("Players"));
				}
			}
			
			for(int i = 0; i < LMWorld.server.players.size(); i++)
				LMWorld.server.players.get(i).setPlayer(null);
			
			new LoadLMDataEvent(e1.latmodFolder, EventLM.Phase.POST).post();
			
			LatCoreMC.logger.info("LatCoreMC data loaded");
		}
	}
	
	@SubscribeEvent
	public void worldSaved(net.minecraftforge.event.world.WorldEvent.Save e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			SaveLMDataEvent e1 = new SaveLMDataEvent(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/"));
			e1.post();
			
			{
				NBTTagCompound tag = new NBTTagCompound();
				LMWorld.server.save(tag);
				tag.setString("UUID", LMWorld.server.worldIDS);
				NBTHelper.writeMap(e1.getFile("LMWorld.dat"), tag);
			}
			
			{
				NBTTagCompound tag = new NBTTagCompound();
				NBTTagCompound players = new NBTTagCompound();
				LMWorld.server.writePlayersToServer(players);
				tag.setTag("Players", players);
				tag.setInteger("LastID", LMPlayerServer.lastPlayerID);
				NBTHelper.writeMap(e1.getFile("LMPlayers.dat"), tag);
			}
			
			// Export player list //
			
			try
			{
				FastList<String> l = new FastList<String>();
				int[] list = LMWorld.server.getAllPlayerIDs();
				Arrays.sort(list);
				
				for(int i = 0; i < list.length; i++)
				{
					LMPlayer p = LMWorld.server.getPlayer(list[i]);
					
					StringBuilder sb = new StringBuilder();
					sb.append(LatCore.fillString("" + p.playerID, ' ', 6));
					sb.append(LatCore.fillString(p.getName(), ' ', 21));
					sb.append(p.uuidString);
					l.add(sb.toString());
				}
				
				LatCore.saveFile(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/LMPlayers.txt"), l);
			}
			catch(Exception ex)
			{
				LatCoreMC.logger.warn("Error occured while saving LatCoreMC.dat!");
				ex.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.action == Action.RIGHT_CLICK_AIR) return;
		
		if(e.entityPlayer.capabilities.isCreativeMode && e.action == Action.LEFT_CLICK_BLOCK && e.entityPlayer.getHeldItem() != null && e.entityPlayer.getHeldItem().getItem() instanceof ICreativeSafeItem)
		{
			if(!e.world.isRemote) e.world.markBlockForUpdate(e.x, e.y, e.z);
			else e.world.markBlockRangeForRenderUpdate(e.x, e.y, e.z, e.x, e.y, e.z);
			e.setCanceled(true);
			return;
		}
		if(!e.world.isRemote && !canInteract(e)) e.setCanceled(true);
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(FTBUConfig.general.allowInteractSecure(e.entityPlayer)) return true;
		if(FTBUConfig.general.isDedi() && !ChunkType.getD(e.world.provider.dimensionId, e.x, e.z, LMWorld.server.getPlayer(e.entityPlayer)).isFriendly()) return false;
		
		TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
		
		if(te != null && !te.isInvalid() && te instanceof ISecureTile)
		{
			if(!((ISecureTile)te).canPlayerInteract(e.entityPlayer, e.action == Action.LEFT_CLICK_BLOCK))
			{ ((ISecureTile)te).onPlayerNotOwner(e.entityPlayer, e.action == Action.LEFT_CLICK_BLOCK); return false; }
		}
		
		return true;
	}
	
	@SubscribeEvent
	public void onPlayerDeath(net.minecraftforge.event.entity.living.LivingDeathEvent e)
	{
		if(e.entity instanceof EntityPlayerMP)
		{
			LMPlayerServer p = LMWorld.server.getPlayer(e.entity);
			p.deaths++;
			
			if(p.lastDeath == null) p.lastDeath = new EntityPos(e.entity);
			else p.lastDeath.set(e.entity);
			
			MessageLM.NET.sendToAll(new MessageLMPlayerDied(p));
		}
	}
	
	@SubscribeEvent
	public void onMobSpawned(net.minecraftforge.event.entity.EntityJoinWorldEvent e)
	{
		if(!FTBUConfig.general.safeSpawn || !FTBUConfig.general.isDedi()) return;
		
		if((e.entity instanceof IMob || (e.entity instanceof EntityChicken && e.entity.riddenByEntity != null)) && Claims.isInSpawnD(e.world.provider.dimensionId, e.entity.posX, e.entity.posZ))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(net.minecraftforge.event.entity.living.LivingAttackEvent e)
	{
		if(!FTBUConfig.general.isDedi()) return;
		
		int dim = e.entity.dimension;
		if(dim != 0 || !(e.entity instanceof EntityPlayerMP) || e.entity instanceof FakePlayer) return;
		
		Entity entity = e.source.getSourceOfDamage();
		
		if(entity != null && entity instanceof EntityPlayerMP && !(entity instanceof FakePlayer))
		{
			if(FTBUConfig.general.allowInteractSecure((EntityPlayerMP)entity)) return;
			
			int cx = MathHelperLM.chunk(e.entity.posX);
			int cz = MathHelperLM.chunk(e.entity.posZ);
			
			if(Claims.isOutsideWorldBorder(dim, cx, cz) || (FTBUConfig.general.safeSpawn && Claims.isInSpawn(dim, cx, cz))) e.setCanceled(true);
			else
			{
				ClaimedChunk c = Claims.get(dim, cx, cz);
				if(c != null && c.claims.isSafe()) e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onExplosion(net.minecraftforge.event.world.ExplosionEvent.Start e)
	{
		if(!FTBUConfig.general.isDedi()) return;
		
		int dim = e.world.provider.dimensionId;
		if(dim != 0) return;
		int cx = MathHelperLM.chunk(e.explosion.explosionX);
		int cz = MathHelperLM.chunk(e.explosion.explosionZ);
		
		if(Claims.isOutsideWorldBorder(dim, cx, cz) || (FTBUConfig.general.safeSpawn && Claims.isInSpawn(dim, cx, cz))) e.setCanceled(true);
		else
		{
			ClaimedChunk c = Claims.get(dim, cx, cz);
			if(c != null && c.claims.isSafe()) e.setCanceled(true);
		}
	}
}