package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilities;

public class FTBUtilitiesNetHandler
{
	static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID);
	static final NetworkWrapper CLAIMS = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_claims");
	static final NetworkWrapper FILES = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_files");
	static final NetworkWrapper STATS = NetworkWrapper.newWrapper(FTBUtilities.MOD_ID + "_stats");

	public static void init()
	{
		GENERAL.register(new MessageBackupProgress());
		GENERAL.register(new MessageSendWarpList());
		GENERAL.register(new MessageRequestBadge());
		GENERAL.register(new MessageSendBadge());

		CLAIMS.register(new MessageClaimedChunksRequest());
		CLAIMS.register(new MessageClaimedChunksUpdate());
		CLAIMS.register(new MessageClaimedChunksModify());

		FILES.register(new MessageEditNBT());
		FILES.register(new MessageEditNBTResponse());
		FILES.register(new MessageEditNBTRequest());
		FILES.register(new MessageViewCrashList());
		FILES.register(new MessageViewCrash());
		FILES.register(new MessageViewCrashResponse());

		STATS.register(new MessageLeaderboardList());
		STATS.register(new MessageLeaderboardListResponse());
		STATS.register(new MessageLeaderboard());
		STATS.register(new MessageLeaderboardResponse());
	}
}