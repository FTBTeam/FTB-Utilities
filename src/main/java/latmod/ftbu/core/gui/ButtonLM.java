package latmod.ftbu.core.gui;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class ButtonLM extends WidgetLM
{
	public int customID = 0;
	private long lastClickMillis = Minecraft.getSystemTime();
	public boolean doubleClickRequired = false;
	public TextureCoords background = null;
	
	public ButtonLM(GuiLM g, int x, int y, int w, int h)
	{ super(g, x, y, w, h); }
	
	public boolean mousePressed(int mx, int my, int b)
	{
		if(mouseOver(mx, my) && isEnabled())
		{
			if(doubleClickRequired)
			{
				if(Minecraft.getSystemTime() - lastClickMillis < 300)
					onButtonDoublePressed(b);
				lastClickMillis = Minecraft.getSystemTime();
			}
			else onButtonPressed(b);
			return true;
		}
		
		return false;
	}
	
	public abstract void onButtonPressed(int b);
	
	public void onButtonDoublePressed(int b)
	{
	}
	
	public boolean isEnabled()
	{ return true; }
	
	public void render(TextureCoords icon, double rw, double rh)
	{
		super.render(background, rw, rh);
		super.render(icon, rw, rh);
	}
}