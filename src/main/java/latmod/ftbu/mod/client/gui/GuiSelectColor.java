package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiSelectColor extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/colselector.png");
	public static final TextureCoords col_tex = new TextureCoords(tex, 143, 10, 28, 36);
	public static final int SLIDER_W = 6, SLIDER_H = 10, SLIDER_BAR_W = 51;
	public static final TextureCoords slider_tex = new TextureCoords(tex, 143, 0, SLIDER_W, SLIDER_H);
	public static final TextureCoords slider_col_tex = new TextureCoords(tex, 149, 0, SLIDER_BAR_W, SLIDER_H);
	
	public static interface ColorSelectorCallback
	{
		public void onColorSelected(boolean set, int color, int ID);
	}
	
	public final ColorSelectorCallback callback;
	public final float initColR, initColG, initColB;
	public final int colorID;
	
	public final ButtonLM colorInit, colorCurrent, buttonAccept, buttonCancel;
	public final SliderLM currentColR, currentColG, currentColB;
	
	public GuiSelectColor(ColorSelectorCallback cb, int col, int id)
	{
		super(new ContainerEmpty.ClientGui(), tex);
		callback = cb;
		initColR = LatCore.Colors.getRed(col) / 255F;
		initColG = LatCore.Colors.getGreen(col) / 255F;
		initColB = LatCore.Colors.getBlue(col) / 255F;
		colorID = id;
		
		xSize = 143;
		ySize = 48;
		
		colorInit = new ButtonLM(this, 7, 6, col_tex.width, col_tex.height)
		{
			public void onButtonPressed(int b)
			{
			}
		};
		
		colorInit.title = LatCore.Colors.getHex(getInitRGB());
		
		colorCurrent = new ButtonLM(this, 92, 6, col_tex.width, col_tex.height)
		{
			public void onButtonPressed(int b)
			{
			}
		};
		
		buttonAccept = new ButtonLM(this, 123, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{ closeGui(true); }
		};
		
		buttonAccept.title = FTBULang.button_accept;
		
		buttonCancel = new ButtonLM(this, 123, 26, 16, 16)
		{
			public void onButtonPressed(int b)
			{ closeGui(false); }
		};
		
		buttonCancel.title = FTBULang.button_cancel;
		
		currentColR = new SliderLM(this, 38, 6, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColR.value = initColR;
		currentColR.displayMax = 255;
		currentColR.title = EnumDyeColor.RED.toString();
		
		currentColG = new SliderLM(this, 38, 19, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColG.value = initColG;
		currentColG.displayMax = 255;
		currentColG.title = EnumDyeColor.GREEN.toString();
		
		currentColB = new SliderLM(this, 38, 32, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColB.value = initColB;
		currentColB.displayMax = 255;
		currentColB.title = EnumDyeColor.BLUE.toString();
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(colorInit);
		l.add(colorCurrent);
		l.add(buttonAccept);
		l.add(buttonCancel);
		l.add(currentColR);
		l.add(currentColG);
		l.add(currentColB);
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		currentColR.update();
		currentColG.update();
		currentColB.update();
		colorCurrent.title = LatCore.Colors.getHex(getCurrentRGB());
		
		GL11.glColor4f(initColR, initColG, initColB, 1F);
		colorInit.render(col_tex);
		GL11.glColor4f(currentColR.value, currentColG.value, currentColB.value, 1F);
		colorCurrent.render(col_tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		buttonAccept.render(Icons.accept);
		buttonCancel.render(Icons.cancel);
		
		setTexture(tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		int w = slider_col_tex.width;
		int h = slider_col_tex.height;
		double z = zLevel;
		
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
	
	public int getInitRGB()
	{
		int r = (int)(initColR * 255F);
		int g = (int)(initColG * 255F);
		int b = (int)(initColB * 255F);
		return LatCore.Colors.getRGBA(r, g, b, 255);
	}
	
	public int getCurrentRGB()
	{
		int r = (int)(currentColR.value * 255F);
		int g = (int)(currentColG.value * 255F);
		int b = (int)(currentColB.value * 255F);
		return LatCore.Colors.getRGBA(r, g, b, 255);
	}
	
	public void closeGui(boolean set)
	{ callback.onColorSelected(set, getCurrentRGB(), colorID); }
}