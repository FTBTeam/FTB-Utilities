package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.ClientBadges;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;

public class MessageUpdateBadges extends MessageToClient<MessageUpdateBadges>
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
				String id = readString(io);
				String url = readString(io);
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
				writeString(io, b.getID());
				writeString(io, b.imageURL);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage(MessageUpdateBadges m, Minecraft mc)
	{
		ClientBadges.clear();
		
		for(Badge b : m.badges)
		{
			ClientBadges.addBadge(b);
		}
	}
}