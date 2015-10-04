package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.core.util.FastList;
import latmod.ftbu.util.LMNBTUtils;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.*;
import net.minecraft.util.IChatComponent;

public class MessageLMPlayerInfo extends MessageLM<MessageLMPlayerInfo>
{
	public NBTTagCompound data;
	
	public MessageLMPlayerInfo() { }
	
	public MessageLMPlayerInfo(int playerID)
	{
		data = new NBTTagCompound();
		LMPlayerServer p = LMWorldServer.inst.getPlayer(playerID);
		if(p == null) return;
		data.setInteger("P", playerID);
		NBTTagList list = new NBTTagList();
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		p.getInfo(info);
		for(int i = 0; i < info.size(); i++)
			list.appendTag(new NBTTagString(IChatComponent.Serializer.func_150696_a(info.get(i))));
		data.setTag("L", list);
	}
	
	public void fromBytes(ByteBuf io)
	{
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		ByteBufUtils.writeTag(io, data);
	}
	
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.data.getInteger("P"));
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		NBTTagList list = m.data.getTagList("L", LMNBTUtils.STRING);
		for(int i = 0; i < list.tagCount(); i++)
			info.add(IChatComponent.Serializer.func_150699_a(list.getStringTagAt(i)));
		p.receiveInfo(info);
		LatCoreMCClient.onGuiClientAction();
		return null;
	}
}