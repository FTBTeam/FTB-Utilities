package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.server.command.TextComponentHelper;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesNotifications
{
	public static final ResourceLocation CHUNK_MODIFIED = new ResourceLocation("ftbutilities:chunk_modified");
	public static final ResourceLocation CHUNK_CHANGED = new ResourceLocation("ftbutilities:chunk_changed");
	public static final ResourceLocation CHUNK_CANT_CLAIM = new ResourceLocation("ftbutilities:cant_claim_chunk");
	public static final ResourceLocation UNCLAIMED_ALL = new ResourceLocation("ftbutilities:unclaimed_all");

	public static void sendCantModifyChunk(MinecraftServer server, EntityPlayerMP player)
	{
		Notification.of(new ResourceLocation(FTBUtilities.MOD_ID, "cant_modify_chunk"), TextComponentHelper.createComponentTranslation(player, "ftbutilities.lang.chunks.cant_modify_chunk")).setError().send(server, player);
	}
}