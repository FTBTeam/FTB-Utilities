package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.util.LangKey;
import com.feed_the_beast.ftbu.FTBUFinals;

/**
 * @author LatvianModder
 */
public interface FTBULang
{
	LangKey TIMER_SHUTDOWN = FTBUFinals.lang("lang.timer.shutdown", String.class);
	LangKey TIMER_BACKUP = FTBUFinals.lang("lang.timer.backup", String.class);
	LangKey LEADERBOARDS = LangKey.of("sidebar_button.ftbu.leaderboards");
	LangKey KILLED_ENTITIES = FTBUFinals.lang("lang.killed_entities", Integer.class, String.class);
	LangKey GUIDE_UNSUPPORTED_FORMAT = FTBUFinals.lang("lang.guide.unsupported_format");
	LangKey GUIDE_COMMANDS_FAILED = FTBUFinals.lang("lang.guide.commands_failed");
	LangKey KICKME = FTBUFinals.lang("lang.kickme");
	LangKey PERM_FOR = FTBUFinals.lang("lang.perm_for", String.class, String.class, String.class);
	LangKey UPLOAD_CRASH = FTBUFinals.lang("lang.upload_crash");
	LangKey UPLOADED_CRASH = FTBUFinals.lang("lang.uploaded_crash", String.class);

	LangKey CHUNKS_UNLOADED_FOR = FTBUFinals.lang("lang.chunks.unloaded_for");
	LangKey CHUNKS_UNCLAIM_ALL_Q = FTBUFinals.lang("lang.chunks.unclaim_all_q");
	LangKey CHUNKS_UNCLAIM_ALL_DIM_Q = FTBUFinals.lang("lang.chunks.unclaim_all_dim_q", String.class);
	LangKey CHUNKS_UNCLAIM_ALL = FTBUFinals.lang("lang.chunks.unclaim_all");
	LangKey CHUNKS_UNCLAIM_ALL_DIM = FTBUFinals.lang("lang.chunks.unclaim_all_dim", String.class);
	LangKey CHUNKS_CLAIMED_COUNT = FTBUFinals.lang("lang.chunks.claimed_count", Integer.class, Integer.class);
	LangKey CHUNKS_LOADED_COUNT = FTBUFinals.lang("lang.chunks.loaded_count", Integer.class, Integer.class);
	LangKey CHUNKS_CLAIMED_AREA = FTBUFinals.lang("lang.chunks.claimed_area");
	LangKey CHUNKS_WILDERNESS = FTBUFinals.lang("lang.chunks.wilderness");
	LangKey CHUNKS_CHUNKLOADER_FORCED = FTBUFinals.lang("lang.chunks.chunkloader.forced", String.class, Integer.class, Integer.class, String.class);
	LangKey CHUNKS_CHUNKLOADER_UNFORCED = FTBUFinals.lang("lang.chunks.chunkloader.unforced", String.class, Integer.class, Integer.class, String.class);

	LangKey WARP_SET = FTBUFinals.lang("lang.warps.set", String.class);
	LangKey WARP_DEL = FTBUFinals.lang("lang.warps.del", String.class);
	LangKey WARP_NOT_SET = FTBUFinals.lang("lang.warps.not_set", String.class);
	LangKey WARP_TP = FTBUFinals.lang("lang.warps.tp", String.class);
	LangKey WARP_NO_DP = FTBUFinals.lang("lang.warps.no_dp");
	LangKey WARP_SPAWN = FTBUFinals.lang("lang.warps.spawn");

	LangKey HOME_SET = FTBUFinals.lang("lang.homes.set", String.class);
	LangKey HOME_DEL = FTBUFinals.lang("lang.homes.del", String.class);
	LangKey HOME_NOT_SET = FTBUFinals.lang("lang.homes.not_set", String.class);
	LangKey HOME_CROSS_DIM = FTBUFinals.lang("lang.homes.cross_dim");
	LangKey HOME_LIMIT = FTBUFinals.lang("lang.homes.limit");

	LangKey BACKUP_START = FTBUFinals.lang("lang.backup.start", String.class);
	LangKey BACKUP_STOP = FTBUFinals.lang("lang.backup.stop");
	LangKey BACKUP_END_1 = FTBUFinals.lang("lang.backup.end_1", String.class);
	LangKey BACKUP_END_2 = FTBUFinals.lang("lang.backup.end_2", String.class, String.class);
	LangKey BACKUP_FAIL = FTBUFinals.lang("lang.backup.fail", String.class);
	LangKey BACKUP_MANUAL_LAUNCH = FTBUFinals.lang("lang.backup.manual_launch", String.class);
	LangKey BACKUP_ALREADY_RUNNING = FTBUFinals.lang("lang.backup.already_running");
	LangKey BACKUP_NOT_RUNNING = FTBUFinals.lang("lang.backup.not_running");
	LangKey BACKUP_SIZE = FTBUFinals.lang("lang.backup.size", String.class, String.class);
	LangKey BACKUP_SAVING_FAILED = FTBUFinals.lang("lang.backup.saving_failed");
	LangKey BACKUP_DELETING_OLD = FTBUFinals.lang("lang.backup.deleting_old", String.class);
	LangKey BACKUP_BACKING_UP_FILES = FTBUFinals.lang("lang.backup.backing_up_files", Integer.class);
	LangKey BACKUP_COMPRESSING_FILES = FTBUFinals.lang("lang.backup.compressing_files", Integer.class);
	LangKey BACKUP_COMPRESSING_DONE = FTBUFinals.lang("lang.backup.compressing_done", String.class, String.class);
	LangKey BACKUP_CREATED_FROM = FTBUFinals.lang("lang.backup.created_from", String.class, String.class);

	LangKey RANK_ID_EXISTS = FTBUFinals.lang("lang.rank.id_exists", String.class);
	LangKey RANK_NOT_FOUND = FTBUFinals.lang("lang.rank.not_found", String.class);
	LangKey RANK_USE_DEOP = FTBUFinals.lang("lang.rank.use_deop", String.class);
	LangKey RANK_USE_OP = FTBUFinals.lang("lang.rank.use_op", String.class);
	LangKey RANK_SET = FTBUFinals.lang("lang.rank.set", String.class, String.class);
	LangKey RANK_UNSET = FTBUFinals.lang("lang.rank.unset", String.class);
}