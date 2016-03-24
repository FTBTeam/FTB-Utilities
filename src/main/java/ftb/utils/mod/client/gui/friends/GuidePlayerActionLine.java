package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.client.*;
import ftb.lib.api.gui.GuiLM;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.api.guide.lines.GuideTextLine;
import ftb.utils.mod.client.gui.guide.*;
import ftb.utils.world.*;

import java.util.List;

/**
 * Created by LatvianModder on 23.03.2016.
 */
@SideOnly(Side.CLIENT)
public class GuidePlayerActionLine extends GuideTextLine
{
	public final LMPlayerClient playerLM;
	public final PlayerAction action;
	
	public GuidePlayerActionLine(GuidePage c, LMPlayerClient p, PlayerAction a)
	{
		super(c, null);
		playerLM = p;
		action = a;
	}
	
	@SideOnly(Side.CLIENT)
	public ButtonGuideTextLine createWidget(GuiGuide gui)
	{ return new ButtonGuidePlayerAction(gui, this); }
	
	public class ButtonGuidePlayerAction extends ButtonGuideTextLine
	{
		public ButtonGuidePlayerAction(GuiGuide g, GuidePlayerActionLine w)
		{
			super(g, null);
			height = 18;
			title = action.getDisplayName();
			width = (action.icon == null ? 8 : 24) + g.getFontRenderer().getStringWidth(title);
		}
		
		public void addMouseOverText(List<String> l)
		{
		}
		
		public void onButtonPressed(int b)
		{
			FTBLibClient.playClickSound();
			action.onClicked(LMWorldClient.inst.clientPlayer, playerLM);
		}
		
		public void renderWidget()
		{
			int ay = getAY();
			if(ay < -height || ay > guiGuide.mainPanel.height) return;
			int ax = getAX();
			float z = gui.getZLevel();
			
			if(mouseOver())
			{
				GlStateManager.color(1F, 1F, 1F, 0.2F);
				GuiLM.drawBlankRect(ax, ay, z, width, height);
			}
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			
			if(action.icon != null) action.render(ax + 1, ay + 1, z);
			
			gui.getFontRenderer().drawString(title, ax + (action.icon == null ? 4 : 20), ay + 5, guiGuide.colorText);
		}
	}
}
