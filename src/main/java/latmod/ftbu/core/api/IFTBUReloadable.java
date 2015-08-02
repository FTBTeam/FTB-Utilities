package latmod.ftbu.core.api;

import net.minecraft.command.ICommandSender;
import cpw.mods.fml.relauncher.Side;

public interface IFTBUReloadable
{
	public void onReloaded(Side s, ICommandSender sender) throws Exception;
}