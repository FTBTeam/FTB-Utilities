package latmod.ftbu.mod.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.gui.FTBLibLang;
import ftb.lib.client.*;
import ftb.lib.gui.GuiLM;
import ftb.lib.mod.client.gui.GuiEditConfig;
import ftb.lib.notification.ClientNotifications;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.friends.*;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import latmod.lib.config.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.event.GuiScreenEvent;

@SideOnly(Side.CLIENT)
public class FTBUGuiEventHandler
{
	public static final FTBUGuiEventHandler instance = new FTBUGuiEventHandler();
	public static final TextureCoords tex_friends = new TextureCoords(FTBU.mod.getLocation("textures/gui/friendsbutton.png"), 0, 0, 256, 256, 256, 256);
	private static final FastList<PlayerSelfAction> buttons = new FastList<PlayerSelfAction>();
	
	private static int nextID = 24280;
	public static final int getNextButtonID()
	{ return ++nextID; }
	
	public static final PlayerSelfAction friends = new PlayerSelfAction(tex_friends)
	{
		public void onClicked(LMPlayerClient p)
		{
			GuiFriends g = new GuiFriends(FTBLibClient.mc.currentScreen);
			g.playClickSound();
			FTBLibClient.mc.displayGuiScreen(g);
		}
		
		public String getTitle()
		{ return "FriendsGUI"; }
	};
	
	public static final ConfigGroup config_buttons = new ConfigGroup("sidebar_buttons");
	public static final ConfigEntryBool button_guide = new ConfigEntryBool("button_guide", true);
	public static final ConfigEntryBool button_info = new ConfigEntryBool("button_info", true);
	public static final ConfigEntryBool button_claims = new ConfigEntryBool("button_claims", true);
	public static final ConfigEntryBool button_settings = new ConfigEntryBool("button_settings", true);
	
	public static void init()
	{
		config_buttons.add(button_guide);
		config_buttons.add(button_info);
		config_buttons.add(button_claims);
		config_buttons.add(button_settings);
		ClientConfigRegistry.add(config_buttons);
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void guiInitEvent(final GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(!LatCoreMCClient.isPlaying()) return;
		
		if(e.gui instanceof GuiOptions)
		{
			if(FTBUClient.optionsButton.get())
				e.buttonList.add(new GuiButton(PlayerSelfAction.settings.ID, e.gui.width / 2 - 155, e.gui.height / 6 + 48 - 6, 150, 20, "[FTBU] " + FTBLibLang.client_config()));
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
			
			buttons.add(friends);
			if(button_guide.get()) buttons.add(PlayerSelfAction.guide);
			if(button_info.get()) buttons.add(PlayerSelfAction.info);
			if(button_claims.get()) buttons.add(PlayerSelfAction.claims);
			if(button_settings.get()) buttons.add(PlayerSelfAction.settings);
			
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
		if(e.button.id == PlayerSelfAction.settings.ID)
			e.gui.mc.displayGuiScreen(new GuiEditConfig(e.gui, ClientConfigRegistry.provider));
		else if(buttons.contains(e.button.id))
		{
			final GuiContainerCreative creativeContainer = (e.gui instanceof GuiContainerCreative) ? (GuiContainerCreative)e.gui : null;
			
			if(creativeContainer == null || creativeContainer.func_147056_g() == CreativeTabs.tabInventory.getTabIndex())
			{
				PlayerSelfAction b = buttons.getObj(e.button.id);
				b.onClicked(LMWorldClient.inst.getClientPlayer());
			}
		}
	}
	
	private static class ButtonInvLM extends GuiButton
	{
		public final PlayerSelfAction button;
		private final GuiContainerCreative creativeContainer;
		
		public ButtonInvLM(PlayerSelfAction b, GuiScreen g, int x, int y)
		{
			super(b.ID, x, y, 16, 16, "");
			button = b;
			creativeContainer = (g instanceof GuiContainerCreative) ? (GuiContainerCreative)g : null;
		}
		
		public void drawButton(Minecraft mc, int mx, int my)
		{
			if(creativeContainer != null && creativeContainer.func_147056_g() != CreativeTabs.tabInventory.getTabIndex())
				return;
			
			GlStateManager.pushAttrib();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			button.render(xPosition, yPosition, 0D);
			
			if(mx >= xPosition && my >= yPosition && mx < xPosition + width && my < yPosition + height)
				GuiLM.drawBlankRect(xPosition, yPosition, 0D, width, height, 0x55FFFFFF);
			
			if(button.ID == friends.ID && !ClientNotifications.Perm.list.isEmpty())
			{
				String n = String.valueOf(ClientNotifications.Perm.list.size());
				int nw = mc.fontRenderer.getStringWidth(n);
				GlStateManager.color(1F, 1F, 1F, 1F);
				GlStateManager.disableTexture();
				GuiLM.drawRect(xPosition + width - nw, yPosition - 4, xPosition + width + 1, yPosition + 5, 0xAAFF2222);
				GlStateManager.enableTexture();
				mc.fontRenderer.drawString(n, xPosition + width - nw + 1, yPosition - 3, 0xFFFFFFFF);
			}
			
			GlStateManager.popAttrib();
		}
	}
}