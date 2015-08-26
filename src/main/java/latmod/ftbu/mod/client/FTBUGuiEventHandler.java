package latmod.ftbu.mod.client;

import latmod.ftbu.core.FTBULang;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.gui.GuiLM;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.player.ClientNotifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUGuiEventHandler
{
	public static final FTBUGuiEventHandler instance = new FTBUGuiEventHandler();
	public static final ResourceLocation friendsButtonTexture = FTBU.mod.getLocation("textures/gui/friendsbutton.png");
	public static final ResourceLocation friendsButtonTextureOn = FTBU.mod.getLocation("textures/gui/friendsbutton_on.png");
	private static final int BUTTON_ID = 24286;
	private static final int SETTINGS_BUTTON_ID = 24287;
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void guiInitEvent(final GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(e.gui instanceof GuiOptions && LatCoreMCClient.getMinecraft().thePlayer != null)
		{
			if(FTBUClient.optionsButton.getB())
				e.buttonList.add(new GuiButton(SETTINGS_BUTTON_ID, e.gui.width / 2 - 155, e.gui.height / 6 + 48 - 6, 150, 20, "[FTBU] " + FTBULang.client_config));
		}
		else if(e.gui instanceof GuiInventory || e.gui instanceof GuiContainerCreative)
		{
			if(!LMWorldClient.inst.hasServer) return;
			
			int xSize = 176;
			int ySize = 166;
			
			int buttonX = -17;
			int buttonY = 8;
			
			if(e.gui instanceof GuiContainerCreative)
			{
				xSize = 195;
				ySize = 136;
				
				buttonY = 6;
			}
			
			int guiLeft = (e.gui.width - xSize) / 2;
			int guiTop = (e.gui.height - ySize) / 2;
			
			e.buttonList.add(new ButtonFriends(e.gui, guiLeft + buttonX, guiTop + buttonY));
		}
	}
	
	@SubscribeEvent
	public void guiActionEvent(GuiScreenEvent.ActionPerformedEvent.Post e)
	{
		if(e.button.id == SETTINGS_BUTTON_ID)
			e.gui.mc.displayGuiScreen(new GuiClientConfig(e.gui)); 
		else if(e.button.id == BUTTON_ID)
		{
			final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
			
			if(creativeContainer == null || creativeContainer.func_147056_g() == CreativeTabs.tabInventory.getTabIndex())
			{
				GuiFriends g = new GuiFriends(e.gui);
				g.playClickSound();
				e.gui.mc.displayGuiScreen(g);
			}
		}
	}
	
	private static class ButtonFriends extends GuiButton
	{
		private final GuiContainerCreative creativeContainer;
		
		public ButtonFriends(GuiScreen g, int x, int y)
		{
			super(BUTTON_ID, x, y, 16, 16, "");
			creativeContainer = (g instanceof GuiContainerCreative) ? (GuiContainerCreative)g : null;
		}
		
		public void drawButton(Minecraft mc, int mx, int my)
		{
			if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
				return;
			
			boolean mouseOver = (mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height);
			
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glColor4f(1F, 1F, 1F, mouseOver ? 1F : 0.8F);
			mc.getTextureManager().bindTexture(mouseOver ? friendsButtonTextureOn : friendsButtonTexture);
			GuiLM.drawTexturedRectD(xPosition, yPosition, 0D, width, height, 0D, 0D, 1D, 1D);
			
			if(!ClientNotifications.perm.isEmpty())
			{
				String n = String.valueOf(ClientNotifications.perm.size());
				int nw = mc.fontRenderer.getStringWidth(n);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GuiLM.drawRect(xPosition + width - nw, yPosition - 4, xPosition + width + 1, yPosition + 5, 0xAAFF2222);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				mc.fontRenderer.drawString(n, xPosition + width - nw + 1, yPosition - 3, 0xFFFFFFFF);
			}
			
			GL11.glPopAttrib();
		}
	}
}