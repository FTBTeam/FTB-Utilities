package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.config.ClientConfigRegistry;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.badges.BadgeRenderer;
import com.feed_the_beast.ftbu.journeymap.IJMPluginHandler;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
    public static final ConfigEntryBool render_badges = new ConfigEntryBool(true);
    public static final ConfigEntryBool light_value_texture_x = new ConfigEntryBool(false);

    public static final String KEY_CATEGORY = "key.categories.ftbu";
    public static final KeyBinding KEY_GUIDE = FTBLibClient.addKeyBinding(new KeyBinding("key.ftbu.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, KEY_CATEGORY));
    public static final KeyBinding KEY_LIGHT_VALUES = FTBLibClient.addKeyBinding(new KeyBinding("key.ftbu.light_values", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_F7, KEY_CATEGORY));
    public static final KeyBinding KEY_CHUNK_BORDER = FTBLibClient.addKeyBinding(new KeyBinding("key.ftbu.chunk_border", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_F9, KEY_CATEGORY));

    public static IJMPluginHandler journeyMapHandler = null;

    @Override
    public void preInit()
    {
        ClientConfigRegistry.addGroup("ftbu", FTBUClient.class);
        FTBUActions.init();

        FTBUWorldDataSP.reloadGlobalBadges();

        MinecraftForge.EVENT_BUS.register(new FTBUClientEventHandler());
    }

    @Override
    public void postInit()
    {
        Map<String, RenderPlayer> skinMap = FTBLibClient.mc().getRenderManager().getSkinMap();
        RenderPlayer render = skinMap.get("default");
        render.addLayer(BadgeRenderer.instance);
        render = skinMap.get("slim");
        render.addLayer(BadgeRenderer.instance);

        //GuideRepoList.refresh();
    }

    @Override
    public void onReloadedClient()
    {
        FTBLibClient.clearCachedData();

        //if(e.modeChanged)
        {
            //FIXME: GuideRepoList.reloadFromFolder(e.world.getMode());
        }
    }
}