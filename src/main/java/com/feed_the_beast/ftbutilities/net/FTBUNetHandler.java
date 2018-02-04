package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUFinals;

public class FTBUNetHandler
{
	static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID);
	static final NetworkWrapper BADGES = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_badges");
	static final NetworkWrapper CLAIMS = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_claims");
	static final NetworkWrapper NBTEDIT = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_nbtedit");
	static final NetworkWrapper VIEW_CRASH = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_view_crash");
	static final NetworkWrapper LEADERBOARDS = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_leaderboards");

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

		LEADERBOARDS.register(1, new MessageSendLeaderboardList());
		LEADERBOARDS.register(2, new MessageSendLeaderboard());
	}
}