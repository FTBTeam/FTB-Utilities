package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.*;
import latmod.ftbu.core.world.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientGuiAction extends MessageLM<MessageClientGuiAction>
{
	public static final int ACTION_ADD_FRIEND = 1;
	public static final int ACTION_REM_FRIEND = 2;
	public static final int ACTION_DENY_FRIEND = 3;
	public static final int ACTION_SET_SAFE_CHUNKS = 4;
	public static final int ACTION_CHAT_LINKS = 5;
	
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
		
		if(m.action == ACTION_ADD_FRIEND || m.action == ACTION_REM_FRIEND || m.action == ACTION_DENY_FRIEND)
		{
			LMPlayerServer p = LMWorldServer.inst.getPlayer(m.extra);
			if(p == null || p.equalsPlayer(owner)) return null;
			
			if(m.action == ACTION_ADD_FRIEND)
			{
				if(!owner.friends.contains(p.playerID))
				{
					owner.friends.add(p.playerID);
					owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
					p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
					
					Notification n = new Notification(LatCoreMC.setColor(EnumChatFormatting.GREEN, new ChatComponentText("Added a friend")), 800);
					n.setDesc(new ChatComponentText(p.getName()));
					n.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "C:/LatvianModder/Pictures/terrain.png"));
					LatCoreMC.notifyPlayer(ep, n);
				}
			}
			else if(m.action == ACTION_REM_FRIEND)
			{
				if(owner.friends.contains(p.playerID))
				{
					owner.friends.removeValue(p.playerID);
					owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
					p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
					
					Notification n = new Notification(LatCoreMC.setColor(EnumChatFormatting.RED, new ChatComponentText("Removed a friend")), 800);
					n.setDesc(new ChatComponentText(p.getName()));
					LatCoreMC.notifyPlayer(ep, n);
				}
			}
			else if(m.action == ACTION_DENY_FRIEND)
			{
				if(p.friends.contains(owner.playerID))
				{
					p.friends.removeValue(owner.playerID);
					owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
					p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
					
					Notification n = new Notification(LatCoreMC.setColor(EnumChatFormatting.RED, new ChatComponentText("Denied a friend request")), 800);
					n.setDesc(new ChatComponentText(p.getName()));
					LatCoreMC.notifyPlayer(ep, n);
				}
			}
		}
		else if(m.action == ACTION_SET_SAFE_CHUNKS)
		{
			owner.claims.setSafe(m.extra == 1);
			owner.sendUpdate(null, true);
		}
		else if(m.action == ACTION_CHAT_LINKS)
		{
			owner.chatLinks = (m.extra == 1);
			owner.sendUpdate(null, true);
		}
		
		return null;
	}
}