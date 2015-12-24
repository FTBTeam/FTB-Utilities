package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.world.LMPlayer;

import java.util.Comparator;

public class LMPNameComparator implements Comparator<LMPlayer>
{
	public static final LMPNameComparator instance = new LMPNameComparator();
	
	public int compare(LMPlayer o1, LMPlayer o2)
	{
		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}