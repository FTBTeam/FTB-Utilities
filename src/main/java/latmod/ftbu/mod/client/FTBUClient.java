package latmod.ftbu.mod.client;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.lib.client.FTBLibClient;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.client.*;
import latmod.ftbu.badges.ThreadLoadBadges;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.claims.ClaimedAreasClient;
import latmod.ftbu.mod.cmd.CmdMath;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.util.client.*;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ClientConfig clientConfig = new ClientConfig("ftbu");
	public static final ClientConfigProperty renderBadges = new ClientConfigProperty("player_decorators", true);
	
	public static final ClientConfigProperty renderMyBadge = new ClientConfigProperty("player_decorators_self", true)
	{
		public void initGui()
		{ setValue(LMWorldClient.inst.getClientPlayer().settings.renderBadge ? 1 : 0); }
		
		public void onClicked()
		{ ClientAction.ACTION_RENDER_BADGE.send(LMWorldClient.inst.getClientPlayer().settings.renderBadge ? 0 : 1); }
	};
	
	public static final ClientConfigProperty addOreNames = new ClientConfigProperty("item_ore_names", false);
	public static final ClientConfigProperty addRegistryNames = new ClientConfigProperty("item_reg_names", false);
	public static final ClientConfigProperty displayDebugInfo = new ClientConfigProperty("debug_info", false);
	public static final ClientConfigProperty optionsButton = new ClientConfigProperty("options_button", true);
	
	public static final ClientConfigProperty chatLinks = new ClientConfigProperty("chat_links", true)
	{
		public void initGui()
		{ setValue(LMWorldClient.inst.getClientPlayer().settings.chatLinks ? 1 : 0); }
		
		public void onClicked()
		{ ClientAction.ACTION_CHAT_LINKS.send(LMWorldClient.inst.getClientPlayer().settings.chatLinks ? 0 : 1); }
	};
	
	public static final ClientConfigProperty playerOptionsShortcut = new ClientConfigProperty("player_options_shortcut", false);
	public static final ClientConfigProperty sortFriendsAZ = new ClientConfigProperty("sort_friends_az", false);
	
	public static final ClientConfig miscConfig = new ClientConfig("ftbu_misc").setHidden();
	public static final ClientConfigProperty hideArmorFG = new ClientConfigProperty("hide_armor_fg", false);
	public static final ClientConfigProperty openHSB = new ClientConfigProperty("openHSB_cg", false);
	
	private static void initConfig()
	{
		if(FTBLibFinals.DEV) clientConfig.add(displayDebugInfo);
		else displayDebugInfo.setValue(0);
		
		clientConfig.add(renderBadges);
		clientConfig.add(renderMyBadge);
		clientConfig.add(addOreNames);
		clientConfig.add(addRegistryNames);
		clientConfig.add(optionsButton);
		clientConfig.add(chatLinks);
		clientConfig.add(playerOptionsShortcut);
		clientConfig.add(sortFriendsAZ);
		ClientConfigRegistry.add(clientConfig);
		
		miscConfig.add(hideArmorFG);
		miscConfig.add(openHSB);
		ClientConfigRegistry.add(miscConfig);
		
		FTBUGuiEventHandler.init();
		
		ClientCommandHandler.instance.registerCommand(new CmdMath());
	}
	
	public static void onWorldJoined()
	{
		ThreadLoadBadges.init();
		ClientNotifications.init();
	}
	
	public static void onWorldClosed()
	{
		ClientNotifications.init();
		ClaimedAreasClient.clear();
	}
	
	public void preInit()
	{
		JsonHelper.initClient();
		EventBusHelper.register(FTBUClientEventHandler.instance);
		EventBusHelper.register(FTBURenderHandler.instance);
		EventBusHelper.register(FTBUGuiEventHandler.instance);
		
		ClientConfigRegistry.init();
		initConfig();
		
		FTBUBadgeRenderer.instance.enable(true);
	}
	
	public void postInit()
	{
		ClientConfigRegistry.load();
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
	}
	
	public LMWorld getClientWorldLM()
	{ return LMWorldClient.inst; }
	
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p)
	{
		NBTTagCompound data = p.func_148857_g();
		t.readTileData(data);
		t.readTileClientData(data);
		t.onUpdatePacket();
		LatCoreMCClient.onGuiClientAction();
	}
	
	public static void onReloaded()
	{
		FTBLibClient.clearCachedData();
		ThreadLoadBadges.init();
		
		if(LMWorldClient.inst != null)
		{
			for(int i = 0; i < LMWorldClient.inst.players.size(); i++)
				LMWorldClient.inst.players.get(i).toPlayerSP().onReloaded();
		}
	}
}