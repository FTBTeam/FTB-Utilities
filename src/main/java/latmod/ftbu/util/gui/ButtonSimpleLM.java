package latmod.ftbu.util.gui;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.FastList;

@SideOnly(Side.CLIENT)
public abstract class ButtonSimpleLM extends ButtonLM
{
	public int colorText = 0xFFFFFFFF;
	public int colorButton = 0xFF888888;
	public int colorButtonOver = 0xFF999999;
	
	public ButtonSimpleLM(GuiLM g, int x, int y, int w, int h)
	{ super(g, x, y, w, h); }
	
	public void addMouseOverText(FastList<String> l)
	{
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height, mouseOver(ax, ay) ? colorButtonOver : colorButton);
		gui.drawCenteredString(gui.getFontRenderer(), title, ax + width / 2, ay + (height - gui.getFontRenderer().FONT_HEIGHT) / 2, colorText);
	}
}