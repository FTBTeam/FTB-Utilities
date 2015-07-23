package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.event.LMPlayerClientEvent;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
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
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean first)
	{
		playerID = p.playerID;
		uuid = p.getUUID();
		username = p.getName();
		
		data = new NBTTagCompound();
		p.writeToNet(data);
		
		firstTime = first;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		long msb = bb.readLong();
		long lsb = bb.readLong();
		uuid = new UUID(msb, lsb);
		username = LMNetHelper.readString(bb);
		data = LMNetHelper.readTagCompound(bb);
		firstTime = bb.readBoolean();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		bb.writeLong(uuid.getMostSignificantBits());
		bb.writeLong(uuid.getLeastSignificantBits());
		LMNetHelper.writeString(bb, username);
		LMNetHelper.writeTagCompound(bb, data);
		bb.writeBoolean(firstTime);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{
		Minecraft mc = LatCoreMCClient.getMinecraft();
		
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(LMWorldClient.inst, m.playerID, new GameProfile(m.uuid, m.username));
		p.readFromNet(m.data);
		if(add) LMWorldClient.inst.players.add(p);
		p.onPostLoaded();
		
		if(m.uuid.equals(mc.thePlayer.getUniqueID()))
		{
			LatCoreMC.logger.info("Joined the server with PlayerID " + p.playerID + " in world " + LMWorldClient.inst.worldIDS);
			LMWorldClient.inst.clientPlayerID = p.playerID;
			FTBUClient.onWorldJoined(p);
		}
		
		new LMPlayerClientEvent.LoggedIn(p, m.firstTime).post();
	}
}