package latmod.core.mod.client;
import java.lang.reflect.Field;
import java.util.UUID;

import latmod.core.*;
import latmod.core.client.playerdeco.*;
import latmod.core.event.*;
import latmod.core.gui.GuiLM;
import latmod.core.mod.*;
import latmod.core.tile.IPaintable;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClientEventHandler
{
	public static final LCClientEventHandler instance = new LCClientEventHandler();
	
	public final FastList<GuiNotification> messages = new FastList<GuiNotification>();
	public final FastMap<UUID, FastList<PlayerDecorator>> playerDecorators = new FastMap<UUID, FastList<PlayerDecorator>>();
	public final FastList<UUID> listLatMod = new FastList<UUID>();
	public final FastList<UUID> listFTB = new FastList<UUID>();
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.itemStack == null || e.itemStack.getItem() == null) return;
		
		Item item = e.itemStack.getItem();
		
		if(item instanceof IPaintable.IPainterItem)
		{
			ItemStack paint = ((IPaintable.IPainterItem)item).getPaintItem(e.itemStack);
			if(paint != null) e.toolTip.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + paint.getDisplayName());
		}
		
		if(LCConfig.Client.addRegistryNames)
		{
			e.toolTip.add(InvUtils.getRegName(e.itemStack));
		}
		
		if(LCConfig.Client.addOreNames)
		{
			FastList<String> ores = ODItems.getOreNames(e.itemStack);
			
			if(ores != null && !ores.isEmpty())
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void preTexturesLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 0)
			LatCoreMC.blockNullIcon = e.map.registerIcon(LC.mod.assets + "nullIcon");
		else if(e.map.getTextureType() == 1)
			LatCoreMC.unknownItemIcon = e.map.registerIcon(LC.mod.assets + "unknown");
	}
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(LCConfig.Client.enablePlayerDecorators && !e.entityPlayer.isInvisible())
		{
			UUID id = e.entityPlayer.getUniqueID();
			
			if(listLatMod.contains(id)) PDLatMod.instance.onPlayerRender(e);
			if(listFTB.contains(id)) PDFTB.instance.onPlayerRender(e);
			
			FastList<PlayerDecorator> l = playerDecorators.get(id);
			
			if(l != null && l.size() > 0)
			{
				for(int i = 0; i < l.size(); i++)
					l.get(i).onPlayerRender(e);
			}
		}
	}
	
	@SubscribeEvent
	public void onReload(ReloadEvent r)
	{
		if(r.side.isClient())
		{
			ThreadCheckPlayerDecorators.init();
		}
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent e)
	{
		if(LCClient.key.isPressed() && LC.proxy.inGameHasFocus())
		{
			EntityPlayer ep = LC.proxy.getClientPlayer();
			
			if (ep != null && ep.worldObj.isRemote)
				LatCoreMC.openGui(ep, LCGuiHandler.FRIENDS, null);
		}
	}
	
	@SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        
        if(mc.theWorld != null && event.phase == TickEvent.Phase.END && !messages.isEmpty())
        {
        	GuiNotification m = messages.get(0);
        	m.render(mc); if(m.isDead()) messages.remove(0);
        }
    }
	
	@SubscribeEvent
	public void onIconsLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 1)
		{
			LoadLMIconsEvent ev = new LoadLMIconsEvent(e.map);
			GuiLM.Icons.load(ev);
			ev.post();
		}
	}
	
	private static Field fps = null;
	
	@SubscribeEvent
	public void onDrawDebugText(RenderGameOverlayEvent.Text event)
	{
		boolean shift = LC.proxy.isShiftDown();
		Minecraft mc = Minecraft.getMinecraft();
		
		// Some ideas around this //
		if(!mc.gameSettings.showDebugInfo)
		{
			if(LatCoreMC.isDevEnv)
			{
				try
				{
					if(fps == null)
					{
						fps = Minecraft.class.getDeclaredField("debugFPS");
						fps.setAccessible(true);
					}
					
					event.left.add("[LatCoreMC] Dev version!");
					event.right.add("FPS: " + fps.getInt(null));
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
		else if(LCConfig.Client.displayDebugInfo)
		{
			event.right.add(null);
			
			if(mc.objectMouseOver != null)
			{
				if(mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
				{
					int x = mc.objectMouseOver.blockX;
					int y = mc.objectMouseOver.blockY;
					int z = mc.objectMouseOver.blockZ;
					
					Block block = mc.theWorld.getBlock(x, y, z);
					
					int meta = mc.theWorld.getBlockMetadata(x, y, z);
					TileEntity te = mc.theWorld.getTileEntity(x, y, z);
					
					event.right.add(shift ? (LatCore.classpath(block.getClass())) : (InvUtils.getRegName(block) + (meta > 0 ? (";" +  meta) : "")) + " @ " + LatCore.stripInt(x, y, z));
					
					if(shift)
					{
						Class<?> bInts[] = block.getClass().getInterfaces();
						
						if(bInts.length > 0)
						{
							event.right.add(null);
							for(int i = 0; i < bInts.length; i++)
								event.right.add(LatCore.classpath(bInts[i]) + "  <");
						}
					}
					
					if(te != null)
					{
						event.right.add(null);
						event.right.add("Tile: " + LatCore.classpath(te.getClass()));
						
						if(shift)
						{
							Class<?> tInts[] = te.getClass().getInterfaces();
							
							if(tInts.length > 0)
							{
								event.right.add(null);
								for(int i = 0; i < tInts.length; i++)
									event.right.add(LatCore.classpath(tInts[i]) + "  <");
							}
						}
					}
				}
			}
		}
	}
}