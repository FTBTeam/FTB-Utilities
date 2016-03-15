package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.badges.*;
import latmod.lib.ByteCount;

import java.util.Collection;

public class MessageUpdateBadges extends MessageFTBU
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
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
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