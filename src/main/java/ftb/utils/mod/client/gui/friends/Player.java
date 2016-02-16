package ftb.utils.mod.client.gui.friends;

import ftb.lib.api.friends.LMPlayerSP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class Player extends AbstractClientPlayer
{
	public final LMPlayerSP playerLM;
	public final boolean isOwner;
	
	public Player(LMPlayerSP p)
	{
		super(Minecraft.getMinecraft().theWorld, p.getProfile());
		playerLM = p;
		isOwner = playerLM.getProfile().equals(Minecraft.getMinecraft().thePlayer.getGameProfile());
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Player) return playerLM.equalsPlayer(((Player) o).playerLM);
		return playerLM.equals(o);
	}
	
	public void addChatMessage(IChatComponent i) { }
	
	public boolean canCommandSenderUseCommand(int i, String s)
	{ return false; }
	
	public BlockPos getPlayerCoordinates()
	{ return new BlockPos(0, 0, 0); }
	
	public boolean isInvisibleToPlayer(EntityPlayer ep)
	{ return true; }
	
	public ResourceLocation getLocationSkin()
	{ return playerLM.getSkin(); }
	
	public boolean func_152122_n()
	{ return false; }
	
	public ResourceLocation getLocationCape()
	{ return null; }
}