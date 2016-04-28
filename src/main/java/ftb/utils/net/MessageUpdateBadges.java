package ftb.utils.net;

import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ClientBadges;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;

public class MessageUpdateBadges extends MessageLM<MessageUpdateBadges>
{
	public Collection<Badge> badges;
	
	public MessageUpdateBadges() { }
	
	public MessageUpdateBadges(Collection<Badge> c)
	{
		badges = c;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		badges = new ArrayList<>();
		int s = io.readInt();
		
		if(s > 0)
		{
			for(int i = 0; i < s; i++)
			{
				String id = ByteBufUtils.readUTF8String(io);
				String url = ByteBufUtils.readUTF8String(io);
				badges.add(new Badge(id, url));
			}
		}
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeInt(badges.size());
		
		if(!badges.isEmpty())
		{
			for(Badge b : badges)
			{
				ByteBufUtils.writeUTF8String(io, b.getID());
				ByteBufUtils.writeUTF8String(io, b.imageURL);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageUpdateBadges m, MessageContext ctx)
	{
		ClientBadges.clear();
		
		for(Badge b : m.badges)
		{
			ClientBadges.addBadge(b);
		}
		
		return null;
	}
}