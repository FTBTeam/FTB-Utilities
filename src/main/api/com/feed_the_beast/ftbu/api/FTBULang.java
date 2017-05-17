package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.LangKey;

/**
 * @author LatvianModder
 */
public class FTBULang
{
    //@formatter:off
    public static final LangKey

    TIMER_RESTART = get("lang.timer_restart"),
    TIMER_BACKUP = get("lang.timer_backup"),

    BUTTON_CLAIMS_UNCLAIM_ALL_Q = get("button.claims_unclaim_all_q"),
    BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q = get("button.claims_unclaim_all_dim_q"),
    BUTTON_CLAIMS_UNCLAIM_ALL = get("button.claims_unclaim_all"),
    BUTTON_CLAIMS_UNCLAIM_ALL_DIM = get("button.claims_unclaim_all_dim"),
    LABEL_CCHUNKS_COUNT = get("label.cchunks_count"),
    LABEL_LCHUNKS_COUNT = get("label.lchunks_count"),

    WARP_SET = get("warp.set"),
    WARP_DEL = get("warp.del"),
    WARP_NOT_SET = get("warp.not_set"),
    WARP_TP = get("warp.tp"),
    WARP_NO_DP = get("warp.no_dp"),
    WARP_SPAWN = get("warp.spawn"),
    HOME_SET = get("home.set"),
    HOME_DEL = get("home.del"),
    HOME_NOT_SET = get("home.not_set"),
    HOME_CROSS_DIM = get("home.cross_dim"),
    HOME_LIMIT = get("home.limit"),

    BACKUP_START = get("backup.start"),
    BACKUP_STOP = get("backup.stop"),
    BACKUP_END_1 = get("backup.end_1"),
    BACKUP_END_2 = get("backup.end_2"),
    BACKUP_FAIL = get("backup.fail"),
    BACKUP_MANUAL_LAUNCH = get("backup.manual_launch"),
    BACKUP_ALREADY_RUNNING = get("backup.already_running"),
    BACKUP_NOT_RUNNING = get("backup.not_running"),
            BACKUP_SIZE = get("backup.size");
    //@formatter:on

    public static LangKey get(String s)
    {
        return new LangKey("ftbu." + s);
    }
}