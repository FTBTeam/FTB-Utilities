package latmod.ftbu.core.gui;

import latmod.ftbu.core.util.*;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class SliderLM extends WidgetLM
{
	public boolean isGrabbed;
	public float value;
	public final int sliderSize;
	public int displayMin = 0;
	public int displayMax = 100;
	
	public SliderLM(GuiLM g, int x, int y, int w, int h, int ss)
	{
		super(g, x, y, w, h);
		sliderSize = ss;
	}
	
	public boolean update()
	{
		float v0 = value;
		
		if(isGrabbed)
		{
			if(Mouse.isButtonDown(0))
			{
				value = (float)(gui.mouseX - (gui.getPosX() + posX + (sliderSize / 2))) / (float)(width - sliderSize);
				value = MathHelperLM.clampFloat(value, 0F, 1F);
			}
			else isGrabbed = false;
		}
		
		return v0 != value;
	}
	
	public int getValueI()
	{ return (int)(value * (width - sliderSize)); }
	
	public void renderSlider(TextureCoords tc)
	{ tc.render(gui, posX + getValueI(), posY, sliderSize, height); }
	
	public void mousePressed(int b)
	{
		if(mouseOver() && b == 0)
		{
			isGrabbed = true;
		}
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		String s = "" + (int)MathHelperLM.map(value, 0D, 1D, displayMin, displayMax);
		if(title != null) s = title + ": " + s;
		l.add(s);
	}
}