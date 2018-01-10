package com.feed_the_beast.ftbutilities.client;

import com.feed_the_beast.ftblib.FTBLibFinals;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftbutilities.FTBUCommon;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final KeyBinding KEY_WARP = new KeyBinding("key.ftbutilities.warp", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_H, FTBLibFinals.KEY_CATEGORY);

	@Override
	public void preInit()
	{
		super.preInit();

		FTBUClientConfig.sync();
		ClientRegistry.registerKeyBinding(KEY_WARP);
	}

	@Override
	public void postInit()
	{
		super.postInit();

		ClientUtils.MC.getRenderManager().getSkinMap().get("default").addLayer(LayerBadge.INSTANCE);
		ClientUtils.MC.getRenderManager().getSkinMap().get("slim").addLayer(LayerBadge.INSTANCE);
	}
}