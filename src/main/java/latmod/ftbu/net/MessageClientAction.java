package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClientAction extends MessageLM<MessageClientAction>
{
	public ClientAction action;
	public int extra;
	
	public MessageClientAction() { }
	
	MessageClientAction(ClientAction a, int e)
	{
		action = (a == null) ? ClientAction.NULL : a;
		extra = e;
	}
	
	public void readData(ByteIOStream io) throws Exception
	{
		action = ClientAction.VALUES[io.readUByte()];
		extra = io.readInt();
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeUByte(action.ID);
		io.writeInt(extra);
	}
	
	public IMessage onMessage(MessageClientAction m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ep);
		
		if(m.action.onAction(m.extra, ep, owner))
			owner.sendUpdate(true);
		
		return null;
	}
}