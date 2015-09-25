package latmod.ftbu.core.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.world.*;
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
	
	public void fromBytes(ByteBuf bb)
	{
		action = ClientAction.VALUES[bb.readByte() & 0xFF];
		extra = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeByte(action.ID);
		bb.writeInt(extra);
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