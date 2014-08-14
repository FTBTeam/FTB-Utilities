package latmod.core.security;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.gson.annotations.Expose;

public class JsonPlayer
{
	@Expose public String displayName;
	@Expose public String uuid;
	@Expose public List<String> whitelist;
	@Expose public List<String> blacklist;
	
	public UUID getUUID()
	{ return UUID.fromString(uuid); }
	
	public EntityPlayer getPlayer(World w)
	{ return w.func_152378_a(getUUID()); }
}