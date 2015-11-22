package latmod.ftbu.mod.client.gui;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.GlStateManager;
import ftb.lib.gui.GuiLM;
import ftb.lib.gui.widgets.*;
import latmod.ftbu.api.client.*;
import latmod.ftbu.util.client.FTBULang;
import latmod.lib.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

@SideOnly(Side.CLIENT)
public class GuiClientConfig extends GuiLM implements IClientActionGui
{
	public final GuiScreen parent;
	public final FastList<ConfigLine> lines;
	public int totalHeight = 0;
	public final ButtonLM buttonClose;
	public final SliderLM scroll;
	public int scrollI;
	
	public GuiClientConfig(GuiScreen g)
	{
		super(null, null);
		parent = g;
		hideNEI = true;
		xSize = width;
		ySize = height;
		
		lines = new FastList<ConfigLine>();
		
		buttonClose = new ButtonLM(this, -16, 2, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				if(parent == null) container.player.closeScreen();
				else mc.displayGuiScreen(parent);
			}
		};
		
		scroll = new SliderLM(this, -16, 20, 16, 0, 10)
		{
			public boolean canMouseScroll()
			{ return true; }
		};
		scroll.isVertical = true;
		scroll.displayMin = scroll.displayMax = 0;
		
		totalHeight = 20;
	}
	
	public void initLMGui()
	{
		xSize = width;
		ySize = height;
		guiLeft = guiTop = 0;
		buttonClose.posX = width - 18;
		scroll.posX = width - 16;
		scroll.height = height - 20;
		scrollI = 0;
		scroll.value = 0F;
		refreshWidgets();
	}
	
	public void addWidgets()
	{
		mainPanel.add(buttonClose);
		if(totalHeight > height) mainPanel.add(scroll);
		
		lines.clear();
		totalHeight = 20;
		for(ClientConfig c : ClientConfigRegistry.map.values)
		{
			if(!c.isHidden)
			{
				ButtonCategory cat = new ButtonCategory(this, c);
				lines.add(cat);
				
				for(ClientConfigProperty p : c.map.values)
				{
					p.initGui();
					lines.add(new ButtonConfig(cat, p));
				}
			}
		}
		mainPanel.addAll(lines);
	}
	
	public void onLMGuiClosed()
	{
		ClientConfigRegistry.save();
	}
	
	public void drawBackground()
	{
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		boolean drawScroll = totalHeight > height;
		
		if(drawScroll)
		{
			float pvalue = scroll.value;
			scroll.update();
			
			int dw = Mouse.getDWheel();
			if(dw != 0)
			{
				float s = (20F / (float)(height - totalHeight)) * 3F;
				if(dw < 0) scroll.value -= s;
				else scroll.value += s;
				scroll.value = MathHelperLM.clampFloat(scroll.value, 0F, 1F);
			}
			
			if(scroll.value != pvalue)
				scrollI = (int)(scroll.value * (height - totalHeight));
		}
		
		for(ConfigLine l : lines) l.renderLine();
		
		drawRect(0, 0, width, 20, 0x99333333);
		drawCenteredString(fontRendererObj, FTBULang.client_config(), width / 2, 6, 0xFFFFFFFF);
		
		if(drawScroll)
		{
			drawRect(scroll.posX, scroll.posY, scroll.posX + scroll.width, scroll.posY + scroll.height, 0x99333333);
			int sy = (int)(scroll.posY + scroll.value * (scroll.height - scroll.sliderSize));
			drawRect(scroll.posX, sy, scroll.posX + scroll.width, sy + scroll.sliderSize, 0x99666666);
		}
		
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		buttonClose.render(GuiIcons.accept);
	}
	
	public static abstract class ConfigLine extends ButtonLM
	{
		public final GuiClientConfig gui;
		public final ClientConfig config;
		
		public ConfigLine(GuiClientConfig g, ClientConfig c)
		{
			super(g, 0, g.totalHeight, g.width - 16, 16);
			gui = g;
			config = c;
			g.totalHeight += height;
			title = c.getIDS();
		}
		
		public boolean isVisible()
		{ return posY + gui.scrollI >= -height && posY + gui.scrollI <= gui.height; }
		
		public abstract void renderLine();
		public abstract void onButtonPressed(int b);
		
		public void addMouseOverText(FastList<String> s) { }
		
		public boolean isAt(int x, int y)
		{ return x >= 0 && y >= posY + gui.scrollI && x < gui.width - 16 && y < posY + gui.scrollI + height; }
		
		public boolean mouseOver()
		{ return isAt(gui.mouseX, gui.mouseY); }
	}
	
	public static class ButtonCategory extends ConfigLine
	{
		public ButtonCategory(GuiClientConfig g, ClientConfig c)
		{
			super(g, c);
			title = EnumChatFormatting.BOLD + "- " + c.getIDS() + " -";
		}
		
		public void renderLine()
		{
			if(!isVisible()) return;
			int y = posY + gui.scrollI;
			gui.drawString(gui.fontRendererObj, title, 4, y + 4, 0xFFFFFFFF);
		}
		
		public void onButtonPressed(int b)
		{
		}
	}
	
	public static class ButtonConfig extends ConfigLine
	{
		public final ButtonCategory category;
		public final ClientConfigProperty prop;
		
		public ButtonConfig(ButtonCategory c, ClientConfigProperty p)
		{
			super(c.gui, c.config);
			category = c;
			prop = p;
			title = prop.getIDS();
		}
		
		public void renderLine()
		{
			if(!isVisible()) return;
			boolean mouseOver = mouseOver();
			int i = prop.getI();
			int textCol = mouseOver ? prop.texColMO[i] : prop.texCol[i];
			int y = posY + gui.scrollI;
			gui.drawString(gui.fontRendererObj, title, 4, y + 4, mouseOver ? 0xFFFFFFFF : 0xFF999999);
			String s = prop.getValueS(i);
			gui.drawString(gui.fontRendererObj, s, gui.width - (gui.fontRendererObj.getStringWidth(s) + 20), y + 4, textCol);
		}
		
		public void onButtonPressed(int b)
		{
			gui.playClickSound();
			prop.onClicked();
		}
	}
	
	public void onClientDataChanged()
	{ refreshWidgets(); }
}