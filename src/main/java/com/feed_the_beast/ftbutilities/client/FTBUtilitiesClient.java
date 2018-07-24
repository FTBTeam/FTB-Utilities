package com.feed_the_beast.ftbutilities.client;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.command.client.CmdPrintItem;
import com.feed_the_beast.ftbutilities.command.client.CmdScanItems;
import com.feed_the_beast.ftbutilities.command.client.CmdShrug;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class FTBUtilitiesClient extends FTBUtilitiesCommon // FTBLibClient
{
	public static final KeyBinding KEY_WARP = new KeyBinding("key.ftbutilities.warp", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_H, FTBLib.KEY_CATEGORY);
	public static final KeyBinding KEY_NBT = new KeyBinding("key.ftbutilities.nbt", KeyConflictContext.IN_GAME, KeyModifier.ALT, Keyboard.KEY_N, FTBLib.KEY_CATEGORY);

	@Override
	public void preInit()
	{
		super.preInit();

		FTBUtilitiesClientConfig.sync();
		ClientRegistry.registerKeyBinding(KEY_WARP);
		ClientRegistry.registerKeyBinding(KEY_NBT);
	}

	@Override
	public void postInit()
	{
		super.postInit();

		ClientCommandHandler.instance.registerCommand(new CmdShrug());

		if (FTBLibConfig.debugging.special_commands)
		{
			ClientCommandHandler.instance.registerCommand(new CmdScanItems());
			ClientCommandHandler.instance.registerCommand(new CmdPrintItem());
		}

		ClientUtils.MC.getRenderManager().getSkinMap().get("default").addLayer(LayerBadge.INSTANCE);
		ClientUtils.MC.getRenderManager().getSkinMap().get("slim").addLayer(LayerBadge.INSTANCE);
	}
}