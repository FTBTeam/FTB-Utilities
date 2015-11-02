package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldJoined extends MessageFTBU
{
	public MessageLMWorldJoined() { super(DATA_LONG); }
	
	public MessageLMWorldJoined(int p)
	{
		this();
		io.writeInt(p);
		NBTTagCompound data = new NBTTagCompound();
		LMWorldServer.inst.writeDataToNet(data, p);
		writeTag(data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMWorldClient.inst = new LMWorldClient(io.readInt());
		LMWorldClient.inst.readDataFromNet(readTag(), true);
		FTBLib.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.getClientPlayer().playerID + " on world " + FTBWorld.client.getWorldIDS());
		FTBUClient.onWorldJoined();
		new EventLMWorldClient.Joined(LMWorldClient.inst).post();
		return null;
	}
}