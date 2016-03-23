package ftb.utils.mod.client.gui.guide;

import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.utils.api.guide.lines.GuideTextLine;
import net.minecraft.util.IChatComponent;

import java.util.List;

/**
 * Created by LatvianModder on 04.03.2016.
 */
public class ButtonGuideTextLine extends ButtonLM
{
	public final GuiGuide guiGuide;
	public List<String> text;
	
	public ButtonGuideTextLine(GuiGuide g, GuideTextLine l)
	{
		super(g, 0, g.panelText.height, 0, 0);
		guiGuide = g;
		
		if(l != null)
		{
			IChatComponent c = l.getText();
			
			if(c != null)
			{
				text = guiGuide.getFontRenderer().listFormattedStringToWidth(c.getFormattedText(), g.panelText.width);
			}
		}
		
		if(text != null)
		{
			if(text.size() > 1) width = g.panelText.width;
			else width = g.getFontRenderer().getStringWidth(text.get(0));
			height = 10 * text.size();
		}
		else
		{
			width = 0;
			height = 11;
		}
	}
	
	public void addMouseOverText(List<String> l)
	{
	}
	
	public void onButtonPressed(int b)
	{
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		
		if(ay < -height || ay > guiGuide.mainPanel.height) return;
		boolean mouseOver = mouseOver();
		
		if(text != null && !text.isEmpty())
		{
			for(int i = 0; i < text.size(); i++)
			{
				guiGuide.getFontRenderer().drawString(text.get(i), ax, ay + i * 10, guiGuide.colorText);
			}
		}
		
		if(mouseOver)
		{
			GlStateManager.color(0F, 0F, 0F, 0.101F);
			GuiLM.drawBlankRect(ax, ay, guiGuide.getZLevel(), width, height);
		}
	}
}
