package ftb.utils.mod.client.gui.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.FTBLibLang;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.api.guide.repos.*;
import latmod.lib.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
@SideOnly(Side.CLIENT)
public class ReposPage extends InfoPage
{
	public static boolean refreshedFirst = false;
	private static Thread thread;
	
	public ReposPage()
	{
		super("Online Guides");
		
		backgroundColor = new LMColor.RGB(30, 30, 30);
		textColor = new LMColor.RGB(20, 200, 255);
		useUnicodeFont = Boolean.FALSE;
		
		if(!refreshedFirst)
		{
			refreshedFirst = true;
			runRefreshThread();
		}
	}
	
	public ButtonLM createSpecialButton(final GuiInfo guiInfo)
	{
		ButtonLM button = new ButtonLM(guiInfo, 0, 0, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				clear();
				addSub(new InfoPage("Loading..."));
				gui.refreshWidgets();
				runRefreshThread();
			}
			
			public void renderWidget()
			{
				render(GuiIcons.refresh);
			}
		};
		
		button.title = EnumChatFormatting.GREEN + FTBLibLang.button_refresh.format();
		return button;
	}
	
	public void refreshGui(GuiInfo gui)
	{
		clear();
		
		List<GuideOnlineRepo> list = new ArrayList<>();
		list.addAll(GuideRepoList.repos.values());
		Collections.sort(list, LMStringUtils.ignoreCaseComparator);
		
		for(GuideOnlineRepo r : list)
		{
			addSub(new PageOnlineRepo(r));
		}
		
		thread = null;
	}
	
	public void runRefreshThread()
	{
		if(thread != null) return;
		
		thread = new Thread()
		{
			public void run()
			{
				GuideRepoList.refresh();
				
				if(Minecraft.getMinecraft().currentScreen instanceof GuiInfo)
					((GuiInfo) Minecraft.getMinecraft().currentScreen).refreshWidgets();
			}
		};
		
		thread.setDaemon(true);
		thread.start();
	}
}
