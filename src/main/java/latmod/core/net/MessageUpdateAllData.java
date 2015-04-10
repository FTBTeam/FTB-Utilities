package latmod.core.net;
import latmod.core.IServerConfig;
import latmod.core.mod.LMDataLoader;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateAllData extends MessageLM<MessageUpdateAllData>
{
	public MessageUpdateAllData()
	{
		data = new NBTTagCompound();
		
		NBTTagCompound players = new NBTTagCompound();
		LMDataLoader.writePlayersToNBT(players, false);
		data.setTag("P", players);
		
		NBTTagCompound config = new NBTTagCompound();
		IServerConfig.Registry.writeToNBT(config);
		data.setTag("C", config);
	}
	
	public void onMessage(MessageContext ctx)
	{
		LMDataLoader.readPlayersFromNBT(data.getCompoundTag("P"), false);
		IServerConfig.Registry.readFromNBT(data.getCompoundTag("C"));
	}
}