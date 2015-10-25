package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.ftbu.world.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClientAction extends MessageFTBU
{
	public MessageClientAction() { super(DATA_SHORT); }
	
	MessageClientAction(ClientAction a, int e)
	{
		this();
		io.writeUByte((a == null) ? ClientAction.NULL.ID : a.ID);
		io.writeInt(e);
	}
	
	public IMessage onMessage(MessageContext ctx)
	{
		ClientAction action = ClientAction.VALUES[io.readUByte()];
		int extra = io.readInt();
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ep);
		
		if(action.onAction(extra, ep, owner))
			owner.sendUpdate();
		
		return null;
	}
}