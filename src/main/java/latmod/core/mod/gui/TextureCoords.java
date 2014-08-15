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
}