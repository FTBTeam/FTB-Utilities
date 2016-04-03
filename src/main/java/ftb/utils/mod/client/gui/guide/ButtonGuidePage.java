package ftb.utils.mod.client.gui.guide;

import ftb.lib.TextureCoords;
import ftb.lib.api.client.*;
import ftb.lib.api.gui.GuiLM;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.utils.api.guide.GuidePage;
import net.minecraft.util.*;

import java.util.List;

/**
 * Created by LatvianModder on 04.03.2016.
 */
public class ButtonGuidePage extends ButtonLM
{
	public final GuiGuide guiGuide;
	public final GuidePage page;
	public String hover;
	public TextureCoords icon;
	
	public ButtonGuidePage(GuiGuide g, GuidePage p, TextureCoords t)
	{
		super(g, 0, g.panelPages.height, g.panelWidth - 36, t == null ? 13 : 18);
		guiGuide = g;
		page = p;
		icon = t;
		updateTitle();
	}
	
	public void onButtonPressed(int b)
	{
		FTBLibClient.playClickSound();
		
		page.refreshGui(guiGuide);
		
		if(page.childPages.isEmpty())
		{
			guiGuide.selectedPage = page;
			guiGuide.sliderText.value = 0F;
			guiGuide.panelText.posY = 10;
			guiGuide.panelText.refreshWidgets();
		}
		else FTBLibClient.openGui(new GuiGuide(guiGuide, page));
	}
	
	public void updateTitle()
	{
		IChatComponent titleC = page.getTitleComponent().createCopy();
		if(guiGuide.selectedPage == page) titleC.getChatStyle().setBold(true);
		title = titleC.getFormattedText();
		hover = null;
		
		if(gui.getFontRenderer().getStringWidth(title) > width)
		{
			hover = title + "";
			title = gui.getFontRenderer().trimStringToWidth(title, width - 3) + "...";
		}
	}
	
	public void addMouseOverText(List<String> l)
	{
		if(hover != null) l.add(hover);
	}
	
	public void renderWidget()
	{
		int ay = getAY();
		if(ay < -height || ay > guiGuide.mainPanel.height) return;
		int ax = getAX();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		if(icon == null)
		{
			guiGuide.getFontRenderer().drawString(mouseOver(ax, ay) ? (EnumChatFormatting.UNDERLINE + title) : title, ax + 1, ay + 1, guiGuide.colorText);
		}
		else
		{
			GuiLM.render(icon, ax + 1, ay + 1, gui.getZLevel(), 16, 16);
			guiGuide.getFontRenderer().drawString(mouseOver(ax, ay) ? (EnumChatFormatting.UNDERLINE + title) : title, ax + 19, ay + 5, guiGuide.colorText);
		}
	}
}