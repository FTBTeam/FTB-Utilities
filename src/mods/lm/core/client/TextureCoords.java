package mods.lm.core.client;
import cpw.mods.fml.relauncher.*;

public class TextureCoords
{
	public String name = null;
	public int posX, posY, size;
	
	@SideOnly(Side.CLIENT)
	public SpriteSheet sheet;
	
	public TextureCoords(int x, int y, int s1)
	{ posX = x; posY = y; size = s1; }
	
	public TextureCoords(int x, int y)
	{ this(x, y, 1); }
	
	public TextureCoords setName(String s)
	{ name = s; return this; }
	
	public boolean isAt(int x, int y, int w, int h)
	{ return x >= posX && y > posY && x <= posX + w && y <= posY + h; }
}