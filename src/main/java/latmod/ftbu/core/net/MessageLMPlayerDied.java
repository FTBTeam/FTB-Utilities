package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.Calendar;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.util.LMStringUtils;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageLMPlayerDied extends MessageLM<MessageLMPlayerDied> implements IClientMessageLM<MessageLMPlayerDied>
{
	public int playerID;
	
	public MessageLMPlayerDied() { }
	
	public MessageLMPlayerDied(LMPlayer p)
	{
		playerID = p.playerID;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageLMPlayerDied m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerDied m, MessageContext ctx)
	{
		if(m.playerID == LMWorldClient.inst.clientPlayer.playerID && Waypoints.enabled.getB() && Waypoints.deathPoint.getB())
		{
			EntityPlayer ep = LatCoreMCClient.getMinecraft().thePlayer;
			Calendar c = Calendar.getInstance();
			
			StringBuilder sb = new StringBuilder();
			sb.append(LMStringUtils.formatInt(c.get(Calendar.MONTH) + 1));
			sb.append('_');
			sb.append(LMStringUtils.formatInt(c.get(Calendar.DAY_OF_MONTH)));
			sb.append('_');
			sb.append(LMStringUtils.formatInt(c.get(Calendar.HOUR_OF_DAY)));
			sb.append('_');
			sb.append(LMStringUtils.formatInt(c.get(Calendar.MINUTE)));
			
			Waypoint w = new Waypoint();
			w.name = sb.toString();
			w.dim = ep.dimension;
			w.setPos(ep.posX, ep.posY, ep.posZ);
			w.isMarker = false;
			w.setColor(LatCoreMC.rand.nextInt(100) + 155, LatCoreMC.rand.nextInt(100), 0);
			Waypoints.add(w);
		}
	}
}