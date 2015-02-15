package latmod.core.net;
import latmod.core.LatCoreMC;
import latmod.core.mod.*;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMKeyPressed extends MessageLM implements IMessageHandler<MessageLMKeyPressed, IMessage>
{
	public IMessage onMessage(MessageLMKeyPressed m, MessageContext ctx)
	{
		if(LCConfig.General.friendsGuiEnabled)
			MessageLM.NET.sendTo(new MessageCustomServerAction(LCEventHandler.ACTION_OPEN_FRIENDS_GUI, null), ctx.getServerHandler().playerEntity);
		else
			LatCoreMC.printChat(ctx.getServerHandler().playerEntity, "FriendsGUI is disabled!");
		
		return null;
	}
}