package latmod.core.gui;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class ButtonLM extends WidgetLM
{
	public int customID = 0;
	private long lastClickMillis = Minecraft.getSystemTime();
	public boolean doubleClickRequired = false;
	
	public ButtonLM(GuiLM g, int x, int y, int w, int h)
	{ super(g, x, y, w, h); }
	
	public boolean mousePressed(int mx, int my, int b)
	{
		if(mouseOver(mx, my))
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
}