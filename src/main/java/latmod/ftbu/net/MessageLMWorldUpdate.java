package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;

public class MessageLMWorldUpdate extends MessageFTBU
{
	public MessageLMWorldUpdate() { super(ByteCount.INT); }
	
	public MessageLMWorldUpdate(LMWorldServer w, LMPlayerServer self)
	{
		this();
		w.writeDataToNet(io, self, false);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMWorldClient.inst.readDataFromNet(io, false);
		return null;
	}
}