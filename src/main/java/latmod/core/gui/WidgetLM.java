package latmod.core.gui;
import latmod.core.util.FastList;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class WidgetLM
{
	public final GuiLM gui;
	public int posX, posY, width, height;
	public String title = null;
	
	public WidgetLM(GuiLM g, int x, int y, int w, int h)
	{
		gui = g;
		posX = x;
		posY = y;
		width = w;
		height = h;
	}
	
	public boolean isAt(int x, int y)
	{ return x >= posX && y >= posY && x < posX + width && y < posY + height; }
	
	public boolean mouseOver(int mx, int my)
	{ return isAt(mx - gui.getPosX(), my - gui.getPosY()); }
	
	public void render(Object icon, double rw, double rh)
	{
		if(icon == null) return;
		if(icon instanceof IIcon)
		{
			gui.setTexture(TextureMap.locationItemsTexture);
			gui.drawTexturedModelRectFromIcon(posX + gui.getPosX(), posY + gui.getPosY(), (IIcon)icon, (int)(width * rw), (int)(height * rh));
		}
		else if(icon instanceof TextureCoords)
			((TextureCoords)icon).render(gui, posX, posY, (int)(width * rw), (int)(height * rh));
	}
	
	public void render(Object icon)
	{ render(icon, 1D, 1D); }
	
	public boolean mousePressed(int mx, int my, int b)
	{
		return false;
	}
	
	public void voidMousePressed(int mx, int my, int b)
	{
	}
	
	public boolean keyPressed(int key, char keyChar)
	{
		return false;
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		if(title != null) l.add(title);
	}
}