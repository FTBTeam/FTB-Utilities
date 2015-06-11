package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.ftbu.FTBU;
import latmod.ftbu.core.LMPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMPlayerLoggedIn extends MessageLM<MessageLMPlayerLoggedIn>
{
	public int playerID;
	public UUID uuid;
	public String username;
	public NBTTagCompound data;
	
	public MessageLMPlayerLoggedIn() { }
	
	public MessageLMPlayerLoggedIn(LMPlayer p)
	{
		playerID = p.playerID;
		uuid = p.getUUID();
		username = p.getName();
		
		data = new NBTTagCompound();
		p.writeToNBT(data, false);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		long msb = bb.readLong();
		long lsb = bb.readLong();
		uuid = new UUID(msb, lsb);
		username = readString(bb);
		data = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		bb.writeLong(uuid.getMostSignificantBits());
		bb.writeLong(uuid.getLeastSignificantBits());
		writeString(bb, username);
		writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{
		if(FTBU.proxy.getClientWorld() == null) return null;
		
		LMPlayer p = new LMPlayer(m.playerID, new GameProfile(m.uuid, m.username));
		LMPlayer.map.put(p.playerID, p);
		
		p.readFromNBT(m.data, false);
		FTBU.proxy.playerLMLoggedIn(p);
		return null;
	}
}