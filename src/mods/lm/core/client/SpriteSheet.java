package mods.lm.core.client;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import mods.lm.core.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class SpriteSheet
{
	public final TextureMap map;
	public final String sheetName;
	public final ResourceLocation path;
	public int textureScale = 1;
	public int gridX, gridY;
	private SpriteTexture[] icons;
	public BufferedImage texture;
	private int scaleX, scaleY;
	public boolean printLoadedIcons = false;
	
	public SpriteSheet(TextureMap m, String s, ResourceLocation loc)
	{
		map = m;
		sheetName = s;
		path = loc;
		
		try
		{
			String s1 = LMUtils.getPath(loc);
			texture = ImageIO.read(SpriteSheet.class.getResourceAsStream(s1));
			int w = texture.getWidth();
			int h = texture.getHeight();
			gridX = w / 16; gridY = h / 16;
			scaleX = w / gridX; scaleY = h / gridY;
			icons = new SpriteTexture[gridX * gridY];
			System.out.println("Loaded sprite sheet '" + sheetName + "' with grid " + gridX + ":" + gridY + " and size " + w + ":" + h + " from " + s1);
		}
		catch(Exception e)
		{ System.err.println("Failed to load sprite sheet '" + s + "'!"); e.printStackTrace(System.err); }
	}
	
	private SpriteTexture getIcon(int i, int s)
	{
		if(i >= 0 && i < icons.length)
		{
			if(icons[i] == null)
			{
				icons[i] = new SpriteTexture(sheetName + "_" + i, this, i, s);
				map.setTextureEntry(sheetName + "_" + i, icons[i]);
			}
		}
		else
		{
			System.out.println("Icon " + i + " from '" + sheetName + "' is Waaaaay out of bounds ( " + (icons.length - 1) + " )");
			return getIcon(0, s);
		}
		
		return icons[i];
	}
	
	public SpriteTexture get(TextureCoords t)
	{ return (t == null) ? null : get(t.posX * textureScale, t.posY * textureScale, t.size * textureScale); }
	
	public SpriteTexture get(int x, int y, int s)
	{ return getIcon((x * textureScale) % gridX + y * textureScale * gridX, s * textureScale); }
	
	public SpriteTexture get(int x, int y)
	{ return get(x, y, 1); }
	
	// END //
	
	@SideOnly(Side.CLIENT)
	public static class SpriteTexture extends TextureAtlasSprite
	{
		public SpriteSheet parent;
		public int[] pixels;
		public String textureFile;
		public int posX, posY, S;
		
		public SpriteTexture(String s, SpriteSheet b, int i, int siz)
		{
			super(s);
			parent = b;
			posX = i % b.gridX;
			posY = i / b.gridX;
			S = siz;
			
			if(parent.printLoadedIcons)
			{
				StringBuilder sb = new StringBuilder();
				sb.append("Added sprite '" + s + "' from '" + b.sheetName + "' @ ");
				sb.append(LMUtils.stripe(posX, posY, siz, siz));
				System.out.println(sb.toString());
			}
		}
		
		public void updateAnimation() { ++tickCounter; }
		
		@SuppressWarnings("all")
		public boolean load(ResourceManager manager, ResourceLocation location) throws IOException
		{
			int x = posX * parent.scaleX;
			int y = posY * parent.scaleY;
			width = height = S * 16;
			frameCounter = 0;
			tickCounter = 0;
			framesTextureData.clear();
			rotated = false;
			pixels = new int[width * height];
			parent.texture.getRGB(x, y, width, height, pixels, 0, width);
			framesTextureData.add(pixels);
			return true;
		}
	}
}