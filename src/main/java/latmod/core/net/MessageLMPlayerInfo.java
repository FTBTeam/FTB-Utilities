package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.LMPlayer;
import latmod.core.event.LMPlayerClientEvent;
import latmod.core.mod.LC;
import latmod.core.util.FastList;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageLMPlayerInfo extends MessageLM<MessageLMPlayerInfo> implements IClientMessageLM<MessageLMPlayerInfo>
{
	public int playerID;
	public FastList<String> info;
	
	public MessageLMPlayerInfo() { }
	
	public MessageLMPlayerInfo(int i, FastList<String> s)
	{
		playerID = i;
		info = s;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		
		int s = bb.readByte();
		
		info = new FastList<String>(s);
		for(int i = 0; i < s; i++)
			info.add(readString(bb));
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		
		int l = Math.min(40, info.size());
		bb.writeByte(l);
		for(int i = 0; i < l; i++)
		{
			String s = info.get(i);
			writeString(bb, (s == null) ? "" : s.trim());
		}
	}
	
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
	{ LC.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerInfo m, MessageContext ctx)
	{
		LMPlayer p = LMPlayer.getPlayer(m.playerID);
		
		if(p != null)
		{
			p.clientInfo = m.info;
			new LMPlayerClientEvent.CustomInfo(p, p.clientInfo).post();
			p.clientInfo.sort(null);
		}
	}
}