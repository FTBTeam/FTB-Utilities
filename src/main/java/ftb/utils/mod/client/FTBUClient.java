package ftb.utils.mod.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.EventBusHelper;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.utils.badges.BadgeRenderer;
import ftb.utils.mod.*;
import ftb.utils.mod.client.gui.guide.GuideClientSettings;
import ftb.utils.mod.cmd.CmdMath;
import ftb.utils.net.ClientAction;
import ftb.utils.world.*;
import latmod.lib.config.*;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	
	@Excluded
	public static final ConfigEntryBool render_my_badge = new ConfigEntryBool("render_my_badge", true)
	{
		public boolean get()
		{ return FTBLibClient.isIngameWithFTBU() && LMWorldClient.inst.clientPlayer.renderBadge; }
		
		public void set(boolean b)
		{
			if(FTBLibClient.isIngameWithFTBU()) ClientAction.BUTTON_RENDER_BADGE.send(b ? 1 : 0);
		}
	};
	
	@Excluded
	public static final ConfigEntryBool chat_links = new ConfigEntryBool("chat_links", true)
	{
		public boolean get()
		{ return FTBLibClient.isIngameWithFTBU() && LMWorldClient.inst.clientPlayer.getSettings().get(PersonalSettings.CHAT_LINKS); }
		
		public void set(boolean b)
		{
			if(FTBLibClient.isIngameWithFTBU()) ClientAction.BUTTON_CHAT_LINKS.send(b ? 1 : 0);
		}
	};
	
	public static final ConfigEntryBool sort_friends_az = new ConfigEntryBool("sort_friends_az", false);
	public static final ConfigEntryBool loaded_chunks_space_key = new ConfigEntryBool("loaded_chunks_space_key", false);
	
	@Hidden
	public static final ConfigEntryBool hide_armor_fg = new ConfigEntryBool("hide_armor_fg", false);
	
	public void preInit()
	{
		ClientConfigRegistry.addGroup("ftbu", FTBUClient.class);
		ClientConfigRegistry.addGroup("ftbu_guide", GuideClientSettings.class);
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		FTBUActions.init();
	}
	
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		FTBUClickAction.init();
		
		EventBusHelper.register(BadgeRenderer.instance);
	}
	
	public LMWorld getClientWorldLM()
	{ return LMWorldClient.inst; }
}