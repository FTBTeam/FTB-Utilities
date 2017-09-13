package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.LangKey;

/**
 * @author LatvianModder
 */
public class FTBULang
{
	public static final LangKey TIMER_SHUTDOWN = get("lang.timer_shutdown");
	public static final LangKey TIMER_BACKUP = get("lang.timer_backup");
	public static final LangKey BUTTON_CLAIMS_UNCLAIM_ALL_Q = get("button.claims_unclaim_all_q");
	public static final LangKey BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q = get("button.claims_unclaim_all_dim_q");
	public static final LangKey BUTTON_CLAIMS_UNCLAIM_ALL = get("button.claims_unclaim_all");
	public static final LangKey BUTTON_CLAIMS_UNCLAIM_ALL_DIM = get("button.claims_unclaim_all_dim");
	public static final LangKey LABEL_CCHUNKS_COUNT = get("label.cchunks_count");
	public static final LangKey LABEL_LCHUNKS_COUNT = get("label.lchunks_count");
	public static final LangKey KILLED_ENTITIES = get("killed_entities");
	public static final LangKey GUIDE_UNSUPPORTED_FORMAT = get("guide.unsupported_format");
	public static final LangKey GUIDE_COMMANDS_FAILED = get("guide.commands_failed");
	public static final LangKey KICKME = get("kickme");
	public static final LangKey PERM_FOR = get("perm_for");

	public static final LangKey CHUNKS_UNLOADED_FOR = get("chunks.unloaded_for");
	public static final LangKey CHUNKS_CLAIMED_FOR = get("chunks.claimed_for");
	public static final LangKey CHUNKS_CANT_CLAIM_FOR = get("chunks.cant_claim_for");

	public static final LangKey WARP_SET = get("warp.set");
	public static final LangKey WARP_DEL = get("warp.del");
	public static final LangKey WARP_NOT_SET = get("warp.not_set");
	public static final LangKey WARP_TP = get("warp.tp");
	public static final LangKey WARP_NO_DP = get("warp.no_dp");
	public static final LangKey WARP_SPAWN = get("warp.spawn");
	public static final LangKey HOME_SET = get("home.set");
	public static final LangKey HOME_DEL = get("home.del");
	public static final LangKey HOME_NOT_SET = get("home.not_set");
	public static final LangKey HOME_CROSS_DIM = get("home.cross_dim");
	public static final LangKey HOME_LIMIT = get("home.limit");

	public static final LangKey BACKUP_START = get("backup.start");
	public static final LangKey BACKUP_STOP = get("backup.stop");
	public static final LangKey BACKUP_END_1 = get("backup.end_1");
	public static final LangKey BACKUP_END_2 = get("backup.end_2");
	public static final LangKey BACKUP_FAIL = get("backup.fail");
	public static final LangKey BACKUP_MANUAL_LAUNCH = get("backup.manual_launch");
	public static final LangKey BACKUP_ALREADY_RUNNING = get("backup.already_running");
	public static final LangKey BACKUP_NOT_RUNNING = get("backup.not_running");
	public static final LangKey BACKUP_SIZE = get("backup.size");
	public static final LangKey BACKUP_SAVING_FAILED = get("backup.saving_failed");
	public static final LangKey BACKUP_DELETING_OLD = get("backup.deleting_old");
	public static final LangKey BACKUP_BACKING_UP_FILES = get("backup.backing_up_files");
	public static final LangKey BACKUP_COMPRESSING_FILES = get("backup.compressing_files");
	public static final LangKey BACKUP_COMPRESSING_DONE = get("backup.compressing_done");
	public static final LangKey BACKUP_CREATED_FROM = get("backup.created_from");

	public static final LangKey RANK_ID_EXISTS = get("rank.id_exists");
	public static final LangKey RANK_NOT_FOUND = get("rank.not_found");
	public static final LangKey RANK_USE_DEOP = get("rank.use_deop");
	public static final LangKey RANK_USE_OP = get("rank.use_op");
	public static final LangKey RANK_SET = get("rank.set");
	public static final LangKey RANK_UNSET = get("rank.unset");

	public static LangKey get(String s)
	{
		return LangKey.of("ftbu." + s);
	}
}