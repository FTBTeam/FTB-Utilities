package latmod.ftbu.core.gui;
import latmod.ftbu.core.util.FastList;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class WidgetLM
{
	public final GuiLM gui;
	public int posX, posY, width, height;
	public PanelLM parentPanel = null;
	public String title = null;
	
	public WidgetLM(GuiLM g, int x, int y, int w, int h)
	{
		gui = g;
		posX = x;
		posY = y;
		width = w;
		height = h;
	}
	
	public boolean isEnabled()
	{ return true; }
	
	public int getAX()
	{ return (parentPanel == null) ? posX : (parentPanel.getAX() + posX); }
	
	public int getAY()
	{ return (parentPanel == null) ? posY : (parentPanel.getAY() + posY); }
	
	public boolean mouseOver()
	{
		int x = getAX();
		int y = getAY();
		return gui.mouseX >= x && gui.mouseY >= y
		&& gui.mouseX <= x + width && gui.mouseY <= y + height;
	}
	
	public void render(TextureCoords icon, double rw, double rh)
	{ if(icon != null) icon.render(gui, getAX(), getAY(), (int)(width * rw), (int)(height * rh)); }
	
	public void render(TextureCoords icon)
	{ render(icon, 1D, 1D); }
	
	public void mousePressed(int b)
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
	
	public void renderWidget()
	{
	}
}