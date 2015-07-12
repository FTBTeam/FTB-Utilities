package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiSelectColor extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/colselector_rgb.png");
	public static final TextureCoords col_tex = new TextureCoords(tex, 98, 13, 32, 16);
	
	public static final int SLIDER_W = 6, SLIDER_H = 13, SLIDER_BAR_W = 86;
	public static final TextureCoords slider_tex = new TextureCoords(tex, 98, 29, SLIDER_W, SLIDER_H);
	public static final TextureCoords slider_col_tex = new TextureCoords(tex, 98, 0, SLIDER_BAR_W, SLIDER_H);
	
	public static interface ColorSelectorCallback
	{
		public void onColorSelected(ColorSelected c);
	}
	
	public static class ColorSelected
	{
		public final boolean set;
		public final int color;
		public final int ID;
		public final boolean closeGui;
		
		public ColorSelected(boolean s, int c, int id, boolean g)
		{
			set = s;
			color = c;
			ID = id;
			closeGui = g;
		}
	}
	
	public final ColorSelectorCallback callback;
	public final int initCol;
	public final int colorID;
	public final boolean isInstant;
	
	public final ButtonLM colorInit, colorCurrent, switchHSB;
	public final SliderLM currentColR, currentColG, currentColB;
	
	public static void displayGui(ColorSelectorCallback cb, int col, int id, boolean instant)
	{
		if(FTBUClient.openHSB.getB())
			LatCoreMCClient.getMinecraft().displayGuiScreen(new GuiSelectColorHSB(cb, col, id, instant));
		else LatCoreMCClient.getMinecraft().displayGuiScreen(new GuiSelectColor(cb, col, id, instant));
	}
	
	public GuiSelectColor(ColorSelectorCallback cb, int col, int id, boolean instant)
	{
		super(new ContainerEmpty.ClientGui(), tex);
		hideNEI = true;
		callback = cb;
		initCol = LatCore.Colors.getRGBA(col, 255);
		colorID = id;
		isInstant = instant;
		
		xSize = 98;
		ySize = 76;
		
		colorInit = new ButtonLM(this, 6, 6, col_tex.width, col_tex.height)
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
		
		colorCurrent = new ButtonLM(this, 60, 6, col_tex.width, col_tex.height)
		{
			public void onButtonPressed(int b)
			{ closeGui(true); }
			
			public void addMouseOverText(FastList<String> s)
			{
				s.add(FTBULang.button_accept);
				s.add(title);
			}
		};
		
		switchHSB = new ButtonLM(this, 41, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				FTBUClient.openHSB.setValue(1);
				ClientConfig.Registry.save();
				mc.displayGuiScreen(new GuiSelectColorHSB(callback, getInitRGB(), colorID, isInstant));
			}
		};
		
		switchHSB.title = "HSB";
		
		currentColR = new SliderLM(this, 6, 25, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColR.value = LatCore.Colors.getRed(col) / 255F;
		currentColR.displayMax = 255;
		currentColR.title = EnumDyeColor.RED.toString();
		
		currentColG = new SliderLM(this, 6, 41, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColG.value = LatCore.Colors.getGreen(col) / 255F;
		currentColG.displayMax = 255;
		currentColG.title = EnumDyeColor.GREEN.toString();
		
		currentColB = new SliderLM(this, 6, 57, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColB.value = LatCore.Colors.getBlue(col) / 255F;
		currentColB.displayMax = 255;
		currentColB.title = EnumDyeColor.BLUE.toString();
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(colorInit);
		l.add(colorCurrent);
		l.add(switchHSB);
		l.add(currentColR);
		l.add(currentColG);
		l.add(currentColB);
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		int prevCol = getCurrentRGB();
		update();
		
		if(isInstant && prevCol != getCurrentRGB())
			callback.onColorSelected(new ColorSelected(true, getCurrentRGB(), colorID, false));
		
		LatCore.Colors.setGLColor(initCol, 255);
		colorInit.render(col_tex);
		GL11.glColor4f(currentColR.value, currentColG.value, currentColB.value, 1F);
		colorCurrent.render(col_tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		switchHSB.render(Icons.hsb);
		
		setTexture(tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		double z = zLevel;
		int w = slider_col_tex.width;
		int h = slider_col_tex.height;
		double u0 = slider_col_tex.minU;
		double v0 = slider_col_tex.minV;
		double u1 = slider_col_tex.maxU;
		double v1 = slider_col_tex.maxV;
		
		int x = guiLeft + currentColR.posX;
		int y = guiTop + currentColR.posY;
		
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(0F, currentColG.value, currentColB.value, 1F);
		GL11.glTexCoord2d(u0, v0); GL11.glVertex3d(x + 0, y + 0, z);
		GL11.glTexCoord2d(u0, v1); GL11.glVertex3d(x + 0, y + h, z);
		GL11.glColor4f(1F, currentColG.value, currentColB.value, 1F);
		GL11.glTexCoord2d(u1, v1); GL11.glVertex3d(x + w, y + h, z);
		GL11.glTexCoord2d(u1, v0); GL11.glVertex3d(x + w, y + 0, z);
		GL11.glEnd();
		
		x = guiLeft + currentColG.posX;
		y = guiTop + currentColG.posY;
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(currentColR.value, 0F, currentColB.value, 1F);
		GL11.glTexCoord2d(u0, v0); GL11.glVertex3d(x + 0, y + 0, z);
		GL11.glTexCoord2d(u0, v1); GL11.glVertex3d(x + 0, y + h, z);
		GL11.glColor4f(currentColR.value, 1F, currentColB.value, 1F);
		GL11.glTexCoord2d(u1, v1); GL11.glVertex3d(x + w, y + h, z);
		GL11.glTexCoord2d(u1, v0); GL11.glVertex3d(x + w, y + 0, z);
		GL11.glEnd();
		
		x = guiLeft + currentColB.posX;
		y = guiTop + currentColB.posY;
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(currentColR.value, currentColG.value, 0F, 1F);
		GL11.glTexCoord2d(u0, v0); GL11.glVertex3d(x + 0, y + 0, z);
		GL11.glTexCoord2d(u0, v1); GL11.glVertex3d(x + 0, y + h, z);
		GL11.glColor4f(currentColR.value, currentColG.value, 1F, 1F);
		GL11.glTexCoord2d(u1, v1); GL11.glVertex3d(x + w, y + h, z);
		GL11.glTexCoord2d(u1, v0); GL11.glVertex3d(x + w, y + 0, z);
		GL11.glEnd();
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		currentColR.renderSlider(slider_tex);
		currentColG.renderSlider(slider_tex);
		currentColB.renderSlider(slider_tex);
	}
	
	public void update()
	{
		currentColR.update();
		currentColG.update();
		currentColB.update();
		colorCurrent.title = LatCore.Colors.getHex(getCurrentRGB());
	}
	
	public int getInitRGB()
	{ return initCol; }
	
	public int getCurrentRGB()
	{
		int r = (int)(currentColR.value * 255F);
		int g = (int)(currentColG.value * 255F);
		int b = (int)(currentColB.value * 255F);
		return LatCore.Colors.getRGBA(r, g, b, 255);
	}
	
	public void closeGui(boolean set)
	{
		playClickSound();
		callback.onColorSelected(new ColorSelected(set, getCurrentRGB(), colorID, true));
	}
}