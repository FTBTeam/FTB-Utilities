package ftb.utils.net;

import ftb.lib.api.net.LMNetworkWrapper;

public class FTBUNetHandler
{
	static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");
	
	public static void init()
	{
		NET.register(1, new MessageSendBadge());
		NET.register(2, new MessageUpdateBadges());
		NET.register(3, new MessageRequestBadge());
		NET.register(4, new MessageRequestServerInfo());
		NET.register(5, new MessageAreaUpdate());
		NET.register(6, new MessageAreaRequest());
		NET.register(7, new MessageClaimChunk());
	}
}