package latmod.ftbu.client;
import java.lang.reflect.Field;
import java.util.UUID;

import latmod.ftbu.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.client.badges.*;
import latmod.ftbu.core.event.*;
import latmod.ftbu.core.gui.GuiLM;
import latmod.ftbu.core.tile.IPaintable;
import latmod.ftbu.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
	public static final ResourceLocation friendsButtonTexture = FTBU.mod.getLocation("textures/gui/friendsbutton.png");
	public static final FTBUClientEventHandler instance = new FTBUClientEventHandler();
	
	public static final FastList<GuiNotification> messages = new FastList<GuiNotification>();
	public static final FastMap<UUID, Badge> playerBadges = new FastMap<UUID, Badge>();
	
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
		
		if(FTBUConfig.Client.addRegistryNames)
		{
			e.toolTip.add(InvUtils.getRegName(e.itemStack));
		}
		
		if(FTBUConfig.Client.addOreNames)
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
			LatCoreMCClient.blockNullIcon = e.map.registerIcon(FTBU.mod.assets + "nullIcon");
		else if(e.map.getTextureType() == 1)
			LatCoreMCClient.unknownItemIcon = e.map.registerIcon(FTBU.mod.assets + "unknown");
	}
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(!Badge.reloading && FTBUConfig.Client.enablePlayerDecorators && !e.entityPlayer.isInvisible())
		{
			Badge b = playerBadges.get(e.entityPlayer.getUniqueID());
			if(b != null) b.onPlayerRender(e.entityPlayer);
		}
	}
	
	@SubscribeEvent
	public void onReload(ReloadEvent r)
	{
		if(r.side.isClient())
		{
			ThreadLoadBadges.init();
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
		boolean shift = FTBU.proxy.isShiftDown();
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
					
					event.left.add("[MC " + EnumChatFormatting.GOLD + LatCoreMC.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
					event.right.add("FPS: " + EnumChatFormatting.GOLD + fps.getInt(null));
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
		else if(FTBUConfig.Client.displayDebugInfo)
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
	
	public GuiButton guiButton = null;
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void guiInitEvent(final GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(!(e.gui instanceof GuiInventory) && !(e.gui instanceof GuiContainerCreative)) return;
		final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
		
		int xSize = 176;
		int ySize = 166;
		
		int buttonX = 28;
		int buttonY = 10;
		
		int textOX = 0;//-24;
		int textOY = 0;
		
		if(creativeContainer != null)
		{
			xSize = 195;
			ySize = 136;
			
			buttonX = 29;
			buttonY = 8;
			
			textOX = 0;
			textOY = 0;
		}
		
		final int guiLeft = (e.gui.width - xSize) / 2;
		final int guiTop = (e.gui.height - ySize) / 2;
		
		final int textOX1 = textOX;
		final int textOY1 = textOY;
		
		guiButton = new GuiButton(4950, guiLeft + buttonX, guiTop + buttonY, 8, 8, "Friends")
		{
			public void drawButton(Minecraft mc, int mx, int my)
			{
				if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
					return;
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				e.gui.mc.getTextureManager().bindTexture(friendsButtonTexture);
				GuiLM.drawTexturedModalRectD(xPosition, yPosition, 0D, 0D, width, height, 8, 8, 0D);
				if(mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height)
					drawString(mc.fontRenderer, displayString, xPosition + textOX1, yPosition + textOY1, -1);
				GL11.glDisable(GL11.GL_BLEND);
			}
		};
		
		e.buttonList.add(guiButton);
	}
	
	@SubscribeEvent
	public void guiActionEvent(GuiScreenEvent.ActionPerformedEvent.Post e)
	{
		if(!(e.gui instanceof GuiInventory) && !(e.gui instanceof GuiContainerCreative)) return;
		
		final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
		
		if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
			return;
		
		if(e.button.id == guiButton.id)
			LatCoreMC.openGui(e.gui.mc.thePlayer, FTBUGuiHandler.FRIENDS, null);
	}
}