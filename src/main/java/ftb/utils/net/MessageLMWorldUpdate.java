package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldClient;
import ftb.utils.world.LMWorldServer;
import latmod.lib.ByteCount;

public class MessageLMWorldUpdate extends MessageFTBU
{
	public MessageLMWorldUpdate() { super(ByteCount.INT); }
	
	public MessageLMWorldUpdate(LMWorldServer w, LMPlayerServer self)
	{
		this();
		w.writeDataToNet(io, self, false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMWorldClient.inst.readDataFromNet(io, false);
		return null;
	}
}