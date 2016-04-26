package ftb.utils.net;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM_IO;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldClient;
import latmod.lib.ByteCount;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class MessageLMPlayerLoggedIn extends MessageLM_IO
{
	public MessageLMPlayerLoggedIn() { super(ByteCount.INT); }
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean first, boolean self)
	{
		this();
		
		io.writeUUID(p.getProfile().getId());
		io.writeUTF(p.getProfile().getName());
		io.writeBoolean(first);
		NBTTagCompound tag = new NBTTagCompound();
		p.writeToNet(tag, self);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		
		UUID uuid = io.readUUID();
		String username = io.readUTF();
		boolean firstTime = io.readBoolean();
		boolean isSelf = uuid.equals(LMWorldClient.inst.clientPlayer.getProfile().getId());
		
		LMPlayerClient p = LMWorldClient.inst.getPlayer(uuid);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(new GameProfile(uuid, username));
		p.readFromNet(readTag(), isSelf);
		LMWorldClient.inst.playerMap.put(uuid, p);
		
		//if(isSelf)
		new EventLMPlayerClient.LoggedIn(p, firstTime).post();
		
		new EventLMPlayerClient.DataLoaded(p).post();
		return null;
	}
}