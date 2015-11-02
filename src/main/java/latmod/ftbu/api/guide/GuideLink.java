package latmod.ftbu.api.guide;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import ftb.lib.client.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.*;

public class GuideLink
{
	public static final int TYPE_URL = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_IMAGE_URL = 3;
	
	public final int type;
	public String link = "";
	public IChatComponent title = null;
	public IChatComponent hover = null;
	
	private TextureCoords texture = null;
	
	public GuideLink(int i)
	{ type = i; }
	
	public boolean isText()
	{ return type == TYPE_URL; }
	
	public boolean isImage()
	{ return type == TYPE_IMAGE || type == TYPE_IMAGE_URL; }
	
	@SideOnly(Side.CLIENT)
	public TextureCoords getTexture()
	{
		if(!isImage()) return null;
		if(texture != null) return texture;
		
		try
		{
			BufferedImage img = null;
			
			if(type == TYPE_IMAGE) img = ImageIO.read(new File(FTBLib.folderModpack, link));
			else if(type == TYPE_IMAGE_URL) img = ImageIO.read(new URL(link));
			
			ResourceLocation location = FTBLibClient.mc.getTextureManager().getDynamicTextureLocation("ftbu/" + link, new DynamicTexture(img));
			texture = new TextureCoords(location, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
		}
		catch(Exception e)
		{ texture = TextureCoords.nullTexture; }
		
		return texture;
	}
}