package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.badges.LayerBadge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.Map;

public class FTBUClient extends FTBUCommon // FTBLibModClient
{
    public static final String KEY_CATEGORY = "key.categories.ftbu";
    public static final KeyBinding KEY_GUIDE = new KeyBinding("key.ftbu.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, KEY_CATEGORY);
    public static final KeyBinding KEY_WARP = new KeyBinding("key.ftbu.warp", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_NONE, KEY_CATEGORY);
    public static final KeyBinding KEY_LIGHT_VALUES = new KeyBinding("key.ftbu.light_values", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_F7, KEY_CATEGORY);
    public static final KeyBinding KEY_CHUNK_BORDER = new KeyBinding("key.ftbu.chunk_border", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_F9, KEY_CATEGORY);

    public static boolean HAS_JM = false;

    @Override
    public void preInit()
    {
        ClientRegistry.registerKeyBinding(KEY_GUIDE);
        ClientRegistry.registerKeyBinding(KEY_WARP);
        ClientRegistry.registerKeyBinding(KEY_LIGHT_VALUES);
        ClientRegistry.registerKeyBinding(KEY_CHUNK_BORDER);

        CachedClientData.reloadGlobalBadges();
        MinecraftForge.EVENT_BUS.register(new FTBUClientEventHandler());
    }

    @Override
    public void postInit()
    {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        skinMap.get("default").addLayer(LayerBadge.INSTANCE);
        skinMap.get("slim").addLayer(LayerBadge.INSTANCE);
        //GuideRepoList.refresh();
    }

    @Override
    public void onReloadedClient()
    {
        FTBLibClient.clearCachedData();
        //FIXME: GuideRepoList.reloadFromFolder(e.world.getMode());
    }
}