package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUNetHandler
{
	static final NetworkWrapper NET = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID);

	public static void init()
	{
		//1
		NET.register(2, new MessageRequestBadge());
		NET.register(3, new MessageSendBadge());
		NET.register(4, new MessageClaimedChunksRequest());
		NET.register(5, new MessageClaimedChunksUpdate());
		NET.register(6, new MessageClaimedChunksModify());
		NET.register(7, new MessageSendWarpList());
		NET.register(8, new MessageEditNBT());
		NET.register(9, new MessageEditNBTResponse());
		NET.register(10, new MessageOpenClaimedChunksGui());
	}
}