package ftb.utils.mod.client.gui.guide;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.utils.api.guide.GuidePage;
import net.minecraft.util.IChatComponent;

/**
 * Created by LatvianModder on 04.03.2016.
 */
public class GuideButtonCategory extends ButtonLM
{
	public final GuiGuide guiGuide;
	public final GuidePage page;
	
	public GuideButtonCategory(GuiGuide g, GuidePage p)
	{
		super(g, 0, g.panelPages.height, g.panelWidth - 36, 13);
		guiGuide = g;
		page = p;
	}
	
	public void onButtonPressed(int b)
	{
		FTBLibClient.playClickSound();
		
		page.onPageClicked();
		
		if(page.childPages.isEmpty())
		{
			guiGuide.selectedPage = page;
			guiGuide.sliderText.value = 0F;
			guiGuide.panelText.posY = 10;
			guiGuide.panelText.refreshWidgets();
		}
		else FTBLibClient.openGui(new GuiGuide(guiGuide, page));
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		IChatComponent titleC = page.getTitleComponent().createCopy();
		boolean mouseOver = mouseOver(ax, ay);
		if(mouseOver) titleC.getChatStyle().setUnderlined(true);
		if(guiGuide.selectedPage == page) titleC.getChatStyle().setBold(true);
		guiGuide.getFontRenderer().drawString(titleC.getFormattedText(), ax + 1, ay + 1, guiGuide.colorText);
	}
}
