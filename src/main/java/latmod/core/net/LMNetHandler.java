package latmod.core.net;

import io.netty.buffer.ByteBuf;
import latmod.core.mod.LC;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class LMNetHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LC.mod.modID);
	
	public static void init()
	{
		INSTANCE.registerMessage(MessageUpdatePlayerData.class, MessageUpdatePlayerData.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(MessageCustomClientAction.class, MessageCustomClientAction.class, 2, Side.SERVER);
		INSTANCE.registerMessage(MessageCustomServerAction.class, MessageCustomServerAction.class, 3, Side.CLIENT);
		INSTANCE.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, 4, Side.SERVER);
		INSTANCE.registerMessage(MessageLMKeyPressed.class, MessageLMKeyPressed.class, 5, Side.SERVER);
	}
	
	// Helper methods //
	
	public static String readString(ByteBuf data)
	{
		int s = data.readShort();
		if(s == -1) return null;
		String str = "";
		for(int i = 0; i < s; i++)
			str += data.readChar();
		return str;
	}
}