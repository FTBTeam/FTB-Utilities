package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.util.*;

public class Waypoint
{
	public String name, customIcon;
	public boolean enabled = true, isMarker = false;
	public int posX, posY, posZ, dim, colR, colG, colB;
	public int listID = -1;
	
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
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("N=");
		sb.append(name);
		sb.append(", ");
		
		sb.append("E=");
		sb.append(enabled ? '1' : '0');
		sb.append(", ");
		
		sb.append("X=");
		sb.append(posX);
		sb.append(", ");
		
		sb.append("Y=");
		sb.append(posY);
		sb.append(", ");
		
		sb.append("Z=");
		sb.append(posZ);
		sb.append(", ");
		
		sb.append("D=");
		sb.append(dim);
		sb.append(", ");
		
		sb.append("C=");
		sb.append(LatCore.Colors.getHex(getColorRGB()));
		sb.append(", ");
		
		sb.append("M=");
		sb.append(isMarker ? '1' : '0');
		
		if(customIcon != null)
		{
			sb.append(", ");
			sb.append("I=");
			sb.append(customIcon);
		}
		
		return sb.toString();
	}
	
	public boolean fromString(String s)
	{
		String[] s1 = s.split(", ");
		FastMap<String, String> map = new FastMap<String, String>();
		
		for(String s2 : s1)
		{
			String[] s3 = s2.split("=");
			if(s3.length == 2) map.put(s3[0], s3[1]);
		}
		
		if(map.isEmpty()) return false;
		
		name = map.get("N");
		posX = Integer.parseInt(map.get("X"));
		posY = Integer.parseInt(map.get("Y"));
		posZ = Integer.parseInt(map.get("Z"));
		dim = Integer.parseInt(map.get("D"));
		setColor(Integer.decode(map.get("C")));
		enabled = map.get("E").equals("1");
		isMarker = map.get("M").equals("1");
		customIcon = map.get("I");
		return true;
	}
	
	public int hashCode()
	{ return listID; }

	public int getColorRGB()
	{ return LatCore.Colors.getRGBA(colR, colG, colB, 255); }
}