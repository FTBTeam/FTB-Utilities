package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.ftbu.api.config.ConfigSyncRegistry;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageSyncConfig extends MessageByteArray<MessageSyncConfig>
{
	public MessageSyncConfig() {}
	
	public MessageSyncConfig(EntityPlayerMP ep)
	{
		ConfigSyncRegistry.writeToIO(io);
	}
	
	public IMessage onMessage(MessageSyncConfig m, MessageContext ctx)
	{
		ConfigSyncRegistry.readFromIO(m.io);
		return null;
	}
}