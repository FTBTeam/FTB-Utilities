package ftb.utils.net;

import ftb.lib.api.net.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageClientAction extends MessageLM<MessageClientAction>
{
	public byte actionID;
	public NBTTagCompound extra;
	
	public MessageClientAction() { }
	
	MessageClientAction(ClientAction a, NBTTagCompound e)
	{
		actionID = (a == null) ? ClientAction.NULL.getID() : a.getID();
		extra = e;
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	public void fromBytes(ByteBuf io)
	{
		actionID = io.readByte();
		extra = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeByte(actionID);
		ByteBufUtils.writeTag(io, extra);
	}
	
	public IMessage onMessage(MessageClientAction m, MessageContext ctx)
	{
		ClientAction action = ClientAction.get(m.actionID);
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		if(action.onAction(m.extra, owner)) owner.sendUpdate();
		return null;
	}
}