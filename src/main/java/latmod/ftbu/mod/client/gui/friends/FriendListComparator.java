package latmod.ftbu.mod.client.gui.friends;

import java.util.Comparator;

import latmod.ftbu.world.FriendStatus;
import latmod.ftbu.world.LMPlayer;
import latmod.ftbu.world.LMPlayerClient;
import latmod.ftbu.world.LMWorldClient;

public class FriendListComparator implements Comparator<LMPlayer>
{
	public static final FriendListComparator instance = new FriendListComparator();
	
	public int compare(LMPlayer p1, LMPlayer p2)
	{
		LMPlayerClient self = LMWorldClient.inst.clientPlayer;
		int output = 0;
		
		FriendStatus f1 = FriendStatus.get(self, p1);
		FriendStatus f2 = FriendStatus.get(self, p2);
		
		boolean o1 = p1.isOnline();
		boolean o2 = p2.isOnline();
		
		if (f1 == f2)
			output = 0;
		else
		{
			if (f1 == FriendStatus.FRIEND)
				output = -1;
			else if (f2 == FriendStatus.FRIEND)
				output = 1;
			else
			{
				if (f1 == FriendStatus.NONE)
					return 1;
				else if (f2 == FriendStatus.NONE)
					return -1;
			}
		}
	
		if (output == 0)
		{
			if (o1 && !o2)
				output = -1;
			else if (!o1 && o2)
				output = 1;
			else if ((o1 && o2) || (!o1 && !o2))
				output = 0;
			
			if (output == 0)
				output = p1.getName().compareToIgnoreCase(p2.getName());
		}
		
		return output;
	}
}