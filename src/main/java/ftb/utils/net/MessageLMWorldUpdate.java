package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.world.*;
import latmod.lib.ByteCount;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessageLMWorldUpdate extends MessageLM_IO
{
	public MessageLMWorldUpdate() { super(ByteCount.INT); }
	
	public MessageLMWorldUpdate(LMWorldServer w, LMPlayerServer self)
	{
		this();
		w.writeDataToNet(io, self, false);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMWorldClient.inst.readDataFromNet(io, false);
		return null;
	}
}