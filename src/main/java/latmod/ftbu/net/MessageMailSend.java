package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.*;
import latmod.lib.*;

public class MessageMailSend extends MessageFTBU
{
	public MessageMailSend() { super(DATA_LONG); }
	
	public MessageMailSend(int id, FastList<String> text)
	{
		this();
		io.writeInt(id);
		io.writeShort((short)text.size());
		for(int i = 0; i < text.size(); i++)
			io.writeString(text.get(i));
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_MAIL; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		Mail mail = LMWorldServer.inst.tempMail.getObj(io.readInt());
		
		if(mail != null)
		{
			mail.timeSent = LMUtils.millis();
			
			int s = io.readShort();
			
			mail.text.clear();
			for(int i = 0; i < s; i++)
				mail.text.add(io.readString());
			
			mail.receiver.mail.add(mail);
			LMWorldServer.inst.tempMail.removeObj(mail.mailID);
		}
		
		return null;
	}
}