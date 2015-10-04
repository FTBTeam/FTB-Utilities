package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

//FIXME: Config
public class MessageUpdateConfig extends MessageLM<MessageUpdateConfig>
{
	public NBTTagCompound data;
	
	public MessageUpdateConfig() { }
	
	public MessageUpdateConfig(EntityPlayerMP ep, String s)
	{
		data = new NBTTagCompound();
	}
	
	public void fromBytes(ByteBuf io)
	{
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		ByteBufUtils.writeTag(io, data);
	}
	
	public IMessage onMessage(MessageUpdateConfig m, MessageContext ctx)
	{
		return null;
	}
}