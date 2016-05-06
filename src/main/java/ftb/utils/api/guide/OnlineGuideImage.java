package ftb.utils.api.guide;

import ftb.lib.FTBLib;
import ftb.lib.TextureCoords;
import ftb.lib.api.client.FTBLibClient;
import latmod.lib.util.FinalIDObject;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by LatvianModder on 06.05.2016.
 */
public class OnlineGuideImage extends FinalIDObject
{
	public static final ResourceLocation DEFAULT_IMAGE = new ResourceLocation("ftbu", "textures/gui/");
	
	public final String imageURL;
	private TextureCoords image;
	
	public OnlineGuideImage(String id, String url)
	{
		super(id);
		imageURL = url;
	}
	
	@SideOnly(Side.CLIENT)
	public TextureCoords getImage()
	{
		if(image == TextureCoords.nullTexture) { return null; }
		else if(image != null) { return image; }
		else if(imageURL == null) { return null; }
		
		image = TextureCoords.nullTexture;
		
		try
		{
			File file = new File(FTBLib.folderModpack, "images/" + imageURL);
			if(FTBLib.DEV_ENV) { FTBLib.dev_logger.info("Loading Guide image: " + file.getAbsolutePath()); }
			BufferedImage img = ImageIO.read(file);
			ResourceLocation tex = FTBLibClient.mc.getTextureManager().getDynamicTextureLocation("ftbu_guide/" + imageURL, new DynamicTexture(img));
			image = new TextureCoords(tex, 0D, 0D, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return image;
	}
}