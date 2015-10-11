package latmod.ftbu.net;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerLoggedIn extends MessageLM<MessageLMPlayerLoggedIn>
{
	public int playerID;
	public UUID uuid;
	public String username;
	public NBTTagCompound data;
	public boolean firstTime;
	
	public MessageLMPlayerLoggedIn() { }
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean first, boolean self)
	{
		playerID = p.playerID;
		uuid = p.getUUID();
		username = p.getName();
		
		data = new NBTTagCompound();
		p.writeToNet(data, self);
		
		firstTime = first;
	}
	
	public void fromBytes(ByteBuf io)
	{
		playerID = io.readInt();
		long msb = io.readLong();
		long lsb = io.readLong();
		uuid = new UUID(msb, lsb);
		username = ByteBufUtils.readUTF8String(io);
		data = ByteBufUtils.readTag(io);
		firstTime = io.readBoolean();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(playerID);
		io.writeLong(uuid.getMostSignificantBits());
		io.writeLong(uuid.getLeastSignificantBits());
		ByteBufUtils.writeUTF8String(io, username);
		ByteBufUtils.writeTag(io, data);
		io.writeBoolean(firstTime);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(LMWorldClient.inst, m.playerID, new GameProfile(m.uuid, m.username));
		p.readFromNet(m.data, p.playerID == LMWorldClient.inst.clientPlayerID);
		if(add) LMWorldClient.inst.players.add(p);
		new EventLMPlayerClient.DataLoaded(p).post();
		new EventLMPlayerClient.LoggedIn(p, m.firstTime).post();
		return null;
	}
}