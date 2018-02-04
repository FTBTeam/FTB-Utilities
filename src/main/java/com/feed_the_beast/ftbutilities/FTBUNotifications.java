package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.TextComponentHelper;

/**
 * @author LatvianModder
 */
public class FTBUNotifications
{
	public static void sendCantModifyChunk(MinecraftServer server, EntityPlayerMP player)
	{
		Notification.of(FTBUFinals.get("cant_modify_chunk"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.cant_modify_chunk")).setError().send(server, player);
	}
}