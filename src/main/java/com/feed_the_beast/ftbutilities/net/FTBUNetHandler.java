package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilities;

public class FTBUNetHandler
{
	static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID);
	static final NetworkWrapper BADGES = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_badges");
	static final NetworkWrapper CLAIMS = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_claims");
	static final NetworkWrapper NBTEDIT = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_nbtedit");
	static final NetworkWrapper VIEW_CRASH = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_crash");
	static final NetworkWrapper STATS = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_stats");

	public static void init()
	{
		GENERAL.register(new MessageBackupProgress());
		GENERAL.register(new MessageSendWarpList());

		BADGES.register(new MessageRequestBadge());
		BADGES.register(new MessageSendBadge());

		CLAIMS.register(new MessageOpenClaimedChunksGui());
		CLAIMS.register(new MessageClaimedChunksRequest());
		CLAIMS.register(new MessageClaimedChunksUpdate());
		CLAIMS.register(new MessageClaimedChunksModify());

		NBTEDIT.register(new MessageEditNBT());
		NBTEDIT.register(new MessageEditNBTResponse());
		NBTEDIT.register(new MessageEditNBTRequest());

		VIEW_CRASH.register(new MessageViewCrash());
		VIEW_CRASH.register(new MessageViewCrashList());

		STATS.register(new MessageSendLeaderboardList());
		STATS.register(new MessageSendLeaderboard());
	}
}