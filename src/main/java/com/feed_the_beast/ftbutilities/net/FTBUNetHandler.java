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
		GENERAL.register(2, new MessageSendWarpList());

		BADGES.register(1, new MessageRequestBadge());
		BADGES.register(2, new MessageSendBadge());

		CLAIMS.register(1, new MessageOpenClaimedChunksGui());
		CLAIMS.register(2, new MessageClaimedChunksRequest());
		CLAIMS.register(3, new MessageClaimedChunksUpdate());
		CLAIMS.register(4, new MessageClaimedChunksModify());

		NBTEDIT.register(1, new MessageEditNBT());
		NBTEDIT.register(2, new MessageEditNBTResponse());
		NBTEDIT.register(3, new MessageEditNBTRequest());

		VIEW_CRASH.register(1, new MessageViewCrash());
		VIEW_CRASH.register(2, new MessageViewCrashList());

		STATS.register(1, new MessageSendLeaderboardList());
		STATS.register(2, new MessageSendLeaderboard());
	}
}