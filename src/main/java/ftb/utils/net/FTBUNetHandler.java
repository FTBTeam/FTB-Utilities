package ftb.utils.net;

import ftb.lib.api.net.LMNetworkWrapper;

public class FTBUNetHandler
{
	static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");
	
	public static void init()
	{
		new MessageSendBadge().register(1);
		new MessageUpdateBadges().register(2);
		new MessageRequestBadge().register(3);
		new MessageRequestServerInfo().register(4);
		new MessageAreaUpdate().register(5);
		new MessageAreaRequest().register(6);
		new MessageClaimChunk().register(7);
	}
}