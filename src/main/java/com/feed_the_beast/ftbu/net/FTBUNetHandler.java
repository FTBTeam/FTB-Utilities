package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;

public class FTBUNetHandler
{
	static final NetworkWrapper NET = NetworkWrapper.newWrapper("FTBU");

	public static void init()
	{
		//1
		NET.register(2, new MessageRequestBadge());
		NET.register(3, new MessageSendBadge());
		NET.register(4, new MessageClaimedChunksRequest());
		NET.register(5, new MessageClaimedChunksUpdate());
		NET.register(6, new MessageClaimedChunksModify());
		//7
		NET.register(8, new MessageSendWarpList());
		//9
		NET.register(10, new MessageOpenClaimedChunksGui());
		NET.register(11, new MessageJMRequest());
		NET.register(12, new MessageJMUpdate());
	}
}