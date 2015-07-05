package latmod.ftbu.mod.client.minimap;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorld;

public class Waypoints
{
	public static final ClientConfig config = new ClientConfig("waypoints");
	public static final ClientConfig.Property enabled = new ClientConfig.Property("enabled", 0, new String[] { "false", "true" });
	public static final ClientConfig.Property waypointType = new ClientConfig.Property("waypoint_type", 1, new String[] { "marker", "beacon" });
	public static final ClientConfig.Property displayTitle = new ClientConfig.Property("display_title", true);
	public static final ClientConfig.Property displayDist = new ClientConfig.Property("display_distance", false);
	public static final ClientConfig.Property renderDistance = new ClientConfig.Property("render_distance", 2, new String[] { "300", "600", "1200", "2500", "10000" }).setTranslateValues(false);
	public static final ClientConfig.Property deathPoint = new ClientConfig.Property("death_point", true);
	private static final FastList<Waypoint> waypoints = new FastList<Waypoint>();
	
	public static final double[] renderDistanceSq = { 300D * 300D, 600D * 600D, 1200D * 1200D, 2500D * 2500D, 10000D * 10000D };
	private static File waypointsFile;
	
	public static void init()
	{
		config.add(enabled);
		config.add(displayTitle);
		config.add(displayDist);
		config.add(renderDistance);
		config.add(deathPoint);
		ClientConfig.Registry.add(config);
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