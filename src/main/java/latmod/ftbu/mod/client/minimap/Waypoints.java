package latmod.ftbu.mod.client.minimap;

import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;

@SideOnly(Side.CLIENT)
public class Waypoints
{
	public static final ClientConfig clientConfig = new ClientConfig("waypoints");
	public static final ClientConfig.Property enabled = new ClientConfig.Property("enabled", 0, "false", "true");
	public static final ClientConfig.Property waypointType = new ClientConfig.Property("waypoint_type", 1, "marker", "beacon");
	public static final ClientConfig.Property displayTitle = new ClientConfig.Property("display_title", true);
	public static final ClientConfig.Property displayDist = new ClientConfig.Property("display_distance", false);
	public static final ClientConfig.Property renderDistance = new ClientConfig.Property("render_distance", 2, "300", "600", "1200", "2500", "10000").setRawValues();
	public static final ClientConfig.Property deathPoint = new ClientConfig.Property("death_point", true);
	private static final FastList<Waypoint> waypoints = new FastList<Waypoint>();
	
	public static final double[] renderDistanceSq = { 300D * 300D, 600D * 600D, 1200D * 1200D, 2500D * 2500D, 10000D * 10000D };
	private static File waypointsFile;
	
	public static void init()
	{
		clientConfig.add(enabled);
		//clientConfig.add(waypointType);
		clientConfig.add(displayTitle);
		clientConfig.add(displayDist);
		clientConfig.add(renderDistance);
		clientConfig.add(deathPoint);
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
			if(LMWorldClient.inst == null) return;
			waypointsFile = LMFileUtils.newFile(new File(LMWorldClient.inst.clientDataFolder, "waypoints.json"));
			WaypointsFile wf = LMJsonUtils.fromJsonFile(waypointsFile, WaypointsFile.class);
			if(wf != null) waypoints.addAll(wf.waypoints);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static void save()
	{
		try
		{
			if(LMWorldClient.inst == null) return;
			WaypointsFile wf = new WaypointsFile();
			wf.waypoints = new ArrayList<Waypoint>();
			wf.waypoints.addAll(waypoints);
			LMJsonUtils.toJsonFile(waypointsFile, wf);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}