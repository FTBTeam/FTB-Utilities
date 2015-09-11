package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.world.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class Player extends AbstractClientPlayer
{
	private static final ChunkCoordinates coords000 = new ChunkCoordinates(0, 0, 0);
	
	public final LMPlayerClient playerLM;
	public final boolean isOwner;
	
	public Player(LMPlayerClient p)
	{
		super(Minecraft.getMinecraft().theWorld, p.gameProfile);
		playerLM = p;
		isOwner = playerLM.playerID == LMWorldClient.inst.clientPlayerID;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Player)
			return playerLM.equalsPlayer(((Player)o).playerLM);
		return playerLM.equals(o);
	}
	
	public void addChatMessage(IChatComponent i) { }
	
	public boolean canCommandSenderUseCommand(int i, String s)
	{ return false; }
	
	public ChunkCoordinates getPlayerCoordinates()
	{ return coords000; }
	
	public boolean isInvisibleToPlayer(EntityPlayer ep)
	{ return true; }
}