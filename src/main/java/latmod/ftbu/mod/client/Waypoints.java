package latmod.ftbu.mod.client;

import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;

public class Waypoints
{
	public static final ClientConfig config = new ClientConfig("waypoints");
	public static final ClientConfig.Property enabled = new ClientConfig.Property("enabled", 1, new String[] { "false", "true" });
	public static final ClientConfig.Property waypointType = new ClientConfig.Property("waypoint_type", 1, new String[] { "marker", "beacon" });
	public static final ClientConfig.Property displayTitle = new ClientConfig.Property("display_title", true);
	public static final ClientConfig.Property displayDist = new ClientConfig.Property("display_distance", false);
	public static final ClientConfig.Property renderDistance = new ClientConfig.Property("render_distance", 2, new String[] { "300", "600", "1200", "2500", "10000" }).setTranslateValues(false);
	public static final ClientConfig.Property deathPoint = new ClientConfig.Property("death_point", true);
	private static final FastList<Waypoint> waypoints = new FastList<Waypoint>();
	
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
			
			FastList<String> f = LatCore.loadFile(LatCore.newFile(new File(LatCoreMC.latmodFolder, "client/waypoints/" + LMWorld.getIDS() + ".txt")));
			
			for(String s : f)
			{
				if(s.isEmpty() || s.charAt(0) == '#') continue;
				
				String[] s1 = s.split(",");
				
				if(s1.length >= 8)
				{
					try
					{
						Waypoint w = new Waypoint(s1[0], Integer.parseInt(s1[4]));
						w.setPos(Double.parseDouble(s1[1]), Double.parseDouble(s1[2]), Double.parseDouble(s1[3]));
						w.setColor(Integer.parseInt(s1[5]), Integer.parseInt(s1[6]), Integer.parseInt(s1[7]));
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
	
	public static FastList<Waypoint> getAll()
	{ return waypoints; }
	
	public static FastList<Waypoint> getForDim(int dim)
	{
		FastList<Waypoint> l = new FastList<Waypoint>();
		for(Waypoint w : waypoints)
			if(w.dim == dim) l.add(w);
		return l;
	}
	
	public static String[] getAllNames(FastList<Waypoint> l)
	{
		String[] s = new String[l.size()];
		for(int i = 0; i < l.size(); i++) s[i] = l.get(i).name;
		return s;
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
		public int posX, posY, posZ;
		public final int dim;
		public int colR, colG, colB;
		
		public int listID = -1;
		
		public Waypoint(String s, int d)
		{
			name = s;
			enabled = true;
			dim = d;
			
		}
		
		public void setPos(double x, double y, double z)
		{
			posX = MathHelperLM.floor(x);
			posY = MathHelperLM.floor(y);
			posZ = MathHelperLM.floor(z);
		}
		
		public void setColor(int r, int g, int b)
		{ colR = r; colG = g; colB = b; }
		
		public void setColor(int col)
		{ setColor(LatCore.Colors.getRed(col), LatCore.Colors.getGreen(col), LatCore.Colors.getBlue(col)); }
		
		public String toString()
		{ return name; }
		
		public int hashCode()
		{ return listID; }

		public int getColorRGB()
		{ return LatCore.Colors.getRGB(colR, colG, colB, 255); }
	}
}