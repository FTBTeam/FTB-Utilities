package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.LMNetworkWrapper;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.mod.client.gui.GuiSendMail;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageMailUpdate extends MessageFTBU
{
	public MessageMailUpdate() { super(DATA_LONG); }
	
	public MessageMailUpdate(Mail m)
	{
		this();
		NBTTagCompound tag = new NBTTagCompound();
		m.writeToNBT(tag);
		tag.setInteger("Rec", m.receiver.playerID);
		writeTag(tag);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_MAIL; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		NBTTagCompound tag = readTag();
		int id = tag.getInteger("ID");
		
		Mail m = LMWorldClient.inst.tempMail.getObj(id);
		if(m == null)
		{
			LMPlayer p = LMWorldClient.inst.getPlayer(tag.getInteger("Rec"));
			if(p == null) return null;
			m = new Mail(id, p);
			FTBLibClient.mc.displayGuiScreen(new GuiSendMail(m));
		}
		
		m.readFromNBT(tag);
		return null;
	}
}