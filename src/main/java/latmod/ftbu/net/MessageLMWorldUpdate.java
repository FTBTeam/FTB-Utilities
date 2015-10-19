package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldUpdate extends MessageLM<MessageLMWorldUpdate>
{
	public NBTTagCompound data;
	
	public MessageLMWorldUpdate() { }
	
	public MessageLMWorldUpdate(LMWorldServer w)
	{
		data = new NBTTagCompound();
		w.writeDataToNet(data, 0);
	}
	
	public void fromBytes(ByteBuf io)
	{
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		ByteBufUtils.writeTag(io, data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMWorldUpdate m, MessageContext ctx)
	{
		LMWorldClient.inst.readDataFromNet(m.data, false);
		return null;
	}
}