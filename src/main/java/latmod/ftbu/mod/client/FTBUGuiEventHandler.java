package latmod.ftbu.mod.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.*;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.GuiClientConfig;
import latmod.ftbu.mod.client.gui.friends.GuiFriends;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import latmod.ftbu.mod.client.gui.minimap.GuiMinimap;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.gui.*;
import latmod.lib.FastList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;

@SideOnly(Side.CLIENT)
public class FTBUGuiEventHandler
{
	public static final FTBUGuiEventHandler instance = new FTBUGuiEventHandler();
	public static final TextureCoords tex_friends = new TextureCoords(FTBU.mod.getLocation("textures/gui/friendsbutton.png"), 0, 0, 256, 256, 256, 256);
	private static final FastList<Button> buttons = new FastList<Button>();
	
	private static final int BUTTON_FRIENDS = 24285;
	private static final int BUTTON_SETTINGS = 24286;
	private static final int BUTTON_GUIDE = 24287;
	private static final int BUTTON_CLAIMS = 24288;
	private static final int BUTTON_INFO = 24289;
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void guiInitEvent(final GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(!LatCoreMCClient.isPlaying()) return;
		
		if(e.gui instanceof GuiOptions)
		{
			if(FTBUClient.optionsButton.getB())
				e.buttonList.add(new GuiButton(BUTTON_SETTINGS, e.gui.width / 2 - 155, e.gui.height / 6 + 48 - 6, 150, 20, "[FTBU] " + FTBULang.client_config()));
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
			
			buttons.clear();
			
			buttons.add(new Button(BUTTON_FRIENDS, tex_friends)
			{
				public void onPressed(GuiContainer g0)
				{
					GuiFriends g = new GuiFriends(g0);
					g.playClickSound();
					e.gui.mc.displayGuiScreen(g);
				}
			});
			
			buttons.add(new Button(BUTTON_GUIDE, null)
			{
				private RenderItem itemRender = new RenderItem();
				private ItemStack itemToRender = new ItemStack(Items.book);
				
				public void onPressed(GuiContainer g0)
				{
					GuiGuide g = new GuiGuide(null, ClientGuideFile.instance.main);
					g.playClickSound();
					e.gui.mc.displayGuiScreen(g);
				}
				
				public void render(int ax, int ay, double z)
				{
					FTBLibClient.setTexture(TextureMap.locationItemsTexture);
					
					itemRender.zLevel = 200F;
					LMRenderHelper.renderGuiItem(itemToRender, itemRender, FTBLibClient.mc.fontRenderer, ax, ay);
					itemRender.zLevel = 0F;
				}
			});
			
			buttons.add(new Button(BUTTON_CLAIMS, GuiIcons.map)
			{
				public void onPressed(GuiContainer g0)
				{ FTBLibClient.mc.displayGuiScreen(new GuiMinimap()); }
			});
			
			buttons.add(new Button(BUTTON_SETTINGS, GuiIcons.settings)
			{
				public void onPressed(GuiContainer g0)
				{ e.gui.mc.displayGuiScreen(new GuiClientConfig(g0)); }
			});
			
			buttons.add(new Button(BUTTON_INFO, GuiIcons.info)
			{
				public void onPressed(GuiContainer g0)
				{ ClientAction.ACTION_REQUEST_SERVER_INFO.send(0); }
			});
			
			for(int i = 0; i < buttons.size(); i++)
			{
				if(hasPotions)
					e.buttonList.add(new ButtonInvLM(buttons.get(i), e.gui, guiLeft + buttonX - 18 * i, guiTop + buttonY));
				else
					e.buttonList.add(new ButtonInvLM(buttons.get(i), e.gui, guiLeft + buttonX, guiTop + buttonY + 18 * i));
			}
		}
	}
	
	@SubscribeEvent
	public void guiActionEvent(GuiScreenEvent.ActionPerformedEvent.Post e)
	{
		if(e.button.id == BUTTON_SETTINGS)
			e.gui.mc.displayGuiScreen(new GuiClientConfig(e.gui));
		else if(buttons.contains(e.button.id))
		{
			final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
			
			if(creativeContainer == null || creativeContainer.func_147056_g() == CreativeTabs.tabInventory.getTabIndex())
			{
				Button b = buttons.getObj(e.button.id);
				b.onPressed((GuiContainer)e.gui);
			}
		}
	}
	
	private static class ButtonInvLM extends GuiButton
	{
		public final Button button;
		private final GuiContainerCreative creativeContainer;
		
		public ButtonInvLM(Button b, GuiScreen g, int x, int y)
		{
			super(b.ID, x, y, 16, 16, "");
			button = b;
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
			button.render(xPosition, yPosition, 0D);
			
			if(mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height)
				GuiLM.drawBlankRect(xPosition, yPosition, 0D, width, height, 0x55FFFFFF);
			
			if(button.ID == BUTTON_FRIENDS && !ClientNotifications.Perm.list.isEmpty())
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
	
	public abstract static class Button
	{
		public final int ID;
		public final TextureCoords tex;
		
		public Button(int i, TextureCoords tc)
		{ ID = i; tex = tc; }
		
		public int hashCode()
		{ return ID; }
		
		public void render(int ax, int ay, double z)
		{
			FTBLibClient.setTexture(tex.texture);
			GuiLM.drawTexturedRectD(ax, ay, z, 16, 16, tex.minU, tex.minV, tex.maxU, tex.maxV);
		}
		
		public boolean equals(Object o)
		{ return o == this || o.hashCode() == hashCode(); }
		
		public abstract void onPressed(GuiContainer g0);
	}
}