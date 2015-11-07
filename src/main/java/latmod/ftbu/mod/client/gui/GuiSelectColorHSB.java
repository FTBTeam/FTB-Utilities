package latmod.ftbu.mod.client.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import ftb.lib.EnumDyeColor;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.*;
import latmod.ftbu.api.client.ClientConfigRegistry;
import latmod.ftbu.api.client.callback.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.util.client.FTBULang;
import latmod.ftbu.util.gui.*;
import latmod.lib.*;
import net.minecraft.util.ResourceLocation;

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
	
	public final IColorCallback callback;
	public final int initCol;
	public final Object colorID;
	public final boolean isInstant;
	public int currentColor;
	
	public final ButtonLM colorInit, colorCurrent, switchRGB;
	public final SliderLM sliderBrightness;
	public final ColorSelector colorSelector;
	
	public GuiSelectColorHSB(IColorCallback cb, int col, Object id, boolean instant)
	{
		super(null, tex);
		hideNEI = true;
		callback = cb;
		currentColor = initCol = LMColorUtils.getRGBA(col, 255);
		colorID = id;
		isInstant = instant;
		
		xSize = 76;
		ySize = 107;
		
		colorInit = new ButtonLM(this, 6, 5, col_tex.widthI, col_tex.heightI)
		{
			public void onButtonPressed(int b)
			{ closeGui(false); }
			
			public void addMouseOverText(FastList<String> s)
			{
				s.add(FTBULang.button_cancel());
				s.add(title);
			}
		};
		
		colorInit.title = LMColorUtils.getHex(getInitRGB());
		
		colorCurrent = new ButtonLM(this, 49, 5, col_tex.widthI, col_tex.heightI)
		{
			public void onButtonPressed(int b)
			{ closeGui(true); }
			
			public void addMouseOverText(FastList<String> s)
			{
				s.add(FTBULang.button_accept());
				s.add(title);
			}
		};
		
		switchRGB = new ButtonLM(this, 30, 5, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				FTBUClient.openHSB.setValue(0);
				ClientConfigRegistry.save();
				mc.displayGuiScreen(new GuiSelectColorRGB(callback, getInitRGB(), colorID, isInstant));
			}
		};
		
		switchRGB.title = "RGB";
		
		sliderBrightness = new SliderLM(this, 6, 91, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		LMColorUtils.setHSB(col);
		sliderBrightness.value = LMColorUtils.getHSBBrightness();
		sliderBrightness.displayMax = 255;
		sliderBrightness.title = EnumDyeColor.BLACK.toString();
		sliderBrightness.scrollStep = 1F / 255F;
		
		colorSelector = new ColorSelector(this, 6, 24, 64, 64);
	}
	
	public void addWidgets()
	{
		mainPanel.add(colorInit);
		mainPanel.add(colorCurrent);
		mainPanel.add(switchRGB);
		mainPanel.add(sliderBrightness);
		mainPanel.add(colorSelector);
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
		
		FTBLibClient.setGLColor(initCol, 255);
		colorInit.render(col_tex);
		FTBLibClient.setGLColor(currentColor, 255);
		colorCurrent.render(col_tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		switchRGB.render(GuiIcons.rgb);
		
		setTexture(tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		double z = zLevel;
		double w = slider_col_tex.width;
		double h = slider_col_tex.height;
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
		FTBLibClient.setGLColor(currentColor, 255);
		GL11.glTexCoord2d(u1, v1); GL11.glVertex3d(x + w, y + h, z);
		GL11.glTexCoord2d(u1, v0); GL11.glVertex3d(x + w, y + 0, z);
		GL11.glEnd();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		colorSelector.renderWidget();
		
		sliderBrightness.renderSlider(slider_tex);
	}
	
	public void update()
	{
		sliderBrightness.update();
		colorCurrent.title = LMColorUtils.getHex(currentColor);
	}
	
	public void updateColor()
	{
		float h = (float)(Math.atan2(colorSelector.cursorPosY - 0.5D, colorSelector.cursorPosX - 0.5D) / MathHelperLM.TWO_PI);
		float s = (float)(MathHelperLM.dist(colorSelector.cursorPosX, colorSelector.cursorPosY, 0D, 0.5D, 0.5D, 0D) * 2D);
		currentColor = LMColorUtils.getHSB(h, s, sliderBrightness.value);
		if(isInstant) callback.onColorSelected(new ColorSelected(colorID, true, currentColor, false));
	}
	
	public int getInitRGB()
	{ return initCol; }
	
	public void closeGui(boolean set)
	{
		playClickSound();
		callback.onColorSelected(new ColorSelected(colorID, set, set ? currentColor : getInitRGB(), true));
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
			//cursorPosX = Math.sin(0D) + 0.5D;
			//cursorPosY = Math.cos(0D) + 0.5D;
		}

		public void renderWidget()
		{
			int ax = getAX();
			int ay = getAY();
			
			if(grabbed && !Mouse.isButtonDown(0)) grabbed = false;
			
			if(grabbed)
			{
				cursorPosX = (gui.mouseX - ax) / (double)width;
				cursorPosY = (gui.mouseY - ay) / (double)height;
				
				double s = MathHelperLM.dist(cursorPosX, cursorPosY, 0D, 0.5D, 0.5D, 0D) * 2D;
				
				if(s > 1D)
				{
					cursorPosX = (cursorPosX - 0.5D) / s + 0.5D;
					cursorPosY = (cursorPosY - 0.5D) / s + 0.5D;
				}
				
				cursorPosX = MathHelperLM.clamp(cursorPosX, 0D, 1D);
				cursorPosY = MathHelperLM.clamp(cursorPosY, 0D, 1D);
				
				gui.updateColor();
			}
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
			gui.setTexture(tex_colors);
			GuiLM.drawTexturedRectD(ax, ay, gui.zLevel, width, height, 0D, 0D, 1D, 1D);
			
			if(cursorPosX >= 0D && cursorPosY >= 0D)
			{
				GL11.glColor4f(1F - LMColorUtils.getRed(gui.currentColor) / 255F, 1F - LMColorUtils.getGreen(gui.currentColor) / 255F, 1F - LMColorUtils.getBlue(gui.currentColor) / 255F, 1F);
				gui.render(cursor_tex, ax + cursorPosX * width - 2, ay + cursorPosY * height - 2, 4, 4);
				GL11.glColor4f(1F, 1F, 1F, 1F);
			}
		}
		
		public void mousePressed(int b)
		{ if(b == 0 && mouseOver()) grabbed = true; }
	}
}