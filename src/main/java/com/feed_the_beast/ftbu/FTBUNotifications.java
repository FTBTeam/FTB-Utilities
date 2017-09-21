package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class FTBUNotifications
{
	public static final Notification CANT_MODIFY_CHUNK = Notification.of(FTBUFinals.get("cant_modify_chunk"), new TextComponentTranslation("ftbu.lang.chunks.cant_modify_chunk")).setError();
	public static final Notification CLAIMING_NOT_ENABLED_DIM = Notification.of(FTBUFinals.get("cant_claim_chunk"), new TextComponentTranslation("ftbu.lang.chunks.claiming_not_enabled_dim")).setError();
	public static final Notification UNCLAIMED_ALL = Notification.of(FTBUFinals.get("unclaimed_all"), new TextComponentTranslation("ftbu.lang.chunks.unclaimed_all"));
	public static final Notification CHUNK_CLAIMED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentTranslation("ftbu.lang.chunks.chunk_claimed"));
	public static final Notification CHUNK_UNCLAIMED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentTranslation("ftbu.lang.chunks.chunk_unclaimed"));
	public static final Notification CHUNK_LOADED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentTranslation("ftbu.lang.chunks.chunk_loaded"));
	public static final Notification CHUNK_UNLOADED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentTranslation("ftbu.lang.chunks.chunk_unloaded"));
	public static final Notification WILDERNESS = Notification.of(FTBUFinals.get("chunk_changed"), StringUtils.color(ChunkUpgrade.WILDERNESS.getLangKey().textComponent(), TextFormatting.DARK_GREEN));
}