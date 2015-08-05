package latmod.ftbu.mod;
import java.io.File;
import java.util.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.event.*;
import latmod.ftbu.core.inv.LMInvUtils;
import latmod.ftbu.core.item.ICreativeSafeItem;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.ISecureTile;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.claims.*;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.mod.cmd.CmdMotd;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;

public class FTBUEventHandler // FTBUTickHandler
{
	public static final FTBUEventHandler instance = new FTBUEventHandler();
	
	@SubscribeEvent
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		if(!(e.player instanceof EntityPlayerMP)) return;
		EntityPlayerMP ep = (EntityPlayerMP)e.player;
		
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
		
		boolean first = (p == null);
		boolean sendAll = false;
		
		if(first)
		{
			p = new LMPlayerServer(LMWorldServer.inst, LMPlayerServer.nextPlayerID(), ep.getGameProfile());
			LMWorldServer.inst.players.add(p);
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
		
		new LMPlayerServerEvent.LoggedIn(p, ep, first).post();
		LMNetHelper.sendTo(sendAll ? null : ep, new MessageLMWorldUpdate(LMWorldServer.inst.worldID));
		IServerConfig.Registry.updateConfig(ep, null);
		LMNetHelper.sendTo(null, new MessageLMPlayerLoggedIn(p, first));
		
		if(first)
		{
			List<ItemStack> items = FTBUConfig.login.getStartingItems(ep.getUniqueID());
			if(items != null && !items.isEmpty()) for(ItemStack is : items)
				LMInvUtils.giveItem(ep, is);
		}
		
		LMNetHelper.sendTo(null, new MessageLMPlayerInfo(p));
		CmdMotd.printMotd(ep);
		Backups.shouldRun = true;
		
		if(first) teleportToSpawn(ep);
	}
	
	@SubscribeEvent
	public void playerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(e.player);
		
		if(p != null && e.player instanceof EntityPlayerMP)
		{
			p.updateLastSeen();
			
			for(int i = 0; i < 4; i++)
				p.lastArmor[i] = e.player.inventory.armorInventory[i];
			p.lastArmor[4] = e.player.inventory.getCurrentItem();
			
			new LMPlayerServerEvent.LoggedOut(p, (EntityPlayerMP)e.player).post();
			LMNetHelper.sendTo(null, new MessageLMPlayerLoggedOut(p));
			LMNetHelper.sendTo(null, new MessageLMPlayerInfo(p));
			p.setPlayer(null);
			Backups.shouldRun = true;
		}
	}
	
	@SubscribeEvent
	public void playerRespawned(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent e)
	{ if(e.player instanceof EntityPlayerMP) teleportToSpawn((EntityPlayerMP)e.player); }
	
	public void teleportToSpawn(EntityPlayerMP ep)
	{
		if(ep.worldObj.provider.dimensionId != 0) return;
		LMDimUtils.teleportPlayer(ep, LMDimUtils.getPlayerEntitySpawnPoint(ep, 0));
	}
	
	@SubscribeEvent
	public void worldLoaded(net.minecraftforge.event.world.WorldEvent.Load e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			IServerConfig.Registry.load();
			
			File latmodFolder = new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/");
			
			NBTTagCompound tagWorldData = LMNBTUtils.readMap(new File(latmodFolder, "LMWorld.dat"));
			if(tagWorldData == null) tagWorldData = new NBTTagCompound();
			LMWorldServer.inst = new LMWorldServer(tagWorldData.hasKey("UUID") ? LatCoreMC.getUUIDFromString(tagWorldData.getString("UUID")) : UUID.randomUUID());
			LMWorldServer.inst.load(tagWorldData);
			
			NBTTagCompound customData = tagWorldData.getCompoundTag("Custom");
			
			new LoadLMDataEvent(latmodFolder, EventLM.Phase.PRE, customData).post();
			
			NBTTagCompound tagPlayers = LMNBTUtils.readMap(new File(latmodFolder, "LMPlayers.dat"));
			if(tagPlayers != null && tagPlayers.hasKey("Players"))
			{
				LMPlayerServer.lastPlayerID = tagPlayers.getInteger("LastID");
				LMWorldServer.inst.readPlayersFromServer(tagPlayers.getCompoundTag("Players"));
			}
			
			for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
				LMWorldServer.inst.players.get(i).setPlayer(null);
			
			new LoadLMDataEvent(latmodFolder, EventLM.Phase.POST, customData).post();
			
			LatCoreMC.logger.info("LatCoreMC data loaded");
		}
	}
	
	@SubscribeEvent
	public void worldSaved(net.minecraftforge.event.world.WorldEvent.Save e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			NBTTagCompound customData = new NBTTagCompound();
			SaveLMDataEvent e1 = new SaveLMDataEvent(new File(e.world.getSaveHandler().getWorldDirectory(), "latmod/"), customData);
			e1.post();
			
			{
				NBTTagCompound tag = new NBTTagCompound();
				LMWorldServer.inst.save(tag);
				tag.setString("UUID", LMWorldServer.inst.worldIDS);
				tag.setTag("Custom", customData);
				LMNBTUtils.writeMap(e1.getFile("LMWorld.dat"), tag);
			}
			
			{
				NBTTagCompound tag = new NBTTagCompound();
				NBTTagCompound players = new NBTTagCompound();
				LMWorldServer.inst.writePlayersToServer(players);
				tag.setTag("Players", players);
				tag.setInteger("LastID", LMPlayerServer.lastPlayerID);
				LMNBTUtils.writeMap(e1.getFile("LMPlayers.dat"), tag);
			}
			
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
		
		TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);
		
		if(te != null && !te.isInvalid() && te instanceof ISecureTile)
		{
			if(!((ISecureTile)te).canPlayerInteract(e.entityPlayer, e.action == Action.LEFT_CLICK_BLOCK))
			{ ((ISecureTile)te).onPlayerNotOwner(e.entityPlayer, e.action == Action.LEFT_CLICK_BLOCK); return false; }
		}
		
		LMPlayerServer p = LMWorldServer.inst.getPlayer(e.entityPlayer);
		if(!LatCoreMC.isDedicatedServer() || p.isOP()) return true;
		if(FTBUConfig.general.isDedi() && !ChunkType.getD(e.world.provider.dimensionId, e.x, e.z, p).isFriendly()) return false;
		
		return true;
	}
	
	@SubscribeEvent
	public void onPlayerDeath(net.minecraftforge.event.entity.living.LivingDeathEvent e)
	{
		if(e.entity instanceof EntityPlayerMP)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(e.entity);
			p.deaths++;
			
			if(p.lastDeath == null) p.lastDeath = new EntityPos(e.entity);
			else p.lastDeath.set(e.entity);
			
			LMNetHelper.sendTo(null, new MessageLMPlayerDied(p));
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChatEvent(net.minecraftforge.client.event.ClientChatReceivedEvent e)
	{
		if(FTBUClient.chatLinks.getB())
		{
			String[] msg = e.message.getUnformattedText().split(" ");
			
			FastList<String> links = new FastList<String>();
			
			for(String s : msg)
			{
				if(s.startsWith("http://") || s.startsWith("https://"))
					links.add(s);
			}
			
			if(!links.isEmpty())
			{
				final IChatComponent line = new ChatComponentText("");
				boolean oneLink = links.size() == 1;
				
				for(int i = 0; i < links.size(); i++)
				{
					String link = links.get(i);
					IChatComponent c = new ChatComponentText(oneLink ? "[ Link ]" : ("[ Link #" + (i + 1) + " ]"));
					c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(link)));
					c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
					line.appendSibling(c);
					if(!oneLink) line.appendSibling(new ChatComponentText(" "));
				}
				
				line.getChatStyle().setColor(EnumChatFormatting.GOLD);
				
				LatCoreMCClient.addCallbackEvent(new ICallbackEvent()
				{
					public void onCallback()
					{ LatCoreMC.printChat(null, line); }
				});
			}
		}
	}
}