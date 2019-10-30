package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.events.FTBLibPreInitRegistryEvent;
import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.IConfigCallback;
import com.feed_the_beast.ftblib.lib.data.AdminPanelAction;
import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import com.feed_the_beast.ftbutilities.net.MessageViewCrashList;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesRegistryEventHandler
{
	@SubscribeEvent
	public static void onFTBLibPreInitRegistry(FTBLibPreInitRegistryEvent event)
	{
		FTBLibPreInitRegistryEvent.Registry registry = event.getRegistry();
		registry.registerServerReloadHandler(new ResourceLocation(FTBUtilities.MOD_ID, "ranks"), reloadEvent -> Ranks.INSTANCE.reload());
		registry.registerServerReloadHandler(new ResourceLocation(FTBUtilities.MOD_ID, "badges"), reloadEvent -> FTBUtilitiesUniverseData.clearBadgeCache());

		registry.registerSyncData(FTBUtilities.MOD_ID, new FTBUtilitiesSyncData());

		registry.registerAdminPanelAction(new AdminPanelAction(FTBUtilities.MOD_ID, "crash_reports", ItemIcon.getItemIcon(Blocks.BARRIER), 0)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.fromBoolean(player.hasPermission(FTBUtilitiesPermissions.CRASH_REPORTS_VIEW));
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data)
			{
				new MessageViewCrashList(new File(player.team.universe.server.getDataDirectory(), "crash-reports")).sendTo(player.getPlayer());
			}
		});

		registry.registerAdminPanelAction(new AdminPanelAction(FTBUtilities.MOD_ID, "edit_world", GuiIcons.GLOBE, 0)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.fromBoolean(player.hasPermission(FTBUtilitiesPermissions.EDIT_WORLD_GAMERULES));
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data)
			{
				ConfigGroup main = ConfigGroup.newGroup("edit_world");
				main.setDisplayName(new TextComponentTranslation("admin_panel.ftbutilities.edit_world"));

				if (player.hasPermission(FTBUtilitiesPermissions.EDIT_WORLD_GAMERULES))
				{
					ConfigGroup gamerules = main.getGroup("gamerules");
					gamerules.setDisplayName(new TextComponentTranslation("gamerules"));

					GameRules rules = player.team.universe.world.getGameRules();

					for (String key : rules.getRules())
					{
						switch (getType(rules, key))
						{
							case BOOLEAN_VALUE:
								gamerules.addBool(key, () -> rules.getBoolean(key), v -> rules.setOrCreateGameRule(key, Boolean.toString(v)), false).setDisplayName(new TextComponentString(StringUtils.camelCaseToWords(key)));
								break;
							case NUMERICAL_VALUE:
								gamerules.addInt(key, () -> rules.getInt(key), v -> rules.setOrCreateGameRule(key, Integer.toString(v)), 1, 1, Integer.MAX_VALUE).setDisplayName(new TextComponentString(StringUtils.camelCaseToWords(key)));
								break;
							default:
								gamerules.addString(key, () -> rules.getString(key), v -> rules.setOrCreateGameRule(key, v), "").setDisplayName(new TextComponentString(StringUtils.camelCaseToWords(key)));
						}
					}
				}

				FTBLibAPI.editServerConfig(player.getPlayer(), main, IConfigCallback.DEFAULT);
			}

			private GameRules.ValueType getType(GameRules gameRules, String key)
			{
				if (gameRules.areSameType(key, GameRules.ValueType.BOOLEAN_VALUE))
				{
					return GameRules.ValueType.BOOLEAN_VALUE;
				}
				else if (gameRules.areSameType(key, GameRules.ValueType.NUMERICAL_VALUE))
				{
					return GameRules.ValueType.NUMERICAL_VALUE;
				}
				else if (gameRules.areSameType(key, GameRules.ValueType.FUNCTION))
				{
					return GameRules.ValueType.FUNCTION;
				}

				return GameRules.ValueType.ANY_VALUE;
			}
		});
	}
}