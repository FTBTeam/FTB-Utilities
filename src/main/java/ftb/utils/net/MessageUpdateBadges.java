package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ClientBadges;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessageUpdateBadges extends MessageLM<MessageUpdateBadges>
{
	public Map<String, String> badges;
	
	public MessageUpdateBadges() { }
	
	public MessageUpdateBadges(Collection<Badge> b)
	{
		badges = new HashMap<>();
		
		for(Badge badge : b)
		{
			badges.put(badge.getID(), badge.imageURL);
		}
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		badges = new HashMap<>();
		
		int s = io.readInt();
		
		for(int i = 0; i < s; i++)
		{
			String id = readString(io);
			String url = readString(io);
			badges.put(id, url);
		}
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeInt(badges.size());
		
		if(!badges.isEmpty())
		{
			for(Map.Entry<String, String> e : badges.entrySet())
			{
				writeString(io, e.getKey());
				writeString(io, e.getValue());
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageUpdateBadges m, MessageContext ctx)
	{
		ClientBadges.clear();
		ClientBadges.addBadges(m.badges);
		return null;
	}
}