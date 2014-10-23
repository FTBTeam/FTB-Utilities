package latmod.core.mod;
import java.io.*;
import java.util.UUID;

import latmod.core.*;
import latmod.core.client.LatCoreMCClient;
import latmod.core.mod.net.*;
import latmod.core.mod.tile.PainterHelper;
import latmod.core.util.*;
import net.minecraft.entity.player.EntityPlayer;
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
				FluidStack fs = null;
				
				if(item instanceof IFluidContainerItem)
				{
					fs = ((IFluidContainerItem)item).getFluid(e.itemStack);
					
					if(fs != null && fs.amount > 0)
					{
						e.toolTip.add("Stored FluidID:");
						e.toolTip.add(FluidRegistry.getFluidName(fs.fluidID));
					}
				}
				
				if(fs == null) fs = FluidContainerRegistry.getFluidForFilledItem(e.itemStack);
				
				if(fs != null && fs.amount > 0)
				{
					e.toolTip.add("Stored FluidID:");
					e.toolTip.add(FluidRegistry.getFluidName(fs.fluidID));
				}
			}
		}
		
		if(item instanceof PainterHelper.IPainterItem)
		{
			ItemStack paint = ((PainterHelper.IPainterItem)item).getPaintItem(e.itemStack);
			if(paint != null) e.toolTip.add("Paint: " + paint.getDisplayName());
		}
	}
	
	@SubscribeEvent
	public void playerJoined(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		UUID id = e.player.getUniqueID();
		LC.logger.info("UUID: " + id);
		
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
			data.setString("UUID", e.player.getUniqueID().toString());
			LMNetHandler.INSTANCE.sendToAll(new MessageCustomServerAction(ACTION_PLAYER_JOINED, data));
		}
	}
	
	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load e)
	{
		if(LatCoreMC.canUpdate() && e.world.provider.dimensionId == 0)
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
						
						new LMPlayer.DataLoadedEvent(p, e.world).post();
						
						LMPlayer.list.add(p);
					}
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
		if(LatCoreMC.canUpdate() && e.world.provider.dimensionId == 0)
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
					
					new LMPlayer.DataSavedEvent(p, e.world).post();
					
					tag1.setString("UUID", p.uuid.toString());
					
					players.appendTag(tag1);
				}
				
				tag.setTag("Players", players);
				
				new SaveCustomLMDataEvent(tag).post();
				
				byte[] b = CompressedStreamTools.compress(tag);
				
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(b);
				fos.close();
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
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
			UUID id = UUID.fromString(e.extraData.getString("UUID"));
			EntityPlayer ep = LatCoreMC.getPlayer(e.player.worldObj, id);
			LC.proxy.setSkinAndCape(ep);
		}
	}
}