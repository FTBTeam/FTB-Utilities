package ftb.utils.mod.client.gui.guide.repos;

import cpw.mods.fml.relauncher.*;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.api.guide.repos.GuideOnlineRepo;
import ftb.utils.mod.client.gui.guide.*;
import latmod.lib.LMStringUtils;
import net.minecraft.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
@SideOnly(Side.CLIENT)
public class PageOnlineRepo extends GuidePage
{
	public GuideOnlineRepo repo;
	
	public PageOnlineRepo(GuideOnlineRepo r)
	{
		super(r.getID());
		setTitle(new ChatComponentText(r.getInfo().name));
		repo = r;
	}
	
	public ButtonGuidePage createButton(GuiGuide g)
	{ return new ButtonGuidePage(g, this, repo.getIcon()); }
	
	@SideOnly(Side.CLIENT)
	public void refreshGui(GuiGuide gui)
	{
		clear();
		printlnText("Version: " + repo.getInfo().version);
		printlnText("Type: " + LMStringUtils.firstUppercase(repo.getInfo().type.name().toLowerCase()));
		printlnText("Authors: " + LMStringUtils.strip(repo.getInfo().authors));
		text.add(null);
		printlnText(EnumChatFormatting.GREEN + "Download");
	}
}