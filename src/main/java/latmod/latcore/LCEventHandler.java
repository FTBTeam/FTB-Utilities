package latmod.latcore;
import java.io.*;
import java.util.UUID;

import latmod.core.*;
import latmod.core.client.LatCoreMCClient;
import latmod.core.event.CustomActionEvent;
import latmod.core.net.*;
import latmod.core.tile.*;
import latmod.core.util.*;
import latmod.core.waila.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;

public class LCEventHandler
{
	public static final String ACTION_PLAYER_JOINED = "PlayerJoined";
	public static final String ACTION_OPEN_URL = "OpenURL";
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.itemStack == null || e.itemStack.getItem() == null) return;
		
		Item item = e.itemStack.getItem();
		
		if(e.showAdvancedItemTooltips && e.itemStack != null && e.itemStack.getItem() != null)
		{
			if(LC.mod.config().general.addOreNames)
			{
				FastList<String> ores = ODItems.getOreNames(e.itemStack);
				
				if(ores != null && !ores.isEmpty())
				{
					e.toolTip.add("Ore Dictionary names:");
					for(String or : ores)
					e.toolTip.add("> " + or);
				}
			}
			
			if(LC.mod.config().general.addRegistryNames)
			{
				e.toolTip.add(LatCoreMC.getRegName(e.itemStack));
			}
			
			if(LC.mod.config().general.addFluidContainerNames)
			{
				FluidStack fs = LatCoreMC.getFluid(e.itemStack);
				
				if(fs != null && fs.amount > 0)
				{
					e.toolTip.add("Stored FluidID:");
					e.toolTip.add(FluidRegistry.getFluidName(fs.fluidID));
				}
			}
		}
		
		if(item instanceof IPaintable.IPainterItem)
		{
			ItemStack paint = ((IPaintable.IPainterItem)item).getPaintItem(e.itemStack);
			if(paint != null) e.toolTip.add("Paint: " + paint.getDisplayName());
		}
	}
	
	@SubscribeEvent
	public void playerJoined(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		UUID id = e.player.getUniqueID();
		LatCoreMC.logger.info("UUID: " + id);
		
		boolean first = false;
		
		LMPlayer p = LMPlayer.getPlayer(id);
		
		if(p == null)
		{
			p = new LMPlayer(id);
			LMPlayer.list.add(p);
		}
		
		p.username = e.player.getCommandSenderName();
		
		if(!p.customData().hasKey("IsOld"))
		{
			p.customData().setBoolean("IsOld", true);
			
			if(p.uuid.equals(LatCoreMC.latvianModderUUID))
			{
				p.setCustomName("LatvianModder");
				e.player.refreshDisplayName();
			}
			
			first = true;
		}
		
		if(EnumLatModTeam.TEAM.uuids.contains(e.player.getUniqueID()))
			LatCoreMC.printChat(e.player, "Hello, Team LatMod member!");
		
		if(LC.mod.config().general.checkUpdates)
			ThreadCheckVersions.init(e.player, false);
		
		new LMPlayer.LMPlayerLoggedInEvent(p, e.player, first).post();
		
		{
			NBTTagCompound data = new NBTTagCompound();
			data.setString("UUID", p.uuid.toString());
			LMNetHandler.INSTANCE.sendToAll(new MessageCustomServerAction(ACTION_PLAYER_JOINED, data));
		}
		
		e.player.refreshDisplayName();
	}
	
	@SubscribeEvent
	public void registerWaila(RegisterWailaEvent e)
	{
		e.register(IWailaTile.Stack.class, new WailaLMTile(e, WailaType.STACK));
		e.register(IWailaTile.Head.class, new WailaLMTile(e, WailaType.HEAD));
		e.register(IWailaTile.Body.class, new WailaLMTile(e, WailaType.BODY));
		e.register(IWailaTile.Tail.class, new WailaLMTile(e, WailaType.TAIL));
		
		e.register(IInventory.class, new WailaInvHandler(e));
		e.register(IFluidHandler.class, new WailaTankHandler(e));
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f = LatCore.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat"));
			
			LMPlayer.list.clear();
			
			if(f.exists())
			{
				try
				{
					FileInputStream fis = new FileInputStream(f);
					byte[] b = new byte[fis.available()];
					fis.read(b); fis.close();
					
					NBTTagCompound tag = CompressedStreamTools.func_152457_a(b, new NBTSizeTracker(Long.MAX_VALUE));
					
					new LoadCustomLMDataEvent(tag).post();
					
					NBTTagList players = tag.getTagList("Players", LatCoreMC.NBT_MAP);
					
					for(int i = 0; i < players.tagCount(); i++)
					{
						NBTTagCompound tag1 = players.getCompoundTagAt(i);
						LMPlayer p = new LMPlayer(UUID.fromString(tag1.getString("UUID")));
						p.readFromNBT(tag1);
						
						new LMPlayer.DataLoadedEvent(p).post();
						
						LMPlayer.list.add(p);
					}
					
					LMGamerules.readFromNBT(tag, "Gamerules");
					
					LatCoreMC.logger.info("LatCoreMC.dat loaded");
				}
				catch(Exception ex)
				{ ex.printStackTrace(); }
			}
		}
	}
	
	public static class LoadCustomLMDataEvent extends Event
	{
		public final NBTTagCompound tag;
		
		public LoadCustomLMDataEvent(NBTTagCompound t)
		{ tag = t; }
		
		public void post()
		{ MinecraftForge.EVENT_BUS.post(this); }
	}
	
	@SubscribeEvent
	public void worldSaved(WorldEvent.Save e)
	{
		if(LatCoreMC.isServer() && e.world.provider.dimensionId == 0)
		{
			File f = LatCore.newFile(new File(e.world.getSaveHandler().getWorldDirectory(), "LatCoreMC.dat"));
			
			try
			{
				NBTTagCompound tag = new NBTTagCompound();
				
				NBTTagList players = new NBTTagList();
				
				for(int i = 0; i < LMPlayer.list.size(); i++)
				{
					NBTTagCompound tag1 = new NBTTagCompound();
					
					LMPlayer p = LMPlayer.list.get(i);
					p.writeToNBT(tag1);
					
					new LMPlayer.DataSavedEvent(p).post();
					
					tag1.setString("UUID", p.uuid.toString());
					
					players.appendTag(tag1);
				}
				
				tag.setTag("Players", players);
				
				LMGamerules.writeToNBT(tag, "Gamerules");
				
				new SaveCustomLMDataEvent(tag).post();
				
				byte[] b = CompressedStreamTools.compress(tag);
				
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(b);
				fos.close();
			}
			catch(Exception ex)
			{
				LatCoreMC.logger.warn("Error occured while saving LatCoreMC.dat!");
			}
		}
	}
	
	public static class SaveCustomLMDataEvent extends Event
	{
		public final NBTTagCompound tag;
		
		public SaveCustomLMDataEvent(NBTTagCompound t)
		{ tag = t; }
		
		public void post()
		{ MinecraftForge.EVENT_BUS.post(this); }
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void playerName(PlayerEvent.NameFormat e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.entityPlayer);
		if(p != null && p.hasCustomName())
			e.displayname = p.getDisplayName();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void preTexturesLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 0)
			LatCoreMCClient.blockNullIcon = e.map.registerIcon(LC.mod.assets + "nullIcon");
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void playerJoinedClient(CustomActionEvent e)
	{
		if(e.action.equals(ACTION_PLAYER_JOINED))
		{
			LMPlayer p = LMPlayer.getPlayer(UUID.fromString(e.extraData.getString("UUID")));
			if(p != null)
			{
				EntityPlayer ep = p.getPlayer();
				if(ep != null) LC.proxy.onClientPlayerJoined(ep);
			}
		}
		else if(e.action.equals(ACTION_OPEN_URL))
		{
			LatCore.openURL(e.extraData.getString("URL"));
		}
	}
}