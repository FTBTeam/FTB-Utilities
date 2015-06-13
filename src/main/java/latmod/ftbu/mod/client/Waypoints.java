package latmod.ftbu.mod.client;

import java.io.File;
import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;

public class Waypoints
{
	private static final ClientConfig config = new ClientConfig("Waypoints");
	public static final ClientConfig.Property enabled = new ClientConfig.Property("Enabled", true);
	public static final ClientConfig.Property displayTitle = new ClientConfig.Property("DisplayTitle", true);
	public static final ClientConfig.Property displayDist = new ClientConfig.Property("DisplayDistance", false);
	public static final ClientConfig.Property renderDistance = new ClientConfig.Property("RenderDistance", 2, new String[] { "300", "600", "1200", "2500", "10000" });
	public static final FastList<Waypoint> waypoints = new FastList<Waypoint>();
	
	public static void init()
	{
		config.add(enabled);
		config.add(displayTitle);
		config.add(displayDist);
		config.add(renderDistance);
		ClientConfig.Registry.add(config);
	}
	
	public static void load(UUID uuid)
	{
		try
		{
			waypoints.clear();
			
			FastList<String> f = LatCore.loadFile(LatCore.newFile(new File(LatCoreMC.latmodFolder, "waypoints/" + LatCoreMC.toShortUUID(uuid) + ".txt")));
			
			for(String s : f)
			{
				String[] s1 = s.split(",");
				
				if(s1.length >= 8)
				{
					try
					{
						Waypoint w = new Waypoint(s1[0], Integer.parseInt(s1[1]), Integer.parseInt(s1[2]), Integer.parseInt(s1[3]), Integer.parseInt(s1[4]));
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
	
	public static void save(UUID uuid)
	{
		try
		{
			FastList<String> f = new FastList<String>();
			for(Waypoint w : waypoints)
				f.add(LatCore.unsplit(new Object[] { w.name, w.posX, w.posY, w.posZ, w.dim, w.colR, w.colG, w.colB }, ","));
			LatCore.saveFile(LatCore.newFile(new File(LatCoreMC.latmodFolder, "waypoints/" + LatCoreMC.toShortUUID(uuid) + ".txt")), f);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static class Waypoint
	{
		public final String name;
		public final int posX, posY, posZ, dim;
		public int colR, colG, colB;
		
		public Waypoint(String n, int x, int y, int z, int d)
		{
			name = n;
			posX = x;
			posY = y;
			posZ = z;
			dim = d;
			colR = 0;
			colG = 148;
			colB = 255;
		}
	}
}