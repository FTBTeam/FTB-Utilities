package ftb.utils.net;

import ftb.lib.api.net.LMNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class FTBUNetHandler
{
	static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");
	static final LMNetworkWrapper NET_WORLD = LMNetworkWrapper.newWrapper("FTBUW");
	
	public static void init()
	{
		NET.register(MessageButtonPressed.class, 1, Side.SERVER);
		NET.register(MessageSendBadge.class, 2, Side.CLIENT);
		NET.register(MessageUpdateBadges.class, 3, Side.CLIENT);
		NET.register(MessageRequestBadge.class, 4, Side.SERVER);
		NET.register(MessageRequestServerInfo.class, 5, Side.SERVER);
		
		NET_WORLD.register(MessageAreaUpdate.class, 1, Side.CLIENT);
		NET_WORLD.register(MessageAreaRequest.class, 2, Side.SERVER);
		NET_WORLD.register(MessageClaimChunk.class, 3, Side.SERVER);
	}
}