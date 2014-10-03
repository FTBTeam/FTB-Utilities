package latmod.core.mod.gui;

import net.minecraft.util.ResourceLocation;

public class TextureCoords
{
	public final ResourceLocation texture;
	public final int posX, posY;
	
	public TextureCoords(ResourceLocation res, int x, int y)
	{
		texture = res;
		posX = x;
		posY = y;
	}
	
	public void render(GuiLM gui, int x, int y, int w, int h)
	{
		gui.setTexture(texture);
		gui.drawTexturedModalRect(gui.getPosX() + x, gui.getPosY() + y, posX, posY, w, h);
	}
}