package latmod.ftbu.mod.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.guide.GuideFile;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.*;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.gui.GuiLM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;

@SideOnly(Side.CLIENT)
public class FTBUGuiEventHandler
{
	public static final FTBUGuiEventHandler instance = new FTBUGuiEventHandler();
	public static final ResourceLocation friendsButtonTexture = FTBU.mod.getLocation("textures/gui/friendsbutton.png");
	private static final int FRIENDS_GUI_BUTTON_ID = 24286;
	private static final int SETTINGS_BUTTON_ID = 24287;
	private static final int GUIDE_BUTTON_ID = 24288;
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void guiInitEvent(final GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(!LatCoreMCClient.isPlaying()) return;
		
		if(e.gui instanceof GuiOptions)
		{
			if(FTBUClient.optionsButton.getB())
				e.buttonList.add(new GuiButton(SETTINGS_BUTTON_ID, e.gui.width / 2 - 155, e.gui.height / 6 + 48 - 6, 150, 20, "[FTBU] " + FTBULang.client_config()));
		}
		else if(e.gui instanceof GuiInventory || e.gui instanceof GuiContainerCreative)
		{
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
			boolean hasPotions = !e.gui.mc.thePlayer.getActivePotionEffects().isEmpty();
			if(hasPotions)
			{ buttonX -= 4; buttonY -= 26; }
			
			int guiLeft = (e.gui.width - xSize) / 2;
			int guiTop = (e.gui.height - ySize) / 2;
			
			e.buttonList.add(new ButtonFriends(e.gui, guiLeft + buttonX, guiTop + buttonY));
			e.buttonList.add(new ButtonGuide(e.gui, guiLeft + buttonX + (hasPotions ? -18 : 0), guiTop + buttonY + (hasPotions ? 0 : 18)));
		}
	}
	
	@SubscribeEvent
	public void guiActionEvent(GuiScreenEvent.ActionPerformedEvent.Post e)
	{
		if(e.button.id == SETTINGS_BUTTON_ID)
			e.gui.mc.displayGuiScreen(new GuiClientConfig(e.gui)); 
		else if(e.button.id == FRIENDS_GUI_BUTTON_ID || e.button.id == GUIDE_BUTTON_ID)
		{
			final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
			
			if(creativeContainer == null || creativeContainer.func_147056_g() == CreativeTabs.tabInventory.getTabIndex())
			{
				if(e.button.id == FRIENDS_GUI_BUTTON_ID)
				{
					GuiFriends g = new GuiFriends(e.gui);
					g.playClickSound();
					e.gui.mc.displayGuiScreen(g);
				}
				else if(e.button.id == GUIDE_BUTTON_ID)
				{
					GuiGuide g = new GuiGuide(null, GuideFile.inst.main);
					g.playClickSound();
					e.gui.mc.displayGuiScreen(g);
				}
			}
		}
	}
	
	private static class ButtonFriends extends GuiButton
	{
		private final GuiContainerCreative creativeContainer;
		
		public ButtonFriends(GuiScreen g, int x, int y)
		{
			super(FRIENDS_GUI_BUTTON_ID, x, y, 16, 16, "");
			creativeContainer = (g instanceof GuiContainerCreative) ? (GuiContainerCreative)g : null;
		}
		
		public void drawButton(Minecraft mc, int mx, int my)
		{
			if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
				return;
			
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
			FTBLibClient.setTexture(friendsButtonTexture);
			GuiLM.drawTexturedRectD(xPosition, yPosition, 0D, width, height, 0D, 0D, 1D, 1D);
			
			if(mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height)
				GuiLM.drawBlankRect(xPosition, yPosition, 0D, width, height, 0x55FFFFFF);
			
			if(!ClientNotifications.Perm.list.isEmpty())
			{
				String n = String.valueOf(ClientNotifications.Perm.list.size());
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
	
	private static class ButtonGuide extends GuiButton
	{
		private static RenderItem itemRender = new RenderItem();
		
		private final GuiContainerCreative creativeContainer;
		private final ItemStack itemToDraw;
		
		public ButtonGuide(GuiScreen g, int x, int y)
		{
			super(GUIDE_BUTTON_ID, x, y, 16, 16, "");
			creativeContainer = (g instanceof GuiContainerCreative) ? (GuiContainerCreative)g : null;
			itemToDraw = new ItemStack(Items.book, 1);
		}
		
		public void drawButton(Minecraft mc, int mx, int my)
		{
			if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
				return;
			
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
			FTBLibClient.setTexture(TextureMap.locationItemsTexture);
			
			zLevel = 200F;
			itemRender.zLevel = 200F;
			LMRenderHelper.renderGuiItem(itemToDraw, itemRender, mc.fontRenderer, xPosition, yPosition);
			zLevel = 0F;
			itemRender.zLevel = 0F;
			
			if(mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height)
				GuiLM.drawBlankRect(xPosition, yPosition, 0D, width, height, 0x55FFFFFF);
			
			GL11.glPopAttrib();
		}
	}
}