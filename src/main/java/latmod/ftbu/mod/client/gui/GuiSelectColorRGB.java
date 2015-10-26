package latmod.ftbu.mod.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import ftb.lib.EnumDyeColor;
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
public class GuiSelectColorRGB extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/colselector_rgb.png");
	public static final TextureCoords col_tex = new TextureCoords(tex, 98, 13, 32, 16);
	
	public static final int SLIDER_W = 6, SLIDER_H = 13, SLIDER_BAR_W = 86;
	public static final TextureCoords slider_tex = new TextureCoords(tex, 98, 29, SLIDER_W, SLIDER_H);
	public static final TextureCoords slider_col_tex = new TextureCoords(tex, 98, 0, SLIDER_BAR_W, SLIDER_H);
	
	public final IColorCallback callback;
	public final int initCol;
	public final Object colorID;
	public final boolean isInstant;
	
	public final ButtonLM colorInit, colorCurrent, switchHSB;
	public final SliderLM currentColR, currentColG, currentColB;
	
	public GuiSelectColorRGB(IColorCallback cb, int col, Object id, boolean instant)
	{
		super(null, tex);
		hideNEI = true;
		callback = cb;
		initCol = LMColorUtils.getRGBA(col, 255);
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
				s.add(FTBULang.button_cancel());
				s.add(title);
			}
		};
		
		colorInit.title = LMColorUtils.getHex(getInitRGB());
		
		colorCurrent = new ButtonLM(this, 60, 6, col_tex.width, col_tex.height)
		{
			public void onButtonPressed(int b)
			{ closeGui(true); }
			
			public void addMouseOverText(FastList<String> s)
			{
				s.add(FTBULang.button_accept());
				s.add(title);
			}
		};
		
		switchHSB = new ButtonLM(this, 41, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				FTBUClient.openHSB.setValue(1);
				ClientConfigRegistry.save();
				mc.displayGuiScreen(new GuiSelectColorHSB(callback, getInitRGB(), colorID, isInstant));
			}
		};
		
		switchHSB.title = "HSB";
		
		currentColR = new SliderLM(this, 6, 25, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColR.value = LMColorUtils.getRed(col) / 255F;
		currentColR.displayMax = 255;
		currentColR.title = EnumDyeColor.RED.toString();
		currentColR.scrollStep = 1F / 255F;
		
		currentColG = new SliderLM(this, 6, 41, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColG.value = LMColorUtils.getGreen(col) / 255F;
		currentColG.displayMax = 255;
		currentColG.title = EnumDyeColor.GREEN.toString();
		currentColG.scrollStep = 1F / 255F;
		
		currentColB = new SliderLM(this, 6, 57, SLIDER_BAR_W, SLIDER_H, SLIDER_W);
		currentColB.value = LMColorUtils.getBlue(col) / 255F;
		currentColB.displayMax = 255;
		currentColB.title = EnumDyeColor.BLUE.toString();
		currentColB.scrollStep = 1F / 255F;
	}
	
	public void addWidgets()
	{
		mainPanel.add(colorInit);
		mainPanel.add(colorCurrent);
		mainPanel.add(switchHSB);
		mainPanel.add(currentColR);
		mainPanel.add(currentColG);
		mainPanel.add(currentColB);
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		int prevCol = getCurrentRGB();
		update();
		
		if(isInstant && prevCol != getCurrentRGB())
			callback.onColorSelected(new ColorSelected(colorID, true, getCurrentRGB(), false));
		
		FTBLibClient.setGLColor(initCol, 255);
		colorInit.render(col_tex);
		GL11.glColor4f(currentColR.value, currentColG.value, currentColB.value, 1F);
		colorCurrent.render(col_tex);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		switchHSB.render(GuiIcons.hsb);
		
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
		colorCurrent.title = LMColorUtils.getHex(getCurrentRGB());
	}
	
	public int getInitRGB()
	{ return initCol; }
	
	public int getCurrentRGB()
	{
		int r = (int)(currentColR.value * 255F);
		int g = (int)(currentColG.value * 255F);
		int b = (int)(currentColB.value * 255F);
		return LMColorUtils.getRGBA(r, g, b, 255);
	}
	
	public void closeGui(boolean set)
	{
		playClickSound();
		callback.onColorSelected(new ColorSelected(colorID, set, set ? getCurrentRGB() : getInitRGB(), true));
	}
}