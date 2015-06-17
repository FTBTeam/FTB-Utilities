package latmod.ftbu.mod;
import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.*;
import latmod.ftbu.core.item.ICreativeSafeItem;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.ISecureTile;
import latmod.ftbu.core.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FTBUEventHandler
{
	public static final FTBUEventHandler instance = new FTBUEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(!(e.player instanceof EntityPlayerMP)) return;
		EntityPlayerMP ep = (EntityPlayerMP)e.player;
		
		LMPlayer p = LMPlayer.getPlayer(ep);
		
		boolean first = (p == null);
		boolean sendAll = false;
		
		if(first)
		{
			p = new LMPlayer(LMDataLoader.nextPlayerID(), ep.getGameProfile());
			LMPlayer.map.put(p.playerID, p);
		}
		else
		{
			if(!p.getName().equals(ep.getGameProfile().getName()))
			{
				p = new LMPlayer(p.playerID, ep.getGameProfile());
				LMPlayer.map.put(p.playerID, p);
				sendAll = true;
			}
		}
		
		p.setOnline(true);
		
		new LMPlayerEvent.LoggedIn(p, ep, first).post();
		updateAllData(sendAll ? null : ep);
		MessageLM.NET.sendToAll(new MessageLMPlayerLoggedIn(p));
		
		p.updateInfo(null);
		for(LMPlayer p1 : LMPlayer.map.values)
		{ if(p1 != p) p1.updateInfo(ep); }
	}
	
	@SubscribeEvent
	public void playerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		if(p != null && e.player instanceof EntityPlayerMP)
		{
			p.setOnline(false);
			
			for(int i = 0; i < 4; i++)
				p.lastArmor[i] = e.player.inventory.armorInventory[i];
			p.lastArmor[4] = e.player.inventory.getCurrentItem();
			
			new LMPlayerEvent.LoggedOut(p, (EntityPlayerMP)e.player).post();
			MessageLM.NET.sendToAll(new MessageLMPlayerLoggedOut(p));
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
				LMWorld.load(tag);
			}
			
			e1.post();
			
			{
				NBTTagCompound tag = NBTHelper.readMap(e1.getFile("LMPlayers.dat"));
				if(tag != null && tag.hasKey("Players"))
				{
					LMDataLoader.lastPlayerID = tag.getInteger("LastID");
					LMDataLoader.readPlayersFromNBT(tag.getCompoundTag("Players"), true);
				}
			}
			
			for(int i = 0; i < LMPlayer.map.values.size(); i++)
				LMPlayer.map.values.get(i).setOnline(false);
			
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
				LMWorld.save(tag);
				NBTHelper.writeMap(e1.getFile("LMWorld.dat"), tag);
			}
			
			{
				NBTTagCompound tag = new NBTTagCompound();
				NBTTagCompound players = new NBTTagCompound();
				LMDataLoader.writePlayersToNBT(players, true);
				tag.setTag("Players", players);
				tag.setInteger("LastID", LMDataLoader.lastPlayerID);
				NBTHelper.writeMap(e1.getFile("LMPlayers.dat"), tag);
			}
			
			// Export player list //
			
			try
			{
				FastList<String> l = new FastList<String>();
				FastList<Integer> list = new FastList<Integer>();
				list.addAll(LMPlayer.map.keys);
				list.sort(null);
				
				for(int i = 0; i < list.size(); i++)
				{
					LMPlayer p = LMPlayer.getPlayer(list.get(i));
					
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
	
	public void updateAllData(EntityPlayerMP ep)
	{
		if(ep != null) MessageLM.NET.sendTo(new MessageUpdateAllData(), ep);
		else MessageLM.NET.sendToAll(new MessageUpdateAllData());
	}
	
	@SubscribeEvent
    public void onConfigChanged(cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent e)
    {
		if(e.modID.equalsIgnoreCase(FTBUFinals.MOD_ID))
			FTBUConfig.instance.load();
	}
	
	@SubscribeEvent
	public void onBlockClick(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.entityPlayer.capabilities.isCreativeMode && e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && e.entityPlayer.getHeldItem() != null && e.entityPlayer.getHeldItem().getItem() instanceof ICreativeSafeItem)
		{
			if(!e.world.isRemote) e.world.markBlockForUpdate(e.x, e.y, e.z);
			else e.world.markBlockRangeForRenderUpdate(e.x, e.y, e.z, e.x, e.y, e.z);
			e.setCanceled(true);
			return;
		}
		if(!e.world.isRemote && (e.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR) && !canInteract(e)) e.setCanceled(true);
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.entityPlayer.capabilities.isCreativeMode && FTBUConfig.General.allowCreativeInteractSecure) return true;
		
		TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
		
		if(te != null && !te.isInvalid() && te instanceof ISecureTile)
		{
			if(!((ISecureTile)te).canPlayerInteract(e.entityPlayer, e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
			{ ((ISecureTile)te).onPlayerNotOwner(e.entityPlayer, e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK); return false; }
		}
		
		return true;
	}
}