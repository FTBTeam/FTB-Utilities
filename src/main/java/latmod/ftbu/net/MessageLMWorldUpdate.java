package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldUpdate extends MessageFTBU
{
	public MessageLMWorldUpdate() { super(DATA_LONG); }
	
	public MessageLMWorldUpdate(LMWorldServer w)
	{
		this();
		NBTTagCompound data = new NBTTagCompound();
		w.writeDataToNet(data, 0);
		writeTag(data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMWorldClient.inst.readDataFromNet(readTag(), false);
		return null;
	}
}