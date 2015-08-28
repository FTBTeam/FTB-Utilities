package latmod.ftbu.core.util;

import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

public class LMColorUtils
{
	public static final int[] chatFormattingColors = new int[16];
	private static final float[] staticHSB = new float[3];
	
	static
	{
		for(int i = 0; i < 16; i++)
        {
            int j = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + j;
            int g = (i >> 1 & 1) * 170 + j;
            int b = (i >> 0 & 1) * 170 + j;
            if(i == 6) r += 85;
            chatFormattingColors[i] = getRGBA(r, g, b, 255);
        }
	}
	
	public static int getColorFrom(EnumChatFormatting e)
	{ return e.isColor() ? chatFormattingColors[e.ordinal()] : 0xFFFFFFFF; }
	
	public static int getRGBA(int r, int g, int b, int a)
	{ return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255) << 0); }
	
	public static int getRed(int c)
	{ return (c >> 16) & 255; }
	
	public static int getGreen(int c)
	{ return (c >> 8) & 255; }
	
	public static int getBlue(int c)
	{ return (c >> 0) & 255; }
	
	public static int getAlpha(int c)
	{ return (c >> 24) & 255; }
	
	public static String getHex(int c)
	{ return "#" + Integer.toHexString(getRGBA(c, 255)).substring(2).toUpperCase(); }
	
	public static int getRGBA(int c, int a)
	{ return getRGBA(getRed(c), getGreen(c), getBlue(c), a); }
	
	@SideOnly(Side.CLIENT)
	public static void setGLColor(int c, int a)
	{
		int r = getRed(c); int g = getGreen(c); int b = getBlue(c);
		GL11.glColor4f(r / 255F, g / 255F, b / 255F, a / 255F);
	}
	
	@SideOnly(Side.CLIENT)
	public static void setGLColor(int c)
	{ setGLColor(c, getAlpha(c)); }
	
	public static int getHSB(float h, float s, float b)
	{ return java.awt.Color.HSBtoRGB(h, s, b); }
	
	public static void setHSB(int r, int g, int b)
	{ java.awt.Color.RGBtoHSB(r, g, b, staticHSB); }
	
	public static void setHSB(int c)
	{ setHSB(getRed(c), getGreen(c), getBlue(c)); }
	
	public static float getHSBHue()
	{ return staticHSB[0]; }
	
	public static float getHSBSaturation()
	{ return staticHSB[1]; }
	
	public static float getHSBBrightness()
	{ return staticHSB[2]; }
	
	public static int addBrightness(int c, int b)
	{
		int red = MathHelperLM.clampInt(getRed(c) + b, 0, 255);
		int green = MathHelperLM.clampInt(getGreen(c) + b, 0, 255);
		int blue = MathHelperLM.clampInt(getBlue(c) + b, 0, 255);
		return getRGBA(red, green, blue, getAlpha(c));
	}
}