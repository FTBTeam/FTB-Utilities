package latmod.ftbu.net;

import ftb.lib.api.*;

abstract class MessageFTBU extends MessageLM
{
	public MessageFTBU(int t)
	{ super(t); }
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
}