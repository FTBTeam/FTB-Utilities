package ftb.utils.net;

import ftb.lib.api.net.*;
import latmod.lib.ByteCount;

abstract class MessageFTBU extends MessageLM
{
	public MessageFTBU(ByteCount t)
	{ super(t); }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
}