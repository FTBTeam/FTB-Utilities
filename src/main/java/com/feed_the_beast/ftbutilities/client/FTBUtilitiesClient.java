package com.feed_the_beast.ftbutilities.client;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.command.client.CommandKaomoji;
import com.feed_the_beast.ftbutilities.command.client.CommandPing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.Map;

public class FTBUtilitiesClient extends FTBUtilitiesCommon // FTBLibClient
{
	public static KeyBinding KEY_NBT, KEY_TRASH;

	@Override
	public void preInit()
	{
		super.preInit();

		FTBUtilitiesClientConfig.sync();
		ClientRegistry.registerKeyBinding(KEY_NBT = new KeyBinding("key.ftbutilities.nbt", KeyConflictContext.IN_GAME, KeyModifier.ALT, Keyboard.KEY_N, FTBLib.KEY_CATEGORY));
		ClientRegistry.registerKeyBinding(KEY_TRASH = new KeyBinding("key.ftbutilities.trash", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_DELETE, FTBLib.KEY_CATEGORY));
	}

	@Override
	public void postInit()
	{
		super.postInit();

		for (Map.Entry<String, String> entry : FTBUtilitiesCommon.KAOMOJIS.entrySet())
		{
			ClientCommandHandler.instance.registerCommand(new CommandKaomoji(entry.getKey(), entry.getValue()));
		}

		ClientCommandHandler.instance.registerCommand(new CommandPing());

		Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default").addLayer(LayerBadge.INSTANCE);
		Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim").addLayer(LayerBadge.INSTANCE);
	}
}