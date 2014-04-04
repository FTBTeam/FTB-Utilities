package mods.lm.core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

public interface ISimpleInvOwner extends ITileInterface
{
	public void markDirty();
	public boolean isUseableByPlayer(EntityPlayer ep);
	public boolean isItemValidForSlot(int i, ItemStack is);
}