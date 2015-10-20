package latmod.ftbu.net;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.world.*;
import latmod.lib.LMUtils;
import net.minecraft.nbt.NBTTagCompound;

public class MessageSendMail extends MessageLM<MessageSendMail>
{
	public int receiver;
	public NBTTagCompound data;
	
	public MessageSendMail() {}
	
	public MessageSendMail(Mail m)
	{
		receiver = m.receiver.playerID;
		data = new NBTTagCompound();
		m.writeToNBT(data);
	}
	
	public void fromBytes(ByteBuf io)
	{
		receiver = io.readInt();
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(receiver);
		ByteBufUtils.writeTag(io, data);
	}
	
	public IMessage onMessage(MessageSendMail m, MessageContext ctx)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(m.receiver);
		
		if(p != null)
		{
			Mail mail = new Mail(p);
			mail.readFromNBT(m.data);
			mail.timeSent = LMUtils.millis();
			p.mail.add(mail);
		}
		
		return null;
	}
}