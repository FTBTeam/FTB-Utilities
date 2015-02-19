package latmod.core.net;
import latmod.core.*;
import latmod.core.mod.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateAllData extends MessageLM implements IMessageHandler<MessageUpdateAllData, IMessage>
{
	public MessageUpdateAllData()
	{
		data = new NBTTagCompound();
		
		NBTTagCompound players = new NBTTagCompound();
		LMDataLoader.writePlayersToNBT(players);
		data.setTag("P", players);
		
		NBTTagCompound config = new NBTTagCompound();
		IServerConfig.Registry.writeToNBT(config);
		data.setTag("C", config);
	}
	
	public IMessage onMessage(MessageUpdateAllData m, MessageContext ctx)
	{
		LMDataLoader.readPlayersFromNBT(m.data.getCompoundTag("P"));
		IServerConfig.Registry.readFromNBT(m.data.getCompoundTag("C"));
		return null;
	}
}