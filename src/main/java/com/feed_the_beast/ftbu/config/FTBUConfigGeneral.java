package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@EventHandler
public class FTBUConfigGeneral
{
	public static final PropertyBool AUTO_RESTART = new PropertyBool(false);
	public static final PropertyDouble RESTART_TIMER = new PropertyDouble(12D, 0D, 720D);
	public static final PropertyBool SERVER_INFO_DIFFICULTY = new PropertyBool(true);
	public static final PropertyBool SERVER_INFO_ADMIN_QUICK_ACCESS = new PropertyBool(true);
	public static final PropertyInt MAX_LEADERBOARD_SIZE = new PropertyInt(250, 1, 1000);
	public static final PropertyBool DISABLE_IN_WALL_DAMAGE = new PropertyBool(false);
	public static final PropertyBool RANDOMIZE_CHAT_COLORS = new PropertyBool(true);

	@SubscribeEvent
	public static void init(RegisterConfigEvent event)
	{
		event.registerFile(FTBUFinals.MOD_ID, () -> new File(CommonUtils.folderLocal, "ftbu/config.json"));
		event.registerValue(PropertyChatSubstitute.ID, PropertyChatSubstitute::new);

		String id = FTBUFinals.MOD_ID + ".general";
		event.register(id, "auto_restart", AUTO_RESTART);
		event.register(id, "restart_timer", RESTART_TIMER);
		event.register(id, "max_leaderboard_size", MAX_LEADERBOARD_SIZE);
		event.register(id, "disable_in_wall_damage", DISABLE_IN_WALL_DAMAGE);
		id = FTBUFinals.MOD_ID + ".general.server_info";
		event.register(id, "difficulty", SERVER_INFO_DIFFICULTY);
		event.register(id, "admin_quick_access", SERVER_INFO_ADMIN_QUICK_ACCESS);
		id = FTBUFinals.MOD_ID + ".general.chat";
		event.register(id, "randomize_colors", RANDOMIZE_CHAT_COLORS);

		FTBUConfigBackups.init(event);
		FTBUConfigCommands.init(event);
		FTBUConfigLogin.init(event);
		FTBUConfigWebAPI.init(event);
		FTBUConfigWorld.init(event);
		FTBUConfigRanks.init(event);
	}
}