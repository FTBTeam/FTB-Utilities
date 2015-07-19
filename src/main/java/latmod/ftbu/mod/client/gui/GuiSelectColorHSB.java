package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.mod.client.gui.GuiSelectColor.ColorSelected;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiSelectColorHSB extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/colselector_hsb.png");
	public static final ResourceLocation tex_colors = FTBU.mod.getLocation("textures/gui/colselector_hsb_colors.png");
	public static final TextureCoords col_tex = new TextureCoords(tex, 76, 10, 21, 16);
	public static final TextureCoords cursor_tex = new TextureCoords(tex, 97, 20, 4, 4);
	
	public static final int SLIDER_W = 6, SLIDER_H = 10, SLIDER_BAR_W = 64;
	public static final TextureCoords slider_tex = new TextureCoords(tex, 97, 10, SLIDER_W, SLIDER_H);
	public static final TextureCoords slider_col_tex = new TextureCoords(tex, 76, 0, SLIDER_BAR_W, SLIDER_H);
	
	public final GuiSelectColor.ColorSelectorCallback callback;
	public final int initCol;
	public final int colorID;
	public final boolean isInstant;
	public int currentColor;
	
	public final ButtonLM colorInit, colorCurrent, switchRGB;
	public final SliderLM sliderBrightness;
	public final ColorSelector colorSelector;
	
	public GuiSelectColorHSB(GuiSelectColor.ColorSelectorCallback cb, int col, int id, boolean instant)
	{
		super(null, tex);
		hideNEI = true;
		callback = cb;
		currentColor = initCol = LatCore.Colors.getRGBA(col, 255);
		colorID = id;
		isInstant = instant;
		
		xSize = 76;
		ySize = 107;
		
		colorInit = new ButtonLM(this, 6, 5, col_tex.width, col_tex.height)
		{
			public void onButtonPressed(int b)
			{ closeGui(false); }
			
			public void addMouseOverText(FastList<String> s)
			{
				s.add(FTBULang.button_cancel);
				s.add(title);
			}
		};
		
		colorInit.title = LatCore.Colors.getHex(getInitRGB());
		
		colorCurrent = new ButtonLM(this, 49, 5, col_tex.width, col_tex.height)
		{
			public void onButtonPressed(int b)
			{ closeGui(true); }
			
			public void addMouseOverText(FastList<String> s)
			{
				s.add(FTBULang.button_accept);
				s.add(title);
			}
		};
		
		switchRGB = new ButtonLM(this, 30, 5, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				FTBUClient.openHSB.setValue(0);
				ClientConfig.Registry.save();
				mc.displayGuiScreen(new GuiSelectColor(callback, getInitRGB(), colorID, isInstant));
			}
		};
		
		switchRGB.title = "RGB";
		
		sliderBrightness = new SliderLM(this, 6, 91, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		sliderBrightness.value = LatCore.Colors.getBrightness(col);
		sliderBrightness.displayMax = 255;
		sliderBrightness.title = EnumDyeColor.BLACK.toString();
		
		colorSelector = new ColorSelector(this, 6, 24, 64, 64);
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(colorInit);
		l.add(colorCurrent);
		l.add(switchRGB);
		l.add(sliderBrightness);
		l.add(colorSelector);
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		float br0 = sliderBrightness.value;
		update();
		if(sliderBrightness.value != br0)
		{
			updateColor();
			ColorSelector.shouldRedraw = true;
		}
		
		LatCore.Colors.setGLColor(initCol, 255);
		colorInit.render(col_tex);
		LatCore.Colors.setGLColor(currentColor, 255);
		colorCurrent.render(col_tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		switchRGB.render(Icons.rgb);
		
		setTexture(tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		double z = zLevel;
		int w = slider_col_tex.width;
		int h = slider_col_tex.height;
		double u0 = slider_col_tex.minU;
		double v0 = slider_col_tex.minV;
		double u1 = slider_col_tex.maxU;
		double v1 = slider_col_tex.maxV;
		
		int x = guiLeft + sliderBrightness.posX;
		int y = guiTop + sliderBrightness.posY;
		
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(0F, 0F, 0F, 1F);
		GL11.glTexCoord2d(u0, v0); GL11.glVertex3d(x + 0, y + 0, z);
		GL11.glTexCoord2d(u0, v1); GL11.glVertex3d(x + 0, y + h, z);
		LatCore.Colors.setGLColor(currentColor, 255);
		GL11.glTexCoord2d(u1, v1); GL11.glVertex3d(x + w, y + h, z);
		GL11.glTexCoord2d(u1, v0); GL11.glVertex3d(x + w, y + 0, z);
		GL11.glEnd();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		colorSelector.render();
		
		sliderBrightness.renderSlider(slider_tex);
	}
	
	public void update()
	{
		sliderBrightness.update();
		colorCurrent.title = LatCore.Colors.getHex(currentColor);
	}
	
	public void updateColor()
	{
		float h = (float)(Math.atan2(colorSelector.cursorPosY - 0.5D, colorSelector.cursorPosX - 0.5D) / MathHelperLM.TWO_PI);
		float s = (float)(MathHelperLM.dist(colorSelector.cursorPosX, colorSelector.cursorPosY, 0D, 0.5D, 0.5D, 0D) * 2D);
		currentColor = LatCore.Colors.getHSB(h, s, sliderBrightness.value);
		if(isInstant) callback.onColorSelected(new ColorSelected(true, currentColor, colorID, false));
	}
	
	public int getInitRGB()
	{ return initCol; }
	
	public void closeGui(boolean set)
	{
		playClickSound();
		callback.onColorSelected(new ColorSelected(set, currentColor, colorID, true));
	}
	
	public static class ColorSelector extends WidgetLM
	{
		public static boolean shouldRedraw = true;
		
		public final GuiSelectColorHSB gui;
		public boolean grabbed = false;
		public double cursorPosX = 0D;
		public double cursorPosY = 0D;
		
		public ColorSelector(GuiSelectColorHSB g, int x, int y, int w, int h)
		{
			super(g, x, y, w, h);
			gui = g;
			cursorPosX = cursorPosY = -1D;
			shouldRedraw = true;
		}

		public void render()
		{
			if(grabbed && !Mouse.isButtonDown(0)) grabbed = false;
			
			if(grabbed)
			{
				cursorPosX = MathHelperLM.clamp((gui.mouseXR - posX) / (double)width, 0D, 1D);
				cursorPosY = MathHelperLM.clamp((gui.mouseYR - posY) / (double)height, 0D, 1D);
				
				double s = MathHelperLM.dist(cursorPosX, cursorPosY, 0D, 0.5D, 0.5D, 0D) * 2D;
				
				if(s > 1D)
				{
					cursorPosX = (cursorPosX - 0.5D) / s + 0.5D;
					cursorPosY = (cursorPosY - 0.5D) / s + 0.5D;
				}
				
				gui.updateColor();
			}
			
			float br = gui.sliderBrightness.value;
			GL11.glColor4f(br, br, br, 1F);
			
			gui.setTexture(tex_colors);
			GuiLM.drawTexturedRectD(gui.guiLeft + posX, gui.guiTop + posY, gui.zLevel, width, height, 0D, 0D, 1D, 1D);
			
			GL11.glPopAttrib();
			
			if(cursorPosX >= 0D && cursorPosY >= 0D)
			{
				GL11.glColor4f(1F - LatCore.Colors.getRed(gui.currentColor) / 255F, 1F - LatCore.Colors.getGreen(gui.currentColor) / 255F, 1F - LatCore.Colors.getBlue(gui.currentColor) / 255F, 1F);
				cursor_tex.render(gui, posX + cursorPosX * width - 2, posY + cursorPosY * height - 2, 4, 4);
				GL11.glColor4f(1F, 1F, 1F, 1F);
			}
		}
		
		public void mousePressed(int b)
		{ if(b == 0 && mouseOver()) grabbed = true; }
	}
}