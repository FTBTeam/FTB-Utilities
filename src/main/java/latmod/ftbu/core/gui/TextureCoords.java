package latmod.ftbu.core.gui;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

public final class TextureCoords
{
	public final ResourceLocation texture;
	public final int posX, posY, width, height;
	public final int textureW, textureH;
	
	public TextureCoords(ResourceLocation res, int x, int y, int w, int h, int tw, int th)
	{
		texture = res;
		posX = x;
		posY = y;
		width = w;
		height = h;
		textureW = tw;
		textureH = th;
	}
	
	public TextureCoords(ResourceLocation res, int x, int y, int w, int h)
	{ this(res, x, y, w, h, 256, 256); }
	
	public TextureCoords(ResourceLocation res, int index)
	{ this(res, (index % 16) * 16, (index / 16) * 16, 16, 16, 256, 256); }
	
	@SideOnly(Side.CLIENT)
	public void render(GuiLM gui, int x, int y, int w, int h)
	{
		gui.setTexture(texture);
		GuiLM.drawTexturedRect(x + gui.getPosX(), y + gui.getPosY(), gui.getZLevel(), w, h, posX, posY, posX + width, posY + height, textureW, textureH);
	}
	
	@SideOnly(Side.CLIENT)
	public void render(GuiLM gui, int x, int y)
	{ render(gui, x, y, width, height); }
}