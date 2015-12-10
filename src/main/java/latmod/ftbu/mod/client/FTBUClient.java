package latmod.ftbu.mod.client;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.lib.client.FTBLibClient;
import ftb.lib.mod.client.FTBLibGuiEventHandler;
import ftb.lib.notification.ClientNotifications;
import latmod.ftbu.badges.ThreadLoadBadges;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.claims.ClaimedAreasClient;
import latmod.ftbu.mod.cmd.CmdMath;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.config.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigGroup client_config = new ConfigGroup("ftbu");
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	
	public static final ConfigEntryBool render_my_badge = new ConfigEntryBool("render_my_badge", true)
	{
		public boolean get()
		{ return LMWorldClient.inst.getClientPlayer().settings.renderBadge; }
		
		public void set(boolean b)
		{ ClientAction.ACTION_RENDER_BADGE.send(b ? 1 : 0); }
	};
	
	public static final ConfigEntryBool chat_links = new ConfigEntryBool("chat_links", true)
	{
		public boolean get()
		{ return LMWorldClient.inst.getClientPlayer().settings.chatLinks; }
		
		public void set(boolean b)
		{ ClientAction.ACTION_CHAT_LINKS.send(b ? 1 : 0); }
	};
	
	public static final ConfigEntryBool player_options_shortcut = new ConfigEntryBool("player_options_shortcut", false);
	public static final ConfigEntryBool sort_friends_az = new ConfigEntryBool("sort_friends_az", false);
	public static final ConfigEntryBool hide_armor_fg = new ConfigEntryBool("hide_armor_fg", false).setHidden();
	
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
		EventBusHelper.register(FTBUBadgeRenderer.instance);
		
		client_config.add(render_badges);
		client_config.add(render_my_badge.setExcluded());
		client_config.add(chat_links.setExcluded());
		client_config.add(player_options_shortcut);
		client_config.add(sort_friends_az);
		client_config.add(hide_armor_fg);
		ClientConfigRegistry.add(client_config);
		
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		
		FTBLibGuiEventHandler.sidebar_buttons_config.addAll(FTBUGuiEventHandler.class);
	}
	
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		FTBUClickAction.init();
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