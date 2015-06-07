package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateAllData extends MessageLM<MessageUpdateAllData>
{
	public NBTTagCompound players;
	public NBTTagCompound config;
	
	public MessageUpdateAllData()
	{
		players = new NBTTagCompound();
		LMDataLoader.writePlayersToNBT(players, false);
		
		config = new NBTTagCompound();
		IServerConfig.Registry.writeToNBT(config);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		players = readTagCompound(bb);
		config = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		writeTagCompound(bb, players);
		writeTagCompound(bb, config);
	}
	
	public IMessage onMessage(MessageUpdateAllData m, MessageContext ctx)
	{
		LMDataLoader.readPlayersFromNBT(m.players, false);
		IServerConfig.Registry.readFromNBT(m.config);
		return null;
	}
}