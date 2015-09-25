package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
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
	
	public void fromBytes(ByteBuf bb)
	{
		data = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageUpdateConfig m, MessageContext ctx)
	{
		ServerConfigRegistry.readFromNBT(m.data);
		return null;
	}
}