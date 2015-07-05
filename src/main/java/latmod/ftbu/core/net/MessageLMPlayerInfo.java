package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageLMPlayerInfo extends MessageLM<MessageLMPlayerInfo> implements IClientMessageLM<MessageLMPlayerInfo>
{
	public int playerID;
	public NBTTagCompound info;
	
	public MessageLMPlayerInfo() { }
	
	public MessageLMPlayerInfo(int i, NBTTagCompound s)
	{
		playerID = i;
		info = s;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		info = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		writeTagCompound(bb, info);
	}
	
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerInfo m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorld.client.getPlayer(m.playerID);
		if(p != null) p.receiveInfo(m.info);
	}
}