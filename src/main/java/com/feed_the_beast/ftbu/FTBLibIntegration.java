package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.EnumReloadType;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibClientRegistry;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
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
import com.feed_the_beast.ftbu.config.PropertyChatSubstitute;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;

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
    public void loadWorldData(MinecraftServer server)
    {
        Ranks.reload();
    }

    @Override
    public void onReload(Side side, ICommandSender sender, EnumReloadType type)
    {
        if(side.isServer())
        {
            if(type.command())
            {
                Ranks.reload();
            }

            ServerInfoPage.reloadCachedInfo();
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
        reg.addConfigValueProvider(PropertyChatSubstitute.ID, PropertyChatSubstitute::new);

        FTBUConfigBackups.init(reg);
        FTBUConfigCommands.init(reg);
        FTBUConfigGeneral.init(reg);
        FTBUConfigLogin.init(reg);
        FTBUConfigWebAPI.init(reg);
        FTBUConfigWorld.init(reg);
        FTBUConfigRanks.init(reg);

        FTBUNotifications.init(reg);

        reg.addUniverseDataProvider(FTBU_DATA, owner -> new FTBUUniverseData());
        reg.addPlayerDataProvider(FTBU_DATA, owner -> new FTBUPlayerData());
        reg.addTeamDataProvider(FTBU_DATA, owner -> new FTBUTeamData());

        FTBUPermissions.addConfigs(reg);
    }

    @Override
    public void configLoaded(boolean startup)
    {
        if(startup && FTBUConfigRanks.ENABLED.getBoolean())
        {
            PermissionAPI.setPermissionHandler(FTBUtilitiesAPI_Impl.INSTANCE);
        }
    }

    @Override
    public void registerClient(IFTBLibClientRegistry reg)
    {
        reg.addClientConfig(FTBUFinals.MOD_ID, "render_badges", FTBUClientConfig.RENDER_BADGES);
        reg.addClientConfig(FTBUFinals.MOD_ID, "journeymap_overlay", FTBUClientConfig.JOURNEYMAP_OVERLAY);

        reg.addSidebarButton(FTBUActions.GUIDE);
        reg.addSidebarButton(FTBUActions.SERVER_INFO);
        reg.addSidebarButton(FTBUActions.CLAIMED_CHUNKS);
        reg.addSidebarButton(FTBUActions.TRASH_CAN);
        reg.addSidebarButton(FTBUActions.SHOP);

        if(!LMUtils.isNEILoaded)
        {
            reg.addSidebarButton(FTBUActions.HEAL);
            reg.addSidebarButton(FTBUActions.TOGGLE_GAMEMODE);
            reg.addSidebarButton(FTBUActions.TOGGLE_RAIN);
            reg.addSidebarButton(FTBUActions.TOGGLE_DAY);
            reg.addSidebarButton(FTBUActions.TOGGLE_NIGHT);
        }
    }

    @Override
    public void registerFTBCommands(CommandTreeBase command, boolean dedi)
    {
        FTBUCommands.register(command, dedi);
    }
}