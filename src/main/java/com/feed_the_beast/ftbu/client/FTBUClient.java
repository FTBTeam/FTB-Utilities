package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.gui.Guides;
import com.feed_the_beast.ftbu.integration.IJMIntegration;
import com.feed_the_beast.ftbu.integration.TiCIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

public class FTBUClient extends FTBUCommon // FTBLibModClient
{
    public static final String KEY_CATEGORY = "key.categories.ftbu";
    public static final KeyBinding KEY_GUIDE = new KeyBinding("key.ftbu.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, KEY_CATEGORY);
    public static final KeyBinding KEY_WARP = new KeyBinding("key.ftbu.warp", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_NONE, KEY_CATEGORY);

    public static IJMIntegration JM_INTEGRATION = null;

    @Override
    public void preInit()
    {
        super.preInit();

        ClientRegistry.registerKeyBinding(KEY_GUIDE);
        ClientRegistry.registerKeyBinding(KEY_WARP);

        MinecraftForge.EVENT_BUS.register(new FTBUClientEventHandler());

        if(Loader.isModLoaded("tconstruct"))
        {
            TiCIntegration.init();
        }
    }

    @Override
    public void postInit()
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getRenderManager().getSkinMap().get("default").addLayer(LayerBadge.INSTANCE);
        mc.getRenderManager().getSkinMap().get("slim").addLayer(LayerBadge.INSTANCE);
        //GuideRepoList.refresh();

        if(mc.getResourceManager() instanceof SimpleReloadableResourceManager)
        {
            ((SimpleReloadableResourceManager) mc.getResourceManager()).registerReloadListener(Guides.INSTANCE);
        }
    }

    @Override
    public void onReloadedClient()
    {
        CachedClientData.clear();
    }
}