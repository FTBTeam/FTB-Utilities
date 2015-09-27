package latmod.ftbu.net;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
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
	
	public void readData(ByteIOStream io) throws Exception
	{
		playerID = io.readInt();
		uuid = io.readUUID();
		username = io.readString();
		data = LMNetHelper.readTagCompound(io);
		firstTime = io.readBoolean();
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeInt(playerID);
		io.writeUUID(uuid);
		io.writeString(username);
		LMNetHelper.writeTagCompound(io, data);
		io.writeBoolean(firstTime);
	}
	
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