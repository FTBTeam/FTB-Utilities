package ftb.utils.mod.client.gui.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.gui.*;
import ftb.lib.api.info.InfoPage;
import ftb.lib.api.info.lines.*;
import ftb.lib.mod.client.gui.info.*;
import ftb.utils.api.guide.repos.GuideOnlineRepo;
import latmod.lib.*;
import net.minecraft.util.*;

import java.io.File;

/**
 * Created by LatvianModder on 03.04.2016.
 */
@SideOnly(Side.CLIENT)
public class PageOnlineRepo extends InfoPage
{
	public GuideOnlineRepo repo;
	
	public PageOnlineRepo(GuideOnlineRepo r)
	{
		super(r.getID());
		setTitle(new ChatComponentText(r.getInfo().name));
		repo = r;
		
		getTitleComponent().getChatStyle().setColor(EnumChatFormatting.GREEN);
	}
	
	public ButtonInfoPage createButton(GuiInfo g)
	{ return new ButtonInfoPage(g, this, repo.getIcon()).setIconBlur(); }
	
	@SideOnly(Side.CLIENT)
	public void refreshGui(GuiInfo gui)
	{
		clear();
		text.add(new InfoImageLine(this).setImage(repo.getIcon()).setSize(64D, 64D));
		text.add(null);
		printlnText("Version: " + repo.getInfo().version);
		printlnText("Type: " + LMStringUtils.firstUppercase(repo.getInfo().type.name().toLowerCase()));
		printlnText("Authors: " + LMStringUtils.strip(repo.getInfo().authors));
		text.add(null);
		printlnText(EnumChatFormatting.GREEN + "Update Available!");
		text.add(null);
		text.add(new LineDownload(this));
	}
	
	public class LineDownload extends InfoTextLine
	{
		public LineDownload(InfoPage c)
		{ super(c, "download"); }
		
		public ButtonInfoTextLine createWidget(GuiInfo gui)
		{ return new ButtonDownloadLine(gui, this); }
	}
	
	public class ButtonDownloadLine extends ButtonInfoTextLine
	{
		private Thread thread;
		
		public ButtonDownloadLine(GuiInfo g, InfoTextLine l)
		{
			super(g, l);
			text = null;
			title = "Download";
			height = 18;
			width = g.getFontRenderer().getStringWidth(title) + 20;
		}
		
		public void onButtonPressed(int b)
		{
			if(thread != null) return;
			
			thread = new Thread()
			{
				public void run()
				{
					try
					{
						title = "Downloading...";
						
						File file = new File(FTBLib.folderLocal, "guidepacks/" + repo.getID());
						if(file.exists()) LMFileUtils.delete(file);
						file.mkdirs();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					
					thread = null;
				}
			};
			
			thread.setDaemon(true);
			thread.start();
		}
		
		public void renderWidget()
		{
			int ay = getAY();
			if(ay < -height || ay > guiInfo.mainPanel.height) return;
			int ax = getAX();
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			GuiLM.render(GuiIcons.down, ax + 1, ay + 1, gui.getZLevel(), 16, 16);
			
			boolean mouseOver = mouseOver();
			
			guiInfo.getFontRenderer().drawString(title, ax + 19, ay + 6, guiInfo.colorText);
			
			if(mouseOver)
			{
				GlStateManager.color(1F, 1F, 1F, 0.2F);
				GuiLM.drawBlankRect(ax, ay, guiInfo.getZLevel(), width, height);
			}
		}
	}
}