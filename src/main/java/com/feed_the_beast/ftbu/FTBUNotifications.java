package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.command.TextComponentHelper;

/**
 * @author LatvianModder
 */
public class FTBUNotifications
{
	public static void sendCantModifyChunk(EntityPlayerMP player)
	{
		Notification.of(FTBUFinals.get("cant_modify_chunk"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.cant_modify_chunk")).setError().send(player);
	}
}