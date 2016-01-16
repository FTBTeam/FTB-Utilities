package latmod.ftbu.net;

import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.badges.*;
import latmod.lib.ByteCount;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

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
				io.writeUTF(b.ID);
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