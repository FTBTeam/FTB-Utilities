package ftb.utils.client.gui;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.ButtonLM;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

/**
 * Created by LatvianModder on 04.03.2016.
 */
public class GuideButtonTextLine extends ButtonLM
{
	public final GuiGuide gui;
	public GuideTextLine line = null;
	
	public GuideButtonTextLine(GuiGuide g, int i)
	{
		super(g, 0, i * 11, g.textPanel.width, 11);
		gui = g;
	}
	
	public void addMouseOverText(List<String> l)
	{
		if(line != null && line.special != null) line.special.addHoverText(l);
	}
	
	public void onButtonPressed(int b)
	{
		if(line != null && line.special != null) line.special.onClicked(gui);
	}
	
	public void renderWidget()
	{
		if(line == null) return;
		
		int ax = getAX();
		int ay = getAY();
		
		if(!line.text.isEmpty()) GuiGuide.guideFont.drawString(line.text, ax, ay, GuiGuide.textColor);
		else if(line.special != null && line.special.type.isImage() && line.texture != null && line.texture.isValid())
		{
			GlStateManager.color(1F, 1F, 1F, 1F);
			FTBLibClient.setTexture(line.texture.texture);
			double w = Math.min(width, line.texture.width);
			GuiLM.render(line.texture, ax, ay, gui.getZLevel(), w, line.texture.getHeight(w) + 1);
			//GuiLM.drawTexturedRectD(ax, ay, gui.getZLevel(), w, line.texture.getHeight(w), 0D, line.texture.minU, 1D, line.texture.maxU);
		}
	}
}
