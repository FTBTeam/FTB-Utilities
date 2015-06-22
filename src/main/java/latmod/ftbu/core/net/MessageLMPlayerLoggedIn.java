package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageLMPlayerLoggedIn extends MessageLM<MessageLMPlayerLoggedIn> implements IClientMessageLM<MessageLMPlayerLoggedIn>
{
	public int playerID;
	public UUID uuid;
	public String username;
	public NBTTagCompound data;
	public boolean firstTime;
	
	public MessageLMPlayerLoggedIn() { }
	
	public MessageLMPlayerLoggedIn(LMPlayer p, boolean first)
	{
		playerID = p.playerID;
		uuid = p.getUUID();
		username = p.getName();
		
		data = new NBTTagCompound();
		p.writeToNBT(data, false);
		
		firstTime = first;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		long msb = bb.readLong();
		long lsb = bb.readLong();
		uuid = new UUID(msb, lsb);
		username = readString(bb);
		data = readTagCompound(bb);
		firstTime = bb.readBoolean();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		bb.writeLong(uuid.getMostSignificantBits());
		bb.writeLong(uuid.getLeastSignificantBits());
		writeString(bb, username);
		writeTagCompound(bb, data);
		bb.writeBoolean(firstTime);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{
		Minecraft mc = LatCoreMCClient.getMinecraft();
		if(mc.theWorld == null || mc.thePlayer == null)
		{
			LatCoreMC.logger.info("Client connection error: " + mc.theWorld + " ; " + mc.thePlayer);
			return;
		}
		
		LMPlayer p = new LMPlayer(m.playerID, new GameProfile(m.uuid, m.username));
		LMPlayer.map.put(p.playerID, p);
		
		p.readFromNBT(m.data, false);
		FTBU.proxy.playerLMLoggedIn(p, m.firstTime);
			
	}
}