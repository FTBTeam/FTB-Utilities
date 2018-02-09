package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.util.LangKey;

/**
 * @author LatvianModder
 */
public interface FTBUtilitiesLang
{
	LangKey TIMER_SHUTDOWN = get("lang.timer.shutdown", String.class);
	LangKey TIMER_BACKUP = get("lang.timer.backup", String.class);
	LangKey LEADERBOARDS = get("sidebar_button.ftbutilities.leaderboards");
	LangKey KILLED_ENTITIES = get("lang.killed_entities", Integer.class, String.class);
	LangKey GUIDE_UNSUPPORTED_FORMAT = get("lang.guide.unsupported_format");
	LangKey GUIDE_COMMANDS_FAILED = get("lang.guide.commands_failed");
	LangKey KICKME = get("lang.kickme");
	LangKey PERM_FOR = get("lang.perm_for", String.class, String.class, String.class);
	LangKey UPLOAD_CRASH = get("lang.upload_crash");
	LangKey UPLOADED_CRASH = get("lang.uploaded_crash", String.class);

	LangKey CHUNKS_UNLOADED_FOR = get("lang.chunks.unloaded_for");
	LangKey CHUNKS_UNCLAIM_ALL_Q = get("lang.chunks.unclaim_all_q");
	LangKey CHUNKS_UNCLAIM_ALL_DIM_Q = get("lang.chunks.unclaim_all_dim_q", String.class);
	LangKey CHUNKS_UNCLAIM_ALL = get("lang.chunks.unclaim_all");
	LangKey CHUNKS_UNCLAIM_ALL_DIM = get("lang.chunks.unclaim_all_dim", String.class);
	LangKey CHUNKS_CLAIMED_COUNT = get("lang.chunks.claimed_count", Integer.class, Integer.class);
	LangKey CHUNKS_LOADED_COUNT = get("lang.chunks.loaded_count", Integer.class, Integer.class);
	LangKey CHUNKS_CLAIMED_AREA = get("lang.chunks.claimed_area");
	LangKey CHUNKS_WILDERNESS = get("lang.chunks.wilderness");
	LangKey CHUNKS_CHUNKLOADER_FORCED = get("lang.chunks.chunkloader.forced", String.class, Integer.class, Integer.class, String.class);
	LangKey CHUNKS_CHUNKLOADER_UNFORCED = get("lang.chunks.chunkloader.unforced", String.class, Integer.class, Integer.class, String.class);

	LangKey WARP_SET = get("lang.warps.set", String.class);
	LangKey WARP_DEL = get("lang.warps.del", String.class);
	LangKey WARP_NOT_SET = get("lang.warps.not_set", String.class);
	LangKey WARP_TP = get("lang.warps.tp", String.class);
	LangKey WARP_NO_DP = get("lang.warps.no_dp");
	LangKey WARP_SPAWN = get("lang.warps.spawn");

	LangKey HOME_SET = get("lang.homes.set", String.class);
	LangKey HOME_DEL = get("lang.homes.del", String.class);
	LangKey HOME_NOT_SET = get("lang.homes.not_set", String.class);
	LangKey HOME_CROSS_DIM = get("lang.homes.cross_dim");
	LangKey HOME_LIMIT = get("lang.homes.limit");

	LangKey BACKUP_START = get("lang.backup.start", String.class);
	LangKey BACKUP_STOP = get("lang.backup.stop");
	LangKey BACKUP_END_1 = get("lang.backup.end_1", String.class);
	LangKey BACKUP_END_2 = get("lang.backup.end_2", String.class, String.class);
	LangKey BACKUP_FAIL = get("lang.backup.fail", String.class);
	LangKey BACKUP_MANUAL_LAUNCH = get("lang.backup.manual_launch", String.class);
	LangKey BACKUP_ALREADY_RUNNING = get("lang.backup.already_running");
	LangKey BACKUP_NOT_RUNNING = get("lang.backup.not_running");
	LangKey BACKUP_SIZE = get("lang.backup.size", String.class, String.class);
	LangKey BACKUP_SAVING_FAILED = get("lang.backup.saving_failed");
	LangKey BACKUP_DELETING_OLD = get("lang.backup.deleting_old", String.class);
	LangKey BACKUP_BACKING_UP_FILES = get("lang.backup.backing_up_files", Integer.class);
	LangKey BACKUP_COMPRESSING_FILES = get("lang.backup.compressing_files", Integer.class);
	LangKey BACKUP_COMPRESSING_DONE = get("lang.backup.compressing_done", String.class, String.class);
	LangKey BACKUP_CREATED_FROM = get("lang.backup.created_from", String.class, String.class);

	LangKey RANK_ID_EXISTS = get("lang.rank.id_exists", String.class);
	LangKey RANK_NOT_FOUND = get("lang.rank.not_found", String.class);
	LangKey RANK_USE_DEOP = get("lang.rank.use_deop", String.class);
	LangKey RANK_USE_OP = get("lang.rank.use_op", String.class);
	LangKey RANK_SET = get("lang.rank.set", String.class, String.class);
	LangKey RANK_UNSET = get("lang.rank.unset", String.class);

	static LangKey get(String key, Class... args)
	{
		return LangKey.of(FTBUtilities.MOD_ID + '.' + key, args);
	}
}