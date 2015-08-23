package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiClientConfig extends GuiLM
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
				mc.displayGuiScreen(parent);
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
		
		for(ClientConfig c : ClientConfig.Registry.map.values)
		{
			if(!c.isHidden)
			{
				ButtonCategory cat = new ButtonCategory(this, c);
				lines.add(cat);
				
				for(ClientConfig.Property p : c.map.values)
				{
					p.initGui();
					lines.add(new ButtonConfig(cat, p));
				}
			}
		}
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
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(buttonClose);
		if(totalHeight > height) l.add(scroll);
		l.addAll(lines);
	}
	
	public void onLMGuiClosed()
	{
		ClientConfig.Registry.save();
	}
	
	public void drawBackground()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		LMColorUtils.recolor();
		
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
		drawCenteredString(fontRendererObj, FTBULang.client_config, width / 2, 6, 0xFFFFFFFF);
		
		if(drawScroll)
		{
			drawRect(scroll.posX, scroll.posY, scroll.posX + scroll.width, scroll.posY + scroll.height, 0x99333333);
			int sy = (int)(scroll.posY + scroll.value * (scroll.height - scroll.sliderSize));
			drawRect(scroll.posX, sy, scroll.posX + scroll.width, sy + scroll.sliderSize, 0x99666666);
		}
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		LMColorUtils.recolor();
		
		buttonClose.render(Icons.accept);
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
			title = LatCoreMC.FORMATTING + "l- " + c.getIDS() + " -";
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
		public final ClientConfig.Property prop;
		
		public ButtonConfig(ButtonCategory c, ClientConfig.Property p)
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
}