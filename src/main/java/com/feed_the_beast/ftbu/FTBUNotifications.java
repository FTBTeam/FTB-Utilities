package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class FTBUNotifications
{
	public static final Notification CANT_MODIFY_CHUNK = Notification.of(FTBUFinals.get("cant_modify_chunk"), new TextComponentString("Can't modify this chunk!")); //LANG
	public static final Notification CLAIMING_NOT_ENABLED = Notification.of(FTBUFinals.get("cant_claim_chunk"), new TextComponentString("Claiming is not enabled on this server!")); //LANG
	public static final Notification CLAIMING_NOT_ALLOWED = Notification.of(FTBUFinals.get("cant_claim_chunk"), new TextComponentString("You are not allowed to claim this chunk")); //LANG
	public static final Notification UNCLAIMED_ALL = Notification.of(FTBUFinals.get("unclaimed_all"), new TextComponentString("Unclaimed all chunks")); //LANG
	public static final Notification CHUNK_CLAIMED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentString("Chunk claimed")); //LANG
	public static final Notification CHUNK_UNCLAIMED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentString("Chunk unclaimed")); //LANG
	public static final Notification CHUNK_LOADED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentString("Chunk loaded")); //LANG
	public static final Notification CHUNK_UNLOADED = Notification.of(FTBUFinals.get("chunk_modified"), new TextComponentString("Chunk unloaded")); //LANG
	public static final Notification WILDERNESS = Notification.of(FTBUFinals.get("chunk_changed"), StringUtils.color(ChunkUpgrade.WILDERNESS.getLangKey().textComponent(), TextFormatting.DARK_GREEN));
}