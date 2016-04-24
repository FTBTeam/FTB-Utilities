package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ClientBadges;
import latmod.lib.ByteCount;

import java.util.Collection;

public class MessageUpdateBadges extends MessageLM
{
	public MessageUpdateBadges() { super(ByteCount.INT); }
	
	public MessageUpdateBadges(Collection<Badge> badges)
	{
		this();
		io.writeInt(badges.size());
		
		if(!badges.isEmpty())
		{
			for(Badge b : badges)
			{
				io.writeUTF(b.getID());
				io.writeUTF(b.imageURL);
			}
		}
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		ClientBadges.clear();
		int s = io.readInt();
		
		if(s > 0)
		{
			for(int i = 0; i < s; i++)
			{
				String id = io.readUTF();
				String url = io.readUTF();
				ClientBadges.addBadge(new Badge(id, url));
			}
		}
		
		return null;
	}
}