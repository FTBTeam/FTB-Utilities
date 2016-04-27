package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class MessageManageFriends extends MessageLM<MessageManageFriends>
{
	public static final byte ID_ADD = 0;
	public static final byte ID_ADD_ALL = 1;
	public static final byte ID_REMOVE = 2;
	public static final byte ID_DENY = 3;
	
	public byte id;
	public UUID uuid;
	
	public MessageManageFriends() { }
	
	public MessageManageFriends(byte i, UUID uid)
	{
		id = i;
		uuid = uid;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		id = io.readByte();
		
		if(id != ID_ADD_ALL)
		{
			uuid = readUUID(io);
		}
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeByte(id);
		
		if(id != ID_ADD_ALL)
		{
			writeUUID(io, uuid);
		}
	}
	
	@Override
	public IMessage onMessage(MessageManageFriends m, MessageContext ctx)
	{
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		LMPlayerServer p = m.id == ID_ADD_ALL ? null : LMWorldServer.inst.getPlayer(m.uuid);
		
		switch(m.id)
		{
			case ID_ADD:
			{
				if(p == null || p.equalsPlayer(owner)) return null;
				
				if(!owner.friendsList.contains(p.getProfile().getId()))
				{
					owner.friendsList.add(p.getProfile().getId());
					owner.sendUpdate();
					p.sendUpdate();
					p.checkNewFriends();
				}
				
				break;
			}
			case ID_ADD_ALL:
			{
				for(LMPlayerServer p1 : owner.getWorld().playerMap.values())
				{
					if(!p1.equalsPlayer(owner) && p1.isFriendRaw(owner) && !owner.isFriendRaw(p1))
					{
						owner.friendsList.add(p1.getProfile().getId());
						owner.sendUpdate();
						p1.sendUpdate();
						p1.checkNewFriends();
					}
				}
				
				break;
			}
			case ID_REMOVE:
			{
				if(p == null || p.equalsPlayer(owner)) return null;
				
				if(owner.friendsList.contains(p.getProfile().getId()))
				{
					owner.friendsList.remove(p.getProfile().getId());
					owner.sendUpdate();
					p.sendUpdate();
					p.checkNewFriends();
				}
				
				break;
			}
			case ID_DENY:
			{
				if(p == null || p.equalsPlayer(owner)) return null;
				
				if(p.friendsList.contains(owner.getProfile().getId()))
				{
					p.friendsList.remove(owner.getProfile().getId());
					owner.sendUpdate();
					p.sendUpdate();
				}
				
				break;
			}
		}
		
		return new MessageLMPlayerUpdate(LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity), true);
	}
}