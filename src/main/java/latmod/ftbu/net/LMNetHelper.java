package latmod.ftbu.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.mod.FTBU;

public class LMNetHelper
{
	static final SimpleNetworkWrapper NET = newChannel(FTBU.mod.modID);
	
	public static void init()
	{
		int ID = 0;
		NET.registerMessage(MessageLMWorldJoined.class, MessageLMWorldJoined.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMWorldUpdate.class, MessageLMWorldUpdate.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerUpdate.class, MessageLMPlayerUpdate.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageClientAction.class, MessageClientAction.class, ++ID, Side.SERVER);
		NET.registerMessage(MessageOpenGui.class, MessageOpenGui.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, ++ID, Side.SERVER);
		NET.registerMessage(MessageClientItemAction.class, MessageClientItemAction.class, ++ID, Side.SERVER);
		NET.registerMessage(MessageLMPlayerDied.class, MessageLMPlayerDied.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageNotifyPlayer.class, MessageNotifyPlayer.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerLoggedIn.class, MessageLMPlayerLoggedIn.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerLoggedOut.class, MessageLMPlayerLoggedOut.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageLMPlayerInfo.class, MessageLMPlayerInfo.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageAreaUpdate.class, MessageAreaUpdate.class, ++ID, Side.CLIENT);
		NET.registerMessage(MessageClaimChunk.class, MessageClaimChunk.class, ++ID, Side.SERVER);
	}
	
	public static SimpleNetworkWrapper newChannel(String s)
	{ return NetworkRegistry.INSTANCE.newSimpleChannel(s); }
}