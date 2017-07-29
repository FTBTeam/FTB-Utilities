package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class FTBUNotifications
{
	public static final Notification CANT_MODIFY_CHUNK = new Notification(FTBUFinals.get("cant_modify_chunk"), StringUtils.text("Can't modify this chunk!")); //TODO: Lang
	public static final Notification CLAIMING_NOT_ENABLED = new Notification(FTBUFinals.get("cant_claim_chunk"), StringUtils.text("Claiming is not enabled on this server!")); //TODO: Lang
	public static final Notification CLAIMING_NOT_ALLOWED = new Notification(FTBUFinals.get("cant_claim_chunk"), StringUtils.text("You are not allowed to claim this chunk")); //TODO: Lang
	public static final Notification UNCLAIMED_ALL = new Notification(FTBUFinals.get("unclaimed_all"), StringUtils.text("Unclaimed all chunks")); //TODO: Lang
	public static final Notification CHUNK_CLAIMED = new Notification(FTBUFinals.get("chunk_modified"), StringUtils.text("Chunk claimed")); //TODO: Lang
	public static final Notification CHUNK_UNCLAIMED = new Notification(FTBUFinals.get("chunk_modified"), StringUtils.text("Chunk unclaimed")); //TODO: Lang
	public static final Notification CHUNK_LOADED = new Notification(FTBUFinals.get("chunk_modified"), StringUtils.text("Chunk loaded")); //TODO: Lang
	public static final Notification CHUNK_UNLOADED = new Notification(FTBUFinals.get("chunk_modified"), StringUtils.text("Chunk unloaded")); //TODO: Lang
	public static final Notification WILDERNESS = new Notification(FTBUFinals.get("chunk_changed"), StringUtils.color(ChunkUpgrade.WILDERNESS.getLangKey().textComponent(), TextFormatting.DARK_GREEN));
}