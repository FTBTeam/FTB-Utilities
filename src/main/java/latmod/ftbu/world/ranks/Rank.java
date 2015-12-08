package latmod.ftbu.world.ranks;

import latmod.lib.util.FinalIDObject;
import net.minecraft.util.EnumChatFormatting;

public class Rank extends FinalIDObject
{
	public Rank parent = null;
	public EnumChatFormatting color = null;
	public String prefix = null;
	public final RankConfig config;
	
	public Rank(String id)
	{
		super(id);
		config = new RankConfig();
	}
}