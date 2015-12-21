package latmod.ftbu.net;

import ftb.lib.api.*;
import latmod.lib.ByteCount;

abstract class MessageFTBU extends MessageLM
{
	public MessageFTBU(ByteCount t)
	{ super(t); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
}