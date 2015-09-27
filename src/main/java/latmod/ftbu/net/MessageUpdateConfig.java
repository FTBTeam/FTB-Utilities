package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.api.ServerConfigRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class MessageUpdateConfig extends MessageLM<MessageUpdateConfig>
{
	public NBTTagCompound data;
	
	public MessageUpdateConfig() { }
	
	public MessageUpdateConfig(EntityPlayerMP ep, String s)
	{
		data = new NBTTagCompound();
		ServerConfigRegistry.writeToNBT(data, ep, s);
	}
	
	public void readData(ByteIOStream io) throws Exception
	{
		data = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		LMNetHelper.writeTagCompound(io, data);
	}
	
	public IMessage onMessage(MessageUpdateConfig m, MessageContext ctx)
	{
		ServerConfigRegistry.readFromNBT(m.data);
		return null;
	}
}