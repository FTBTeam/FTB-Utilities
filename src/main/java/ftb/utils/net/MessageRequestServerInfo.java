package ftb.utils.net;

import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageToServer;
import ftb.utils.api.guide.ServerGuideFile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageRequestServerInfo extends MessageToServer<MessageRequestServerInfo>
{
	public MessageRequestServerInfo() { }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
	}
	
	@Override
	public void onMessage(MessageRequestServerInfo m, EntityPlayerMP ep)
	{
		ForgePlayerMP owner = ForgeWorldMP.inst.getPlayer(ep);
		new ServerGuideFile(owner).displayGuide(owner.getPlayer());
	}
}