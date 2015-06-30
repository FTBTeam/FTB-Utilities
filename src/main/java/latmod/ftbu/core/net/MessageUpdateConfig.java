package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.IServerConfig;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateConfig extends MessageLM<MessageUpdateConfig>
{
	public NBTTagCompound data;
	
	public MessageUpdateConfig() { }
	
	public MessageUpdateConfig(String s)
	{
		data = new NBTTagCompound();
		IServerConfig.Registry.writeToNBT(data, s);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		data = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageUpdateConfig m, MessageContext ctx)
	{
		IServerConfig.Registry.readFromNBT(m.data);
		return null;
	}
}