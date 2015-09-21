package latmod.ftbu.core.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.*;
import latmod.ftbu.core.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class MessageClientGuiAction extends MessageLM<MessageClientGuiAction>
{
	public static final int ACTION_ADD_FRIEND = 1;
	public static final int ACTION_REM_FRIEND = 2;
	public static final int ACTION_DENY_FRIEND = 3;
	public static final int ACTION_SET_SAFE_CHUNKS = 4;
	public static final int ACTION_CHAT_LINKS = 5;
	public static final int ACTION_CHUNK_MESSAGES = 6;
	//FIXME: public static final int ACTION_MY_BADGE = 7;
	
	public int action;
	public int extra;
	
	public MessageClientGuiAction() { }
	
	public MessageClientGuiAction(int a, int e)
	{
		action = a;
		extra = e;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		action = bb.readByte();
		extra = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeByte(action);
		bb.writeInt(extra);
	}
	
	public IMessage onMessage(MessageClientGuiAction m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ep);
		
		if(onAction(m.action, m.extra, ep, owner))
			owner.sendUpdate(true);
		
		return null;
	}
	
	public boolean onAction(int a, int e, EntityPlayerMP ep, LMPlayerServer owner)
	{
		if(a == ACTION_ADD_FRIEND || a == ACTION_REM_FRIEND || a == ACTION_DENY_FRIEND)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(e);
			if(p == null || p.equalsPlayer(owner)) return false;
			
			if(a == ACTION_ADD_FRIEND)
			{
				if(!owner.friends.contains(p.playerID))
				{
					owner.friends.add(p.playerID);
					owner.sendUpdate(true);
					p.sendUpdate(true);
					
					if(p.isOnline())
					{
						Notification n = new Notification("friend_request", LatCoreMC.setColor(EnumChatFormatting.GREEN, new ChatComponentText("New friend request from " + owner.getName() + "!")), 4000);
						n.setDesc(new ChatComponentText("Click to add as friend"));
						n.setClickEvent(new NotificationClick(NotificationClick.CMD, "/ftbu friends add " + owner.getName()));
						LatCoreMC.notifyPlayer(p.getPlayer(), n);
					}
				}
			}
			else if(a == ACTION_REM_FRIEND)
			{
				if(owner.friends.contains(p.playerID))
				{
					owner.friends.removeValue(p.playerID);
					owner.sendUpdate(true);
					p.sendUpdate(true);
					Notification n = new Notification("friend_removed", LatCoreMC.setColor(EnumChatFormatting.RED, new ChatComponentText("Removed a friend")), 800);
					n.setDesc(new ChatComponentText(p.getName()));
					LatCoreMC.notifyPlayer(ep, n);
				}
			}
			else if(a == ACTION_DENY_FRIEND)
			{
				if(p.friends.contains(owner.playerID))
				{
					p.friends.removeValue(owner.playerID);
					owner.sendUpdate(true);
					p.sendUpdate(true);
					
					Notification n = new Notification("friend_denied", LatCoreMC.setColor(EnumChatFormatting.RED, new ChatComponentText("Denied a friend request")), 800);
					n.setDesc(new ChatComponentText(p.getName()));
					LatCoreMC.notifyPlayer(ep, n);
				}
			}
		}
		else if(a == ACTION_SET_SAFE_CHUNKS)
		{
			owner.claims.settings.setSafe(owner, e == 1);
			return true;
		}
		else if(a == ACTION_CHAT_LINKS)
		{
			owner.chatLinks = (e == 1);
			return true;
		}
		else if(a == ACTION_CHUNK_MESSAGES)
		{
			owner.chunkMessages = e;
			return true;
		}
		
		return false;
	}
}