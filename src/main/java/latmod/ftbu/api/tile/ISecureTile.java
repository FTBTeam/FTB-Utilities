package latmod.ftbu.api.tile;

import net.minecraft.entity.player.EntityPlayer;

public interface ISecureTile
{
	public boolean canPlayerInteract(EntityPlayer ep, boolean breakBlock);
	public void onPlayerNotOwner(EntityPlayer ep, boolean breakBlock);
}