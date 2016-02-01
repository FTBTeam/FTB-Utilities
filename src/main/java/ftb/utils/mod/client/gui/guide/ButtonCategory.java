package ftb.utils.mod.client.gui.guide;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.utils.api.guide.GuideCategory;
import net.minecraft.util.IChatComponent;

/**
 * Created by LatvianModder on 01.02.2016.
 */
public class ButtonCategory extends ButtonLM
{
	public final GuiGuide gui;
	public final GuideCategory cat;
	
	public ButtonCategory(GuiGuide g, int x, int y, int w, int h, GuideCategory c)
	{
		super(g, x, y, w, h);
		gui = g;
		cat = c;
	}
	
	public void onButtonPressed(int b)
	{
		FTBLibClient.playClickSound();
		
		if(cat.subcategories.isEmpty())
		{
			gui.selectedCategory = cat;
			gui.sliderText.value = 0F;
			gui.initLMGui();
		}
		else FTBLibClient.openGui(new GuiGuide(gui, cat));
	}
	
	public boolean isEnabled()
	{ return true; }
	
	public void renderWidget()
	{
		if(!isEnabled()) return;
		int ax = getAX();
		int ay = getAY();
		IChatComponent titleC = cat.getTitleComponent().createCopy();
		boolean mouseOver = mouseOver(ax, ay);
		if(mouseOver) titleC.getChatStyle().setUnderlined(true);
		if(gui.selectedCategory == cat) titleC.getChatStyle().setBold(true);
		gui.getFontRenderer().drawString(titleC.getFormattedText(), ax + 1, ay + 1, mouseOver ? GuiGuide.textColorOver : GuiGuide.textColor);
	}
}
