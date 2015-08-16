package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.world.LMPlayerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class LMClientPlayer extends AbstractClientPlayer
{
	private static final ChunkCoordinates coords000 = new ChunkCoordinates(0, 0, 0);
	public final LMPlayerClient playerLM;
	
	public LMClientPlayer(LMPlayerClient p)
	{
		super(Minecraft.getMinecraft().theWorld, p.gameProfile);
		playerLM = p;
	}
	
	public void addChatMessage(IChatComponent i) { }
	
	public boolean canCommandSenderUseCommand(int i, String s)
	{ return false; }
	
	public ChunkCoordinates getPlayerCoordinates()
	{ return coords000; }
	
	public boolean isInvisibleToPlayer(EntityPlayer ep)
	{ return true; }
}