package latmod.core.mod;
import java.io.File;

import latmod.core.*;
import latmod.core.event.*;
import latmod.core.net.*;
import latmod.core.tile.ISecureTile;
import latmod.core.util.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LCEventHandler
{
	public static final LCEventHandler instance = new LCEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(!(e.player instanceof EntityPlayerMP)) return;
		
		LMPlayer p = LMPlayer.getPlayer(e.player);
		
		boolean first = p == null || !p.isOld;
		boolean sendAll = false;
		
		String cmdName = e.player.getCommandSenderName();
		
		if(p == null)
		{
			first = true;
			p = new LMPlayer(LMDataLoader.nextPlayerID(), e.player.getUniqueID(), cmdName);
			LMPlayer.map.put(p.playerID, p);
		}
		else
		{
			if(!p.username.equals(cmdName))
			{
				p = new LMPlayer(p.playerID, e.player.getUniqueID(), cmdName);
				LMPlayer.map.put(p.playerID, p);
				sendAll = true;
			}
		}
		
		p.isOld = !first;
		p.setOnline(true);
		
		new LMPlayerEvent.LoggedIn(p, (EntityPlayerMP)e.player, !p.isOld).post();
		updateAllData(sendAll ? null : (EntityPlayerMP)e.player);
		MessageLM.NET.sendToAll(new MessageLMPlayerLoggedIn(p));
		
		p.isOld = true;
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
			e1.post();
			
			{
				NBTTagCompound tag = NBTHelper.readMap(e1.getFile("LMPlayers.dat"));
				if(tag != null)
				{
					if(tag.hasKey("Players"))
					{
						LMDataLoader.lastPlayerID = tag.getInteger("LastID");
						LMDataLoader.readPlayersFromNBT(tag.getCompoundTag("Players"), true);
					}
					else
					{
						LMDataLoader.readPlayersFromNBT(tag, true);
						
						// TODO: Deprecated, will be removed //
						NBTTagCompound common = NBTHelper.readMap(e1.getFile("CommonData.dat"));
						if(common != null)
						{
							LMDataLoader.lastPlayerID = common.getInteger("LastPlayerID");
							e1.getFile("CommonData.dat").delete();
						}
					}
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
					
					String id = LatCore.fillString("" + p.playerID, ' ', 6);
					String u = LatCore.fillString(p.username, ' ', 21);
					String s = "" + p.uuid;
					
					l.add(id + u + s);
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
		if(e.modID.equalsIgnoreCase(LC.MOD_ID))
			LCConfig.instance.load();
	}
	
	@SubscribeEvent
	public void onBlockClick(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.world.isRemote) return;
		
		if(e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
		if(!canInteract(e)) e.setCanceled(true);
	}
	
	private boolean canInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e)
	{
		if(e.entityPlayer.capabilities.isCreativeMode && LCConfig.General.allowCreativeInteractSecure) return true;
		
		TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
		
		if(te != null && !te.isInvalid() && te instanceof ISecureTile)
		{
			LMSecurity s = ((ISecureTile)te).getSecurity();
			
			if(s != null && !s.level.isPublic() && s.owner != null && !s.canInteract(e.entityPlayer))
				return false;
		}
		
		return true;
	}
}