package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.world.*;

import java.util.Comparator;

public class LMPStatusComparator implements Comparator<LMPlayer>
{
	public static final LMPStatusComparator instance = new LMPStatusComparator();
	
	public LMPlayerClient self = null;
	
	public int compare(LMPlayer p1, LMPlayer p2)
	{
		int output = 0;
		
		FriendStatus f1 = FriendStatus.get(self, p1);
		FriendStatus f2 = FriendStatus.get(self, p2);
		
		boolean o1 = p1.isOnline();
		boolean o2 = p2.isOnline();
		
		if(f1 == f2) output = 0;
		else
		{
			if(f1 == FriendStatus.FRIEND) output = -1;
			else if(f2 == FriendStatus.FRIEND) output = 1;
			else
			{
				if(f1 == FriendStatus.NONE) return 1;
				else if(f2 == FriendStatus.NONE) return -1;
			}
		}
		
		if(output == 0)
		{
			if(o1 && !o2) output = -1;
			else if (!o1 && o2) output = 1;
			else if ((o1 && o2) || (!o1 && !o2)) output = 0;
			if (output == 0)
				output = p1.getName().compareToIgnoreCase(p2.getName());
		}
		
		return output;
	}
}