package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.LangKey;

/**
 * @author LatvianModder
 */
public interface FTBULang
{
	LangKey TIMER_SHUTDOWN = LangKey.of("ftbu.lang.timer.shutdown", String.class);
	LangKey TIMER_BACKUP = LangKey.of("ftbu.lang.timer.backup", String.class);
	LangKey LEADERBOARDS = LangKey.of("ftbu.lang.leaderboards");
	LangKey KILLED_ENTITIES = LangKey.of("ftbu.lang.killed_entities", Integer.class, String.class);
	LangKey GUIDE_UNSUPPORTED_FORMAT = LangKey.of("ftbu.lang.guide.unsupported_format");
	LangKey GUIDE_COMMANDS_FAILED = LangKey.of("ftbu.lang.guide.commands_failed");
	LangKey KICKME = LangKey.of("ftbu.lang.kickme");
	LangKey PERM_FOR = LangKey.of("ftbu.lang.perm_for", String.class, String.class, String.class);

	LangKey CHUNKS_UNLOADED_FOR = LangKey.of("ftbu.lang.chunks.unloaded_for");
	LangKey CHUNKS_UNCLAIM_ALL_Q = LangKey.of("ftbu.lang.chunks.unclaim_all_q");
	LangKey CHUNKS_UNCLAIM_ALL_DIM_Q = LangKey.of("ftbu.lang.chunks.unclaim_all_dim_q", String.class);
	LangKey CHUNKS_UNCLAIM_ALL = LangKey.of("ftbu.lang.chunks.unclaim_all");
	LangKey CHUNKS_UNCLAIM_ALL_DIM = LangKey.of("ftbu.lang.chunks.unclaim_all_dim", String.class);
	LangKey CHUNKS_CLAIMED_COUNT = LangKey.of("ftbu.lang.chunks.claimed_count", Integer.class, Integer.class);
	LangKey CHUNKS_LOADED_COUNT = LangKey.of("ftbu.lang.chunks.loaded_count", Integer.class, Integer.class);

	LangKey WARP_SET = LangKey.of("ftbu.lang.warps.set", String.class);
	LangKey WARP_DEL = LangKey.of("ftbu.lang.warps.del", String.class);
	LangKey WARP_NOT_SET = LangKey.of("ftbu.lang.warps.not_set", String.class);
	LangKey WARP_TP = LangKey.of("ftbu.lang.warps.tp", String.class);
	LangKey WARP_NO_DP = LangKey.of("ftbu.lang.warps.no_dp");
	LangKey WARP_SPAWN = LangKey.of("ftbu.lang.warps.spawn");

	LangKey HOME_SET = LangKey.of("ftbu.lang.homes.set", String.class);
	LangKey HOME_DEL = LangKey.of("ftbu.lang.homes.del", String.class);
	LangKey HOME_NOT_SET = LangKey.of("ftbu.lang.homes.not_set", String.class);
	LangKey HOME_CROSS_DIM = LangKey.of("ftbu.lang.homes.cross_dim");
	LangKey HOME_LIMIT = LangKey.of("ftbu.lang.homes.limit");

	LangKey BACKUP_START = LangKey.of("ftbu.backup.start", String.class);
	LangKey BACKUP_STOP = LangKey.of("ftbu.backup.stop");
	LangKey BACKUP_END_1 = LangKey.of("ftbu.backup.end_1", String.class);
	LangKey BACKUP_END_2 = LangKey.of("ftbu.backup.end_2", String.class, String.class);
	LangKey BACKUP_FAIL = LangKey.of("ftbu.backup.fail", String.class);
	LangKey BACKUP_MANUAL_LAUNCH = LangKey.of("ftbu.backup.manual_launch", String.class);
	LangKey BACKUP_ALREADY_RUNNING = LangKey.of("ftbu.backup.already_running");
	LangKey BACKUP_NOT_RUNNING = LangKey.of("ftbu.backup.not_running");
	LangKey BACKUP_SIZE = LangKey.of("ftbu.backup.size", String.class, String.class);
	LangKey BACKUP_SAVING_FAILED = LangKey.of("ftbu.backup.saving_failed");
	LangKey BACKUP_DELETING_OLD = LangKey.of("ftbu.backup.deleting_old", String.class);
	LangKey BACKUP_BACKING_UP_FILES = LangKey.of("ftbu.backup.backing_up_files", Integer.class);
	LangKey BACKUP_COMPRESSING_FILES = LangKey.of("ftbu.backup.compressing_files", Integer.class);
	LangKey BACKUP_COMPRESSING_DONE = LangKey.of("ftbu.backup.compressing_done", String.class, String.class);
	LangKey BACKUP_CREATED_FROM = LangKey.of("ftbu.backup.created_from", String.class, String.class);

	LangKey RANK_ID_EXISTS = LangKey.of("ftbu.lang.rank.id_exists", String.class);
	LangKey RANK_NOT_FOUND = LangKey.of("ftbu.lang.rank.not_found", String.class);
	LangKey RANK_USE_DEOP = LangKey.of("ftbu.lang.rank.use_deop", String.class);
	LangKey RANK_USE_OP = LangKey.of("ftbu.lang.rank.use_op", String.class);
	LangKey RANK_SET = LangKey.of("ftbu.lang.rank.set", String.class, String.class);
	LangKey RANK_UNSET = LangKey.of("ftbu.lang.rank.unset", String.class);
}