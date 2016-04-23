package ftb.utils.mod.client.gui.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.TextureCoords;
import ftb.lib.api.LangKey;
import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.gui.*;
import ftb.lib.api.info.InfoPage;
import ftb.lib.api.info.lines.*;
import ftb.lib.mod.client.gui.info.*;
import ftb.utils.api.guide.repos.GuideOnlineRepo;
import latmod.lib.LMStringUtils;
import net.minecraft.util.*;

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
	
	@Override
	public ButtonInfoPage createButton(GuiInfo g)
	{ return new ButtonInfoPage(g, this, new TextureCoords(repo.getIcon(), 0, 0, 16, 16, 16, 16)).setIconBlur(); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void refreshGui(GuiInfo gui)
	{
		clear();
		text.add(new InfoImageLine(this).setImage(new TextureCoords(repo.getIcon(), 0, 0, 16, 16, 16, 16)).setSize(64D, 64D));
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
		
		@Override
		public ButtonInfoTextLine createWidget(GuiInfo gui)
		{ return new ButtonDownloadLine(gui, this); }
	}
	
	public class ButtonDownloadLine extends ButtonInfoTextLine
	{
		private Thread thread;
		public PackState state;
		
		public ButtonDownloadLine(GuiInfo g, InfoTextLine l)
		{
			super(g, l);
			text = null;
			height = 18;
			setState(PackState.DOWNLOAD);
		}
		
		private void setState(PackState s)
		{
			state = s;
			title = state.lang.format();
			width = gui.getFontRenderer().getStringWidth(title) + 20;
		}
		
		@Override
		public void onButtonPressed(int b)
		{
			if(thread != null) return;
			
			thread = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						setState(PackState.DOWNLOADING);
						repo.download();
						setState(PackState.UPDATE);
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
		
		@Override
		public void renderWidget()
		{
			int ay = getAY();
			int ax = getAX();
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			GuiLM.render(state.icon, ax + 1, ay + 1, gui.getZLevel(), 16, 16);
			
			boolean mouseOver = mouseOver();
			
			guiInfo.getFontRenderer().drawString(title, ax + 19, ay + 6, guiInfo.colorText);
			
			if(mouseOver)
			{
				GlStateManager.color(1F, 1F, 1F, 0.2F);
				GuiLM.drawBlankRect(ax, ay, guiInfo.getZLevel(), width, height);
			}
		}
	}
	
	public enum PackState
	{
		DOWNLOAD("download", GuiIcons.add),
		DOWNLOADING("downloading", GuiIcons.add),
		REMOVE("remove", GuiIcons.remove),
		UPDATE("update", GuiIcons.down);
		
		public final LangKey lang;
		public final TextureCoords icon;
		
		PackState(String s, TextureCoords t)
		{
			lang = new LangKey("ftbu.pack_state." + s);
			icon = t;
		}
	}
}