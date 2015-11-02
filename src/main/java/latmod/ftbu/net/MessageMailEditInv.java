package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.mod.FTBUGuiHandler;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class MessageMailEditInv extends MessageFTBU
{
	public MessageMailEditInv() { super(DATA_SHORT); }
	
	public MessageMailEditInv(int id)
	{
		this();
		io.writeInt(id);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_MAIL; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		Mail mail = LMWorldServer.inst.tempMail.getObj(io.readInt());
		
		if(mail != null)
		{
			EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("ID", mail.mailID);
			FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.MAIL_ITEMS, tag);
			new MessageMailUpdate(mail).sendTo(ep);
		}
		
		return null;
	}
}