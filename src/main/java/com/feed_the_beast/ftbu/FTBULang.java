package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.LangKey;

/**
 * Created by LatvianModder on 13.04.2016.
 */
public class FTBULang
{
    public static final LangKey timer_restart = get("lang.timer_restart");
    public static final LangKey timer_backup = get("lang.timer_backup");
    public static final LangKey button_claims_unclaim_all_q = get("button.claims_unclaim_all_q");
    public static final LangKey button_claims_unclaim_all_dim_q = get("button.claims_unclaim_all_dim_q");
    public static final LangKey button_claims_unclaim_all = get("button.claims_unclaim_all");
    public static final LangKey button_claims_unclaim_all_dim = get("button.claims_unclaim_all_dim");
    public static final LangKey label_cchunks_count = get("label.cchunks_count");
    public static final LangKey label_lchunks_count = get("label.lchunks_count");
    public static final LangKey warp_set = get("warp.set");
    public static final LangKey warp_del = get("warp.del");
    public static final LangKey warp_not_set = get("warp.not_set");
    public static final LangKey warp_tp = get("warp.tp");
    public static final LangKey warp_no_dp = get("warp.no_dp");
    public static final LangKey warp_spawn = get("warp.spawn");
    public static final LangKey home_set = get("home.set");
    public static final LangKey home_del = get("home.del");
    public static final LangKey home_not_set = get("home.not_set");
    public static final LangKey home_cross_dim = get("home.cross_dim");
    public static final LangKey home_limit = get("home.limit");
    public static final LangKey backup_start = get("backup.start");
    public static final LangKey backup_stop = get("backup.stop");
    public static final LangKey backup_end_1 = get("backup.end_1");
    public static final LangKey backup_end_2 = get("backup.end_2");
    public static final LangKey backup_fail = get("backup.fail");
    public static final LangKey backup_manual_launch = get("backup.manual_launch");
    public static final LangKey backup_already_running = get("backup.already_running");
    public static final LangKey backup_not_running = get("backup.not_running");
    public static final LangKey backup_size = get("backup.size");

    public static LangKey get(String s)
    {
        return new LangKey("ftbu." + s);
    }
}