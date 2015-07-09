package latmod.ftbu.mod.client.minimap;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorld;

public class Waypoints
{
	public static final ClientConfig clientConfig = new ClientConfig("waypoints");
	public static final ClientConfig.Property enabled = new ClientConfig.Property(clientConfig, "enabled", 0, "false", "true");
	public static final ClientConfig.Property waypointType = new ClientConfig.Property(clientConfig, "waypoint_type", 1, "marker", "beacon");
	public static final ClientConfig.Property displayTitle = new ClientConfig.Property(clientConfig, "display_title", true);
	public static final ClientConfig.Property displayDist = new ClientConfig.Property(clientConfig, "display_distance", false);
	public static final ClientConfig.Property renderDistance = new ClientConfig.Property(clientConfig, "render_distance", 2, "300", "600", "1200", "2500", "10000").setTranslateValues(false);
	public static final ClientConfig.Property deathPoint = new ClientConfig.Property(clientConfig, "death_point", true);
	private static final FastList<Waypoint> waypoints = new FastList<Waypoint>();
	
	public static final double[] renderDistanceSq = { 300D * 300D, 600D * 600D, 1200D * 1200D, 2500D * 2500D, 10000D * 10000D };
	private static File waypointsFile;
	
	public static void init()
	{
		ClientConfig.Registry.add(clientConfig);
	}
	
	public static boolean hasWaypoints()
	{ return !waypoints.isEmpty(); }
	
	public static FastList<Waypoint> getAll()
	{ return waypoints; }
	
	public static void add(Waypoint w)
	{
		waypoints.add(w);
		for(int i = 0; i < waypoints.size(); i++)
			waypoints.get(i).listID = i;
		save();
	}
	
	public static void remove(int index)
	{
		waypoints.remove(index);
		for(int i = 0; i < waypoints.size(); i++)
			waypoints.get(i).listID = i;
		save();
	}
	
	public static void load()
	{
		try
		{
			waypoints.clear();
			waypointsFile = LatCore.newFile(new File(LatCoreMC.latmodFolder, "client/" + LMWorld.client.worldIDS + "/waypoints.txt"));
			FastList<String> f = LatCore.loadFile(waypointsFile);
			
			for(String s : f)
			{
				if(s.isEmpty()) continue;
				Waypoint w = new Waypoint();
				try { if(w.fromString(s)) add(w); }
				catch(Exception e1) { e1.printStackTrace(); }
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static void save()
	{
		try
		{
			FastList<String> f = new FastList<String>();
			for(Waypoint w : waypoints) f.add(w.toString());
			LatCore.saveFile(waypointsFile, f);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}