package latmod.core.net;

import latmod.core.mod.LC;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class LMNetHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LC.mod.modID);
	
	public static void init()
	{
		INSTANCE.registerMessage(MessageUpdateLMData.class, MessageUpdateLMData.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(MessageUpdateLMPlayer.class, MessageUpdateLMPlayer.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(MessageCustomClientAction.class, MessageCustomClientAction.class, 2, Side.SERVER);
		INSTANCE.registerMessage(MessageCustomServerAction.class, MessageCustomServerAction.class, 3, Side.CLIENT);
		INSTANCE.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, 4, Side.SERVER);
		INSTANCE.registerMessage(MessageLMKeyPressed.class, MessageLMKeyPressed.class, 5, Side.SERVER);
		INSTANCE.registerMessage(MessageReload.class, MessageReload.class, 6, Side.CLIENT);
		INSTANCE.registerMessage(MessageDisplayMsg.class, MessageDisplayMsg.class, 7, Side.CLIENT);
		INSTANCE.registerMessage(MessageManageGroups.class, MessageManageGroups.class, 8, Side.SERVER);
	}
}