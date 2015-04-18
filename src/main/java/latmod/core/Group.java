package latmod.core;

import latmod.core.util.*;
import net.minecraft.nbt.*;

public class Group implements Comparable<Group>
{
	public static enum Status
	{
		NONE('-', 0),
		PENDING('P', 1),
		MEMBER('M', 5),
		ADMIN('A', 10),
		OWNER('O', 50);
		
		public final char ID;
		public final int power;
		
		Status(char c, int i)
		{ ID = c; power = i; }
		
		public boolean isAtLeast(Status s)
		{ return power >= s.power; }
	}
	
	public static int lastGroupID = 0;
	public static final FastList<Group> groups = new FastList<Group>();
	
	public final int groupID;
	public String title;
	public final FastMap<LMPlayer, Status> members;
	
	public Group(int id)
	{
		groupID = id;
		title = "Unnamed";
		members = new FastMap<LMPlayer, Status>();
	}
	
	public int hashCode()
	{ return groupID; }
	
	public String toString()
	{ return title; }
	
	public boolean equals(Object o)
	{
		if(o instanceof Number || o instanceof Group)
			return o.hashCode() == hashCode();
		return o.toString().equals(toString());
	}
	
	public int compareTo(Group o)
	{ return Integer.compare(groupID, o.groupID); }
	
	private void readFromNBT(NBTTagCompound tag)
	{
		title = tag.getString("Title");
	}
	
	private void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Title", title);
	}
	
	public static void readGroupsFromNBT(NBTTagList list)
	{
		groups.clear();
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			
			int gid = tag.getInteger("ID");
			
			if(gid > 0)
			{
				Group g = new Group(gid);
				g.readFromNBT(tag);
				groups.add(g);
			}
		}
	}
	
	public static NBTTagList writeGroupsToNBT()
	{
		NBTTagList list = new NBTTagList();
		
		for(Group g : groups)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			g.writeToNBT(tag1);
			tag1.setInteger("ID", g.groupID);
			list.appendTag(tag1);
		}
		
		return list;
	}
	
	public static Group getGroup(int id)
	{
		if(id < 1 || id >= lastGroupID) return null;
		return groups.get(id);
	}
	
	public static Group getGroup(String name)
	{
		if(name == null || name.isEmpty()) return null;
		return groups.getObj(name);
	}
	
	public static int getGroupID(String name)
	{
		Group g = getGroup(name);
		return (g == null) ? 0 : g.groupID;
	}
	
	public static FastList<Group> getMemberGroupsFor(Object o)
	{
		FastList<Group> l = new FastList<Group>();
		
		LMPlayer p = LMPlayer.getPlayer(o);
		if(p == null) return l;
		
		for(Group g : groups)
		{
			int i = g.members.keys.indexOf(o);
			if(i != -1 && g.members.values.get(i).isAtLeast(Status.MEMBER))
				l.add(g);
		}
		
		return l;
	}
	
	public Status getPlayerStatus(LMPlayer p)
	{
		if(p == null) return Status.NONE;
		for(int i = 0; i < members.size(); i++)
		{ if(p.equals(members.keys.get(i))) return members.values.get(i); }
		return Status.NONE;
	}
	
	public boolean isPlayerInGroup(Object o)
	{ return getPlayerStatus(LMPlayer.getPlayer(o)) != Status.NONE; }
	
	public static FastList<Group> getAllGroups(LMPlayer p)
	{
		FastList<Group> l = new FastList<Group>();
		if(p == null) l.addAll(groups);
		else for(Group g : groups) { if(g.isPlayerInGroup(p)) l.add(g); }
		return l;
	}
	
	public static String[] getAllGroupNames(LMPlayer p)
	{
		FastList<Group> gl = getAllGroups(p);
		String[] l = new String[gl.size()];
		for(int i = 0; i < l.length; i++) l[i] = gl.get(i).title;
		return l;
	}
}