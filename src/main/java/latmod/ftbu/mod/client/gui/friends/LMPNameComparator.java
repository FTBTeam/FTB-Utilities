package latmod.ftbu.mod.client.gui.friends;

import java.util.Comparator;

import latmod.ftbu.core.world.LMPlayerClient;

public class LMPNameComparator implements Comparator<LMPlayerClient>
{
	public static final LMPNameComparator instance = new LMPNameComparator();
	
	public int compare(LMPlayerClient o1, LMPlayerClient o2)
	{
		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}