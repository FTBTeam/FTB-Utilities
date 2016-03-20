package ftb.utils.client.gui;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.utils.api.guide.GuidePage;
import net.minecraft.util.IChatComponent;

/**
 * Created by LatvianModder on 04.03.2016.
 */
public class GuideButtonCategory extends ButtonLM
{
	public final GuiGuide gui;
	public final GuidePage cat;
	
	public GuideButtonCategory(GuiGuide g, GuidePage c)
	{
		super(g, 0, g.panelCategories.height, g.panelWidth - 36, 13);
		gui = g;
		cat = c;
	}
	
	public void onButtonPressed(int b)
	{
		FTBLibClient.playClickSound();
		
		if(cat.childPages.isEmpty())
		{
			gui.selectedCategory = cat;
			gui.sliderText.value = 0F;
			gui.panelText.refreshWidgets();
		}
		else FTBLibClient.openGui(new GuiGuide(gui, cat));
	}
	
	public boolean isEnabled()
	{ return true; }
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		IChatComponent titleC = cat.getTitleComponent().createCopy();
		boolean mouseOver = mouseOver(ax, ay);
		if(mouseOver) titleC.getChatStyle().setUnderlined(true);
		if(gui.selectedCategory == cat) titleC.getChatStyle().setBold(true);
		gui.getFontRenderer().drawString(titleC.getFormattedText(), ax + 1, ay + 1, mouseOver ? GuiGuide.textColorOver : GuiGuide.textColor);
	}
}
