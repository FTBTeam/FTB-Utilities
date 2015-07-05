package latmod.ftbu.mod.client;
import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.client.badges.ThreadLoadBadges;
import latmod.ftbu.core.event.ReloadEvent;
import latmod.ftbu.core.gui.GuiLM;
import latmod.ftbu.core.tile.IPaintable;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.claims.ChunkType;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
	public static final ResourceLocation friendsButtonTexture = FTBU.mod.getLocation("textures/gui/friendsbutton.png");
	public static final FTBUClientEventHandler instance = new FTBUClientEventHandler();
	private static final int BUTTON_ID = 24286;
	private static final int SETTINGS_BUTTON_ID = 24287;
	
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
		
		if(FTBUClient.addRegistryNames.getB())
		{
			e.toolTip.add(InvUtils.getRegName(e.itemStack));
		}
		
		if(FTBUClient.addOreNames.getB())
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
		{
			LatCoreMCClient.blockNullIcon = e.map.registerIcon(FTBU.mod.assets + "empty_block");
			FTBULang.reload();
		}
		else if(e.map.getTextureType() == 1)
			LatCoreMCClient.unknownItemIcon = e.map.registerIcon(FTBU.mod.assets + "unknown");
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
	public void onDrawDebugText(RenderGameOverlayEvent.Text e)
	{
		boolean shift = FTBU.proxy.isShiftDown();
		Minecraft mc = LatCoreMCClient.getMinecraft();
		
		// Some ideas around this //
		if(!mc.gameSettings.showDebugInfo)
		{
			if(LatCoreMC.isDevEnv)
				e.left.add("[MC " + EnumChatFormatting.GOLD + LatCoreMC.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
			
			if(FTBUClient.displayDebugInfo.getB())
				e.right.add(mc.debug);
		}
		
		LMPlayer p = LMWorld.client.getClientPlayer();
		if(p != null) ChunkType.getMessage(mc.theWorld.provider.dimensionId, MathHelperLM.chunk(mc.thePlayer.posX), MathHelperLM.chunk(mc.thePlayer.posZ), p, e.right, shift);
		
		if(FTBUClient.displayDebugInfo.getB())
		{
			//e.right.add("WorldClient@" + mc.theWorld.hashCode() + ", " + mc.theWorld.provider.dimensionId + ", " + mc.theWorld.provider.getDimensionName());
		}
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void guiInitEvent(final GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(e.gui instanceof GuiOptions)
		{
			if(FTBUClient.optionsButton.getB())
				e.buttonList.add(new GuiButton(SETTINGS_BUTTON_ID, e.gui.width / 2 - 155, e.gui.height / 6 + 48 - 6, 150, 20, "FTBU Client Config"));
		}
		else if(e.gui instanceof GuiInventory || e.gui instanceof GuiContainerCreative)
		{
			int xSize = 176;
			int ySize = 166;
			
			int buttonX = 28;
			int buttonY = 10;
			
			if(e.gui instanceof GuiContainerCreative)
			{
				xSize = 195;
				ySize = 136;
				
				buttonX = 29;
				buttonY = 8;
			}
			
			final int guiLeft = (e.gui.width - xSize) / 2;
			final int guiTop = (e.gui.height - ySize) / 2;
			
			e.buttonList.add(new ButtonFriends(e.gui, guiLeft + buttonX, guiTop + buttonY));
		}
	}
	
	@SubscribeEvent
	public void guiActionEvent(GuiScreenEvent.ActionPerformedEvent.Post e)
	{
		if(e.button.id == SETTINGS_BUTTON_ID)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiClientConfig(e.gui)); 
		}
		else if(e.button.id == BUTTON_ID)
		{
			final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
			
			if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
				return;
			
			LatCoreMC.openGui(e.gui.mc.thePlayer, FTBUGuiHandler.FRIENDS, null);
		}
	}
	
	private static class ButtonFriends extends GuiButton
	{
		private final GuiContainerCreative creativeContainer;
		private int textOX = 0, textOY = 0;
		
		public ButtonFriends(GuiScreen g, int x, int y)
		{
			super(BUTTON_ID, x, y, 8, 8, "Friends");
			creativeContainer = (g instanceof GuiContainerCreative) ? (GuiContainerCreative)g : null;
		}
		
		public void drawButton(Minecraft mc, int mx, int my)
		{
			if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
				return;
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			mc.getTextureManager().bindTexture(friendsButtonTexture);
			GuiLM.drawTexturedRectD(xPosition, yPosition, 0D, 8, 8, 0D, 0D, 1D, 1D);
			if(mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height)
				drawString(mc.fontRenderer, displayString, xPosition + textOX, yPosition + textOY, -1);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}