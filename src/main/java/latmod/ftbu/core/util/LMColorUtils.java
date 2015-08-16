package latmod.ftbu.core.util;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

public class LMColorUtils
{
	public static int getRGBA(int r, int g, int b, int a)
	{ return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0); }
	
	public static int getRed(int c)
	{ return (c >> 16) & 0xFF; }
	
	public static int getGreen(int c)
	{ return (c >> 8) & 0xFF; }
	
	public static int getBlue(int c)
	{ return (c >> 0) & 0xFF; }
	
	public static int getAlpha(int c)
	{ return (c >> 24) & 0xFF; }
	
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
	
	@SideOnly(Side.CLIENT)
	public static final void recolor()
	{ GL11.glColor4f(1F, 1F, 1F, 1F); }
	
	public static int getHSB(float h, float s, float b)
	{ return java.awt.Color.HSBtoRGB(h, s, b); }
	
	public static float[] getHSB(int r, int g, int b)
	{
		float[] f = new float[3];
		java.awt.Color.RGBtoHSB(r, g, b, f);
		return f;
	}
	
	public static float[] getHSB(int c)
	{ return getHSB(getRed(c), getGreen(c), getBlue(c)); }
	
	public static float getHue(int c)
	{ return getHSB(c)[0]; }
	
	public static float getSaturation(int c)
	{ return getHSB(c)[1]; }
	
	public static float getBrightness(int c)
	{ return getHSB(c)[2]; }
}