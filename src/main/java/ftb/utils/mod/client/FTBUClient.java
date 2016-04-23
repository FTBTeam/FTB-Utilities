package ftb.utils.mod.client;

import com.google.gson.JsonElement;
import cpw.mods.fml.relauncher.*;
import ftb.lib.EventBusHelper;
import ftb.lib.api.GameModes;
import ftb.lib.api.config.*;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.utils.api.guide.repos.*;
import ftb.utils.badges.BadgeRenderer;
import ftb.utils.mod.*;
import ftb.utils.mod.cmd.CmdMath;
import ftb.utils.world.*;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	
	public static final ConfigEntryBool sort_friends_az = new ConfigEntryBool("sort_friends_az", false);
	public static final ConfigEntryBool loaded_chunks_space_key = new ConfigEntryBool("loaded_chunks_space_key", false);
	
	@Override
	public void preInit()
	{
		ClientConfigRegistry.addGroup("ftbu", FTBUClient.class);
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		FTBUActions.init();
	}
	
	@Override
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		FTBUClickAction.init();
		EventBusHelper.register(BadgeRenderer.instance);
		
		JsonElement guideRepo = GameModes.getGameModes().getCustomData("guide_repo");
		
		if(guideRepo.isJsonPrimitive())
		{
			try
			{
				GuideRepoList.refreshOnlineRepos();
				
				GuideOnlineRepo repo = GuideRepoList.onlineRepos.get(guideRepo.getAsString());
				if(repo.needsUpdate()) repo.download();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public LMWorld getClientWorldLM()
	{ return LMWorldClient.inst; }
}