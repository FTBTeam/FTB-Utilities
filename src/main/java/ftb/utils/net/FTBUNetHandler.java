package ftb.utils.net;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.api.net.LMNetworkWrapper;

public class FTBUNetHandler
{
	static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");
	static final LMNetworkWrapper NET_INFO = LMNetworkWrapper.newWrapper("FTBUI");
	
	public static void init()
	{
		NET.register(MessageLMPlayerUpdate.class, 1, Side.CLIENT);
		NET.register(MessageLMPlayerLoggedIn.class, 2, Side.CLIENT);
		NET.register(MessageLMPlayerLoggedOut.class, 3, Side.CLIENT);
		NET.register(MessageLMPlayerDied.class, 4, Side.CLIENT);
		NET.register(MessageRequestSelfUpdate.class, 5, Side.SERVER);
		
		NET_INFO.register(MessageLMPlayerInfo.class, 1, Side.CLIENT);
		NET_INFO.register(MessageAreaUpdate.class, 2, Side.CLIENT);
		NET_INFO.register(MessageAreaRequest.class, 3, Side.SERVER);
		NET_INFO.register(MessageClaimChunk.class, 4, Side.SERVER);
		NET_INFO.register(MessageSendBadge.class, 5, Side.CLIENT);
		NET_INFO.register(MessageUpdateBadges.class, 6, Side.CLIENT);
		NET_INFO.register(MessageRequestBadge.class, 7, Side.SERVER);
		NET_INFO.register(MessageRequestPlayerInfo.class, 8, Side.SERVER);
		NET_INFO.register(MessageRequestServerInfo.class, 9, Side.SERVER);
		NET_INFO.register(MessageManageFriends.class, 10, Side.SERVER);
	}
}