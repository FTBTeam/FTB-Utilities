package latmod.core.mod;
import java.io.*;
import java.util.UUID;

import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.eventhandler.*;

public class LCEventHandler
{
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null && e.itemStack.getItem() != null)
		{
			if(LC.config.general.addOreNames)
			{
				FastList<String> ores = ODItems.getOreNames(e.itemStack);
				
				if(ores != null && !ores.isEmpty())
				{
					e.toolTip.add("Ore Dictionary names:");
					for(String or : ores)
					e.toolTip.add("> " + or);
				}
			}
			
			if(LC.config.general.addRegistryNames)
			{
				e.toolTip.add(LatCoreMC.getRegName(e.itemStack.getItem(), false));
			}
			
			if(LC.config.general.addFluidContainerNames)
			{
				if(e.itemStack.getItem() instanceof IFluidContainerItem)
				{
					FluidStack fs = ((IFluidContainerItem)e.itemStack.getItem()).getFluid(e.itemStack);
					
					if(fs != null && fs.amount > 0)
					{
						e.toolTip.add("Stored FluidID:");
						e.toolTip.add(FluidRegistry.getFluidName(fs.fluidID));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerJoined(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		UUID id = e.player.getUniqueID();
		LC.logger.info("UUID: " + id);
		
		LMPlayer p = LMPlayer.getPlayer(id);
		if(p == null)
		{
			p = new LMPlayer(id);
			p.displayName = e.player.getCommandSenderName();
			
			if(p.uuid.toString().equals("8234defe-cc96-4ea4-85cb-abf2bf80add1") || p.uuid.toString().equals("2f77c363-be5f-3bec-9c10-ce5202449b13"))
				p.customName = "LatvianModder";
			
			LMPlayer.list.add(p);
		}
		else
		{
			p.displayName = e.player.getCommandSenderName();
		}
		
		if(EnumLatModTeam.TEAM.uuids.contains(e.player.getUniqueID()))
			LatCoreMC.printChat(e.player, "Hello, Team LatMod member!");
		
		if(LC.config.general.checkUpdates)
			ThreadCheckVersions.init(e.player, false);
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
					
					NBTTagList players = tag.getTagList("Players", LatCoreMC.NBT_MAP);
					
					for(int i = 0; i < players.tagCount(); i++)
					{
						NBTTagCompound tag1 = players.getCompoundTagAt(i);
						LMPlayer p = new LMPlayer(UUID.fromString(tag1.getString("UUID")));
						p.readFromNBT(tag1);
						
						LMPlayer.list.add(p);
					}
					
					MinecraftForge.EVENT_BUS.post(new LoadCustomLMDataEvent(tag));
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
		{
			tag = t;
		}
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
					tag1.setString("UUID", p.uuid.toString());
					
					players.appendTag(tag1);
				}
				
				tag.setTag("Players", players);
				
				MinecraftForge.EVENT_BUS.post(new SaveCustomLMDataEvent(tag));
				
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
		{
			tag = t;
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerName(PlayerEvent.NameFormat e)
	{
		LMPlayer p = LMPlayer.getPlayer(e.entityPlayer);
		if(p != null && p.customName != null && p.customName.length() > 0)
		{
			String s = p.customName + "";
			e.displayname = s.trim().replace("&", "\u00a7").replace("%name%", p.displayName);
		}
	}
}