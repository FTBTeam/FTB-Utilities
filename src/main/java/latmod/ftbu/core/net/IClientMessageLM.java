package latmod.ftbu.core.net;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.*;

public interface IClientMessageLM<M extends MessageLM<?>>
{
	@SideOnly(Side.CLIENT)
	public void onMessageClient(M m, MessageContext ctx);
}