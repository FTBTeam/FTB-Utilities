package latmod.ftbu.net;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.api.LMNetworkWrapper;

public class FTBUNetHandler
{
	static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");
	static final LMNetworkWrapper NET_INFO = LMNetworkWrapper.newWrapper("FTBUI");
	static final LMNetworkWrapper NET_CLAIMS = LMNetworkWrapper.newWrapper("FTBUC");
	
	public static void init()
	{
		int ID = 0;
		
		NET.register(MessageLMWorldJoined.class, ++ID, Side.CLIENT);
		NET.register(MessageLMWorldUpdate.class, ++ID, Side.CLIENT);
		NET.register(MessageLMPlayerUpdate.class, ++ID, Side.CLIENT);
		NET.register(MessageClientAction.class, ++ID, Side.SERVER);
		NET.register(MessageOpenGui.class, ++ID, Side.CLIENT);
		NET.register(MessageClientTileAction.class, ++ID, Side.SERVER);
		NET.register(MessageClientItemAction.class, ++ID, Side.SERVER);
		NET.register(MessageLMPlayerDied.class, ++ID, Side.CLIENT);
		NET.register(MessageNotifyPlayer.class, ++ID, Side.CLIENT);
		NET.register(MessageLMPlayerLoggedIn.class, ++ID, Side.CLIENT);
		NET.register(MessageLMPlayerLoggedOut.class, ++ID, Side.CLIENT);
		
		NET_INFO.register(MessageLMPlayerInfo.class, 1, Side.CLIENT);
		NET_INFO.register(MessageDisplayGuide.class, 2, Side.CLIENT);
		
		NET_CLAIMS.register(MessageAreaUpdate.class, 1, Side.CLIENT);
		NET_CLAIMS.register(MessageAreaRequest.class, 2, Side.CLIENT);
		NET_CLAIMS.register(MessageClaimChunk.class, 3, Side.SERVER);
	}
}