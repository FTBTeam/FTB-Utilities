package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.ftbu.world.*;
import latmod.lib.LMUtils;
import net.minecraft.nbt.NBTTagCompound;

public class MessageSendMail extends MessageFTBU
{
	public MessageSendMail() { super(DATA_LONG); }
	
	public MessageSendMail(Mail m)
	{
		this();
		io.writeInt(m.receiver.playerID);
		NBTTagCompound data = new NBTTagCompound();
		m.writeToNBT(data);
		writeTag(data);
	}
	
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(io.readInt());
		
		if(p != null)
		{
			Mail mail = new Mail(p);
			mail.readFromNBT(readTag());
			mail.timeSent = LMUtils.millis();
			p.mail.add(mail);
		}
		
		return null;
	}
}