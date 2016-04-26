package ftb.utils.mod.client;

import com.google.gson.JsonElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.EventBusHelper;
import ftb.lib.api.EventFTBSync;
import ftb.lib.api.GameModes;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.lib.mod.FTBLibMod;
import ftb.utils.api.EventLMWorldClient;
import ftb.utils.api.guide.repos.GuideOnlineRepo;
import ftb.utils.api.guide.repos.GuideRepoList;
import ftb.utils.badges.BadgeRenderer;
import ftb.utils.mod.FTBUCommon;
import ftb.utils.mod.FTBUGuiHandler;
import ftb.utils.mod.cmd.CmdMath;
import ftb.utils.world.LMWorld;
import ftb.utils.world.LMWorldClient;
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
	
	@Override
	public void syncData(EventFTBSync e)
	{
		LMWorldClient.inst = new LMWorldClient();
		LMWorldClient.inst.readDataFromNet(e.syncData.getCompoundTag("FTBU"), e.login);
		FTBLibMod.logger.info("Joined the server");
		new EventLMWorldClient(LMWorldClient.inst, false).post();
	}
}