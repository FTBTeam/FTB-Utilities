package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.EnumReloadType;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibClientRegistry;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.client.FTBUActions;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.cmd.FTBUCommands;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigCommands;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.config.PropertyChatSubstituteList;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import com.feed_the_beast.ftbu.world.ServerInfoFile;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.command.CommandTreeBase;

import java.io.File;
import java.util.Collections;

/**
 * Created by LatvianModder on 20.09.2016.
 */
public enum FTBLibIntegration implements IFTBLibPlugin
{
    @FTBLibPlugin
    INSTANCE;

    public static FTBLibAPI API;
    public static final ResourceLocation FTBU_DATA = FTBUFinals.get("data");

    @Override
    public void init(FTBLibAPI api)
    {
        API = api;
    }

    @Override
    public void onReload(Side side, ICommandSender sender, EnumReloadType type)
    {
        if(side.isServer())
        {
            ServerInfoFile.CachedInfo.reload();
            Ranks.INSTANCE.reload();

            FTBUUniverseData.reloadServerBadges();
            LoadedChunkStorage.INSTANCE.checkAll();
        }
        else
        {
            FTBU.PROXY.onReloadedClient();
        }
    }

    @Override
    public void registerCommon(IFTBLibRegistry reg)
    {
        reg.addOptionalServerMod(FTBUFinals.MOD_ID);
        reg.addConfigFileProvider(FTBUFinals.MOD_ID, () -> new File(LMUtils.folderLocal, "ftbu/config.json"));
        reg.addConfigValueProvider(PropertyChatSubstituteList.ID, () -> new PropertyChatSubstituteList(Collections.emptyMap()));

        FTBUConfigBackups.init(reg);
        FTBUConfigCommands.init(reg);
        FTBUConfigGeneral.init(reg);
        FTBUConfigLogin.init(reg);
        FTBUConfigWebAPI.init(reg);
        FTBUConfigWorld.init(reg);
        FTBUConfigRanks.init(reg);

        reg.addNotification(FTBUNotifications.NO_TEAM);
        reg.addNotification(FTBUNotifications.CANT_MODIFY_CHUNK);
        reg.addNotification(FTBUNotifications.CLAIMING_NOT_ENABLED);
        reg.addNotification(FTBUNotifications.CLAIMING_NOT_ALLOWED);
        reg.addNotification(FTBUNotifications.UNCLAIMED_ALL);
        reg.addNotification(FTBUNotifications.CHUNK_CLAIMED);
        reg.addNotification(FTBUNotifications.CHUNK_UNCLAIMED);
        reg.addNotification(FTBUNotifications.CHUNK_LOADED);
        reg.addNotification(FTBUNotifications.CHUNK_UNLOADED);
        reg.addNotification(FTBUNotifications.WILDERNESS);

        reg.addUniverseDataProvider(FTBU_DATA, owner -> new FTBUUniverseData());
        reg.addPlayerDataProvider(FTBU_DATA, owner -> new FTBUPlayerData());
        reg.addTeamDataProvider(FTBU_DATA, owner -> new FTBUTeamData());
    }

    @Override
    public void registerClient(IFTBLibClientRegistry reg)
    {
        reg.addClientConfig(FTBLibFinals.MOD_ID, "render_badges", FTBUClientConfig.RENDER_BADGES);
        reg.addClientConfig(FTBLibFinals.MOD_ID, "light_value_texture_x", FTBUClientConfig.LIGHT_VALUE_TEXTURE_X);
        reg.addClientConfig(FTBLibFinals.MOD_ID, "journeymap_overlay", FTBUClientConfig.JOURNEYMAP_OVERLAY);

        reg.addSidebarButton(FTBUFinals.get("guide"), FTBUActions.GUIDE);
        reg.addSidebarButton(FTBUFinals.get("server_info"), FTBUActions.SERVER_INFO);
        reg.addSidebarButton(FTBUFinals.get("claimed_chunks"), FTBUActions.CLAIMED_CHUNKS);
        reg.addSidebarButton(FTBUFinals.get("trash_can"), FTBUActions.TRASH_CAN);
        reg.addSidebarButton(FTBUFinals.get("shop"), FTBUActions.SHOP);
        reg.addSidebarButton(FTBUFinals.get("heal"), FTBUActions.HEAL);
        reg.addSidebarButton(FTBUFinals.get("toggle.gamemode"), FTBUActions.TOGGLE_GAMEMODE);
        reg.addSidebarButton(FTBUFinals.get("toggle.rain"), FTBUActions.TOGGLE_RAIN);
        reg.addSidebarButton(FTBUFinals.get("toggle.day"), FTBUActions.TOGGLE_DAY);
        reg.addSidebarButton(FTBUFinals.get("toggle.night"), FTBUActions.TOGGLE_NIGHT);
    }

    @Override
    public void registerFTBCommands(CommandTreeBase command, boolean dedi)
    {
        FTBUCommands.register(command, dedi);
    }
}