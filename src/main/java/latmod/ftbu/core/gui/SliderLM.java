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
	public boolean isVertical = false;
	public float scrollStep = 0.1F;
	
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
				if(isVertical)
					value = (float)(gui.mouseY - gui.getPosY(posY + (sliderSize / 2))) / (float)(height - sliderSize);
				else
					value = (float)(gui.mouseX - gui.getPosX(posX + (sliderSize / 2))) / (float)(width - sliderSize);
				value = MathHelperLM.clampFloat(value, 0F, 1F);
			}
			else isGrabbed = false;
		}
		
		if(gui.mouseDWheel != 0 && canMouseScroll())
		{
			value += (gui.mouseDWheel < 0) ? scrollStep : -scrollStep;
			value = MathHelperLM.clampFloat(value, 0F, 1F);
		}
		
		return v0 != value;
	}
	
	public boolean canMouseScroll()
	{ return mouseOver(); }
	
	public int getValueI()
	{ return (int)(value * ((isVertical ? height : width) - sliderSize)); }
	
	public void renderSlider(TextureCoords tc)
	{
		if(isVertical)
			tc.render(gui, posX, posY + getValueI(), width, sliderSize);
		else
			tc.render(gui, posX + getValueI(), posY, sliderSize, height);
	}
	
	public void mousePressed(int b)
	{
		if(mouseOver() && b == 0)
			isGrabbed = true;
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		if(displayMin == 0 && displayMax == 0) return;
		String s = "" + (int)MathHelperLM.map(value, 0D, 1D, displayMin, displayMax);
		if(title != null) s = title + ": " + s;
		l.add(s);
	}
}