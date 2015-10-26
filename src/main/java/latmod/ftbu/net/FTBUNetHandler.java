package latmod.ftbu.net;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.api.LMNetworkWrapper;

public class FTBUNetHandler
{
	static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");
	static final LMNetworkWrapper NET_INFO = LMNetworkWrapper.newWrapper("FTBUI");
	static final LMNetworkWrapper NET_WORLD = LMNetworkWrapper.newWrapper("FTBUW");
	
	public static void init()
	{
		NET.register(MessageLMWorldJoined.class, 1, Side.CLIENT);
		NET.register(MessageLMWorldUpdate.class, 2, Side.CLIENT);
		NET.register(MessageLMPlayerUpdate.class, 3, Side.CLIENT);
		NET.register(MessageClientAction.class, 4, Side.SERVER);
		NET.register(MessageClientTileAction.class, 5, Side.SERVER);
		NET.register(MessageClientItemAction.class, 6, Side.SERVER);
		NET.register(MessageLMPlayerDied.class, 6, Side.CLIENT);
		NET.register(MessageNotifyPlayer.class, 7, Side.CLIENT);
		NET.register(MessageLMPlayerLoggedIn.class, 8, Side.CLIENT);
		NET.register(MessageLMPlayerLoggedOut.class, 9, Side.CLIENT);
		
		NET_INFO.register(MessageLMPlayerInfo.class, 1, Side.CLIENT);
		NET_INFO.register(MessageDisplayGuide.class, 2, Side.CLIENT);
		NET_INFO.register(MessagePing.class, 3, Side.SERVER);
		NET_INFO.register(MessagePingResponse.class, 4, Side.CLIENT);
		NET_INFO.register(MessageOpenGui.class, 5, Side.CLIENT);
		
		NET_WORLD.register(MessageAreaUpdate.class, 1, Side.CLIENT);
		NET_WORLD.register(MessageAreaRequest.class, 2, Side.SERVER);
		NET_WORLD.register(MessageClaimChunk.class, 3, Side.SERVER);
	}
}