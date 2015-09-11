package latmod.ftbu.core.gui;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public abstract class ButtonLM extends WidgetLM
{
	public int customID = 0;
	private long lastClickMillis = Minecraft.getSystemTime();
	public boolean doubleClickRequired = false;
	public TextureCoords background = null;
	
	public ButtonLM(GuiLM g, int x, int y, int w, int h)
	{ super(g, x, y, w, h); }
	
	public void mousePressed(int b)
	{
		if(mouseOver())
		{
			if(doubleClickRequired)
			{
				long l = Minecraft.getSystemTime();
				if(l - lastClickMillis < 300)
					onButtonPressed(b);
				lastClickMillis = l;
			}
			
			else onButtonPressed(b);
		}
	}
	
	public abstract void onButtonPressed(int b);
	
	public void render(TextureCoords icon, double rw, double rh)
	{
		super.render(background, rw, rh);
		super.render(icon, rw, rh);
	}
}