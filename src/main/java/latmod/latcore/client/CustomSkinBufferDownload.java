package latmod.latcore.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.IImageBuffer;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CustomSkinBufferDownload implements IImageBuffer
{
	public BufferedImage image;

	public BufferedImage parseUserSkin(BufferedImage img)
	{
		image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.drawImage(img, 0, 0, null);
		graphics.dispose();
		//imageData = ((DataBufferInt)img1.getRaster().getDataBuffer()).getData();
		//setAreaOpaque(0, 0, w / 2, h / 2);
		//setAreaTransparent(w / 2, 0, w, h);
		//setAreaOpaque(0, h / 2, w, h);
		return image;
	}
	
	public void func_152634_a() {}
	
	public void setAreaTransparent(int x0, int y0, int x1, int y1)
	{
		if (!hasTransparency(x0, y0, x1, y1))
			for (int x = x0; x < x1; ++x) for (int y = y0; y < y1; ++y)
			image.setRGB(x, y, image.getRGB(x, y) & 16777215);
	}
	
	public void setAreaOpaque(int x0, int y0, int x1, int y1)
	{
		for (int x = x0; x < x1; ++x) for (int y = y0; y < y1; ++y)
			image.setRGB(x, y, image.getRGB(x, y) | -16777216);
	}
	
	public boolean hasTransparency(int x0, int y0, int x1, int y1)
	{
		for (int x = x0; x < x1; ++x) for (int y = y0; y < y1; ++y)
		if ((image.getRGB(x, y) >> 24 & 255) < 128) return true;
		return false;
	}
}