package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.util.LangKey;

/**
 * @author LatvianModder
 */
public interface FTBUtilitiesLang
{
	LangKey TIMER_SHUTDOWN = LangKey.of("ftbutilities.lang.timer.shutdown", String.class);
	LangKey LEADERBOARDS = LangKey.of("sidebar_button.ftbutilities.leaderboards");
	LangKey KILLED_ENTITIES = LangKey.of("ftbutilities.lang.killed_entities", Integer.class, String.class);
	LangKey KICKME = LangKey.of("ftbutilities.lang.kickme");
	LangKey PERM_FOR = LangKey.of("ftbutilities.lang.perm_for", String.class, String.class, String.class);
	LangKey UPLOAD_CRASH = LangKey.of("ftbutilities.lang.upload_crash");
	LangKey UPLOADED_CRASH = LangKey.of("ftbutilities.lang.uploaded_crash", String.class);

	LangKey CHUNKS_UNLOADED_FOR = LangKey.of("ftbutilities.lang.chunks.unloaded_for");
	LangKey CHUNKS_UNCLAIM_ALL_Q = LangKey.of("ftbutilities.lang.chunks.unclaim_all_q");
	LangKey CHUNKS_UNCLAIM_ALL_DIM_Q = LangKey.of("ftbutilities.lang.chunks.unclaim_all_dim_q", String.class);
	LangKey CHUNKS_UNCLAIM_ALL = LangKey.of("ftbutilities.lang.chunks.unclaim_all");
	LangKey CHUNKS_UNCLAIM_ALL_DIM = LangKey.of("ftbutilities.lang.chunks.unclaim_all_dim", String.class);
	LangKey CHUNKS_CLAIMED_COUNT = LangKey.of("ftbutilities.lang.chunks.claimed_count", Integer.class, Integer.class);
	LangKey CHUNKS_LOADED_COUNT = LangKey.of("ftbutilities.lang.chunks.loaded_count", Integer.class, Integer.class);
	LangKey CHUNKS_CLAIMED_AREA = LangKey.of("ftbutilities.lang.chunks.claimed_area");
	LangKey CHUNKS_WILDERNESS = LangKey.of("ftbutilities.lang.chunks.wilderness");

	LangKey WARP_SET = LangKey.of("ftbutilities.lang.warps.set", String.class);
	LangKey WARP_DEL = LangKey.of("ftbutilities.lang.warps.del", String.class);
	LangKey WARP_NOT_SET = LangKey.of("ftbutilities.lang.warps.not_set", String.class);
	LangKey WARP_TP = LangKey.of("ftbutilities.lang.warps.tp", String.class);
	LangKey WARP_NO_DP = LangKey.of("ftbutilities.lang.warps.no_dp");
	LangKey WARP_SPAWN = LangKey.of("ftbutilities.lang.warps.spawn");

	LangKey HOME_SET = LangKey.of("ftbutilities.lang.homes.set", String.class);
	LangKey HOME_DEL = LangKey.of("ftbutilities.lang.homes.del", String.class);
	LangKey HOME_NOT_SET = LangKey.of("ftbutilities.lang.homes.not_set", String.class);
	LangKey HOME_CROSS_DIM = LangKey.of("ftbutilities.lang.homes.cross_dim");
	LangKey HOME_LIMIT = LangKey.of("ftbutilities.lang.homes.limit");

	LangKey BACKUP_START = LangKey.of("ftbutilities.lang.backup.start", String.class);
	LangKey BACKUP_STOP = LangKey.of("ftbutilities.lang.backup.stop");
	LangKey BACKUP_END_1 = LangKey.of("ftbutilities.lang.backup.end_1", String.class);
	LangKey BACKUP_END_2 = LangKey.of("ftbutilities.lang.backup.end_2", String.class, String.class);
	LangKey BACKUP_FAIL = LangKey.of("ftbutilities.lang.backup.fail", String.class);
	LangKey BACKUP_MANUAL_LAUNCH = LangKey.of("ftbutilities.lang.backup.manual_launch", String.class);
	LangKey BACKUP_ALREADY_RUNNING = LangKey.of("ftbutilities.lang.backup.already_running");
	LangKey BACKUP_NOT_RUNNING = LangKey.of("ftbutilities.lang.backup.not_running");
	LangKey BACKUP_SIZE = LangKey.of("ftbutilities.lang.backup.size", String.class, String.class);
	LangKey BACKUP_SAVING_FAILED = LangKey.of("ftbutilities.lang.backup.saving_failed");

	LangKey RANK_ID_EXISTS = LangKey.of("ftbutilities.lang.rank.id_exists", String.class);
	LangKey RANK_NOT_FOUND = LangKey.of("ftbutilities.lang.rank.not_found", String.class);
	LangKey RANK_USE_DEOP = LangKey.of("ftbutilities.lang.rank.use_deop", String.class);
	LangKey RANK_USE_OP = LangKey.of("ftbutilities.lang.rank.use_op", String.class);
	LangKey RANK_SET = LangKey.of("ftbutilities.lang.rank.set", String.class, String.class);
	LangKey RANK_UNSET = LangKey.of("ftbutilities.lang.rank.unset", String.class);
}