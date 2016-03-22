package ftb.utils.api.guide;

import ftb.lib.TextureCoords;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 22.03.2016.
 */
public final class GuideImage
{
	public ResourceLocation texture;
	public int width, height;
	
	public double displayWidth, displayHeight;
	public double displayScale;
	
	public double getDisplayWidth()
	{
		if(displayWidth > 0) return displayWidth;
		else if(displayScale != 0D)
		{
			return (displayScale > 0D ? width * displayScale : ((double) width / -displayScale));
		}
		
		return width;
	}
	
	public double getDisplayHeight()
	{
		if(displayHeight > 0) return displayHeight;
		else if(displayScale != 0D)
		{
			return (displayScale > 0D ? height * displayScale : ((double) height / -displayScale));
		}
		
		return height;
	}
	
	public TextureCoords createDisplay()
	{
		if(texture == null) return null;
		double w = getDisplayWidth();
		double h = getDisplayHeight();
		if(w <= 0D || h <= 0D) return null;
		return new TextureCoords(texture, 0, 0, w, h, w, h);
	}
	
	public TextureCoords createAcual()
	{
		if(texture == null) return null;
		if(width <= 0D || height <= 0D) return null;
		return new TextureCoords(texture, 0, 0, width, height, width, height);
	}
}
