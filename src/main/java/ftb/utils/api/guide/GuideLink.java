package ftb.utils.api.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.item.ItemStackTypeAdapter;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class GuideLink
{
	public final LinkType type;
	public final String link;
	public IChatComponent title = null;
	public IChatComponent hover = null;
	
	private TextureCoords texture = null;
	private ItemStack itemStack = null;
	
	public GuideLink(LinkType t, String l)
	{
		type = t;
		link = l;
	}
	
	@SideOnly(Side.CLIENT)
	public TextureCoords getTexture()
	{
		if(!type.isImage()) return null;
		if(texture != null) return texture;
		
		try
		{
			BufferedImage img = null;
			
			if(type == LinkType.IMAGE) img = ImageIO.read(new File(FTBLib.folderModpack, link));
			else if(type == LinkType.IMAGE_URL) img = ImageIO.read(new URL(link));
			
			ResourceLocation location = FTBLibClient.mc.getTextureManager().getDynamicTextureLocation("ftbu/" + link, new DynamicTexture(img));
			texture = new TextureCoords(location, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
		}
		catch(Exception e)
		{
			texture = TextureCoords.nullTexture;
		}
		
		return texture;
	}
	
	public ItemStack getItem()
	{
		if(type == LinkType.RECIPE)
		{
			itemStack = ItemStackTypeAdapter.parseItem(link);
			if(itemStack != null) title = new ChatComponentText(itemStack.getDisplayName());
			return itemStack;
		}
		
		return null;
	}
}