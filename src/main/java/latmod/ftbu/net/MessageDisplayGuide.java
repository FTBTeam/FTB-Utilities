package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.LMNetworkWrapper;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.guide.GuideFile;
import latmod.ftbu.mod.client.gui.guide.GuiGuide;
import net.minecraft.nbt.NBTTagCompound;

public class MessageDisplayGuide extends MessageFTBU
{
	public MessageDisplayGuide() { super(DATA_LONG); }
	
	public MessageDisplayGuide(GuideFile file)
	{
		this();
		NBTTagCompound tag = new NBTTagCompound();
		file.main.writeToNBT(tag);
		writeTag(tag);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		NBTTagCompound data = readTag();
		GuideFile file = new GuideFile(null);
		file.main.readFromNBT(data);
		FTBLibClient.mc.displayGuiScreen(new GuiGuide(null, file.main));
		return null;
	}
}