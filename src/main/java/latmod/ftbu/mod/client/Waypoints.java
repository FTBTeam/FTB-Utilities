package latmod.ftbu.mod.client;

import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;

public class Waypoints
{
	private static final ClientConfig config = new ClientConfig("waypoints");
	public static final ClientConfig.Property enabled = new ClientConfig.Property("enabled", 1, new String[] { "false", "true" });
	public static final ClientConfig.Property waypointType = new ClientConfig.Property("waypoint_type", 1, new String[] { "marker", "beacon" });
	public static final ClientConfig.Property displayTitle = new ClientConfig.Property("display_title", true);
	public static final ClientConfig.Property displayDist = new ClientConfig.Property("display_distance", false);
	public static final ClientConfig.Property renderDistance = new ClientConfig.Property("render_distance", 2, new String[] { "300", "600", "1200", "2500", "10000" }).setTranslateValues(false);
	public static final ClientConfig.Property deathPoint = new ClientConfig.Property("death_point", true);
	public static final FastList<Waypoint> waypoints = new FastList<Waypoint>();
	
	public static final double[] renderDistanceSq = { 300D * 300D, 600D * 600D, 1200D * 1200D, 2500D * 2500D, 10000D * 10000D };
	
	public static void init()
	{
		config.add(enabled);
		config.add(waypointType);
		config.add(displayTitle);
		config.add(displayDist);
		config.add(renderDistance);
		ClientConfig.Registry.add(config);
	}
	
	public static void load()
	{
		try
		{
			waypoints.clear();
			
			FastList<String> f = LatCore.loadFile(LatCore.newFile(new File(LatCoreMC.latmodFolder, "client/waypoints/" + LMWorld.getIDS() + ".txt")));
			
			for(String s : f)
			{
				if(s.isEmpty() || s.charAt(0) == '#') continue;
				
				String[] s1 = s.split(",");
				
				if(s1.length >= 8)
				{
					try
					{
						Waypoint w = new Waypoint(s1[0], Double.parseDouble(s1[1]), Double.parseDouble(s1[2]), Double.parseDouble(s1[3]), Integer.parseInt(s1[4]));
						w.colR = Integer.parseInt(s1[5]); w.colG = Integer.parseInt(s1[6]); w.colB = Integer.parseInt(s1[7]);
						waypoints.add(w);
					}
					catch(Exception e1)
					{ e1.printStackTrace(); }
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static int getFirstIndex(String s)
	{
		for(int i = 0; i < waypoints.size(); i++)
			if(waypoints.get(i).name.equals(s))
				return i;
		return -1;
	}
	
	public static void save()
	{
		try
		{
			FastList<String> f = new FastList<String>();
			f.add("#name,x,y,z,dim,red,green,blue");
			for(Waypoint w : waypoints)
				f.add(LatCore.unsplit(new Object[] { w.name, w.posX, w.posY, w.posZ, w.dim, w.colR, w.colG, w.colB }, ","));
			LatCore.saveFile(LatCore.newFile(new File(LatCoreMC.latmodFolder, "client/waypoints/" + LMWorld.getIDS() + ".txt")), f);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static class Waypoint
	{
		public String name;
		public boolean enabled;
		public final int posX, posY, posZ;
		public final int dim;
		public int colR, colG, colB;
		
		public Waypoint(String n, double x, double y, double z, int d)
		{
			name = n;
			enabled = true;
			posX = MathHelperLM.floor(x);
			posY = MathHelperLM.floor(y);
			posZ = MathHelperLM.floor(z);
			dim = d;
			colR = 0;
			colG = 148;
			colB = 255;
		}
		
		public String toString()
		{ return name; }
	}
}