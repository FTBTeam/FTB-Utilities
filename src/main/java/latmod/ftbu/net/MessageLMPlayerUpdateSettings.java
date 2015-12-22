package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;

public class MessageLMPlayerUpdateSettings extends MessageFTBU
{
	public MessageLMPlayerUpdateSettings() { super(ByteCount.SHORT); }
	
	public MessageLMPlayerUpdateSettings(LMPlayer p)
	{
		this();
		p.getSettings().writeToNet(io);
		io.writeBoolean(p.renderBadge);
	}
	
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		p.getSettings().readFromNet(io);
		p.renderBadge = io.readBoolean();
		return new MessageLMPlayerUpdate(p, true);
	}
}