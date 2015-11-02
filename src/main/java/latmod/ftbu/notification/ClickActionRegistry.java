package latmod.ftbu.notification;

import latmod.lib.*;

public class ClickActionRegistry
{
	private static final FastList<ClickAction> list = new FastList<ClickAction>();
	
	static
	{
		add(ClickAction.CMD);
		add(ClickAction.SHOW_CMD);
		add(ClickAction.URL);
		add(ClickAction.FILE);
		add(ClickAction.GUI);
		add(ClickAction.FRIEND_ADD);
		add(ClickAction.FRIEND_ADD_ALL);
	}
	
	public static void add(ClickAction a)
	{
		if(a != null && a.type != null)
		{
			String s = a.toString();
			if(LMStringUtils.isValid(s) && !list.contains(s))
				list.add(a);
		}
	}
	
	public static ClickAction get(String s)
	{ return list.getObj(s); }
}