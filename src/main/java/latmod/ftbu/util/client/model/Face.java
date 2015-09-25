package latmod.ftbu.util.client.model;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

/** Made by LatvianModder */
@SideOnly(Side.CLIENT)
public class Face
{
	public int[] verticies = null;
	public int[] texVerticies = null;
	public int[] normals = null;
	public int drawMode = -1;
	
	private Face(int i, boolean tex, boolean norm)
	{
		verticies = new int[i];
		
		if(tex) texVerticies = new int[i];
		if(norm) normals = new int[i];
		
		if(i == 3) drawMode = GL11.GL_TRIANGLES;
		else if(i == 4) drawMode = GL11.GL_QUADS;
		else drawMode = GL11.GL_POLYGON;
	}
	
	public static final Face parseFace(OBJModel m, String line, String[] s1)
	{
		String[] s = new String[s1.length - 1];
		for(int i = 0; i < s.length; i++)
		s[i] = s1[i + 1];
		
		if (isValidFace_V_VN_Line(line))
		{
			Face face = new Face(s.length, false, true);
			
			for (int i = 0; i < s.length; ++i)
			{
				String s2[] = s[i].split("//");
				face.verticies[i] = Integer.parseInt(s2[0]) - 1;
				face.normals[i] = Integer.parseInt(s2[1]) - 1;
			}
			
			return face;
		}
		
		else if (isValidFace_V_VT_VN_Line(line))
		{
			Face face = new Face(s.length, true, true);
			
			for (int i = 0; i < s.length; ++i)
			{
				String s2[] = s[i].split("/");
				face.verticies[i] = Integer.parseInt(s2[0]) - 1;
				face.texVerticies[i] = Integer.parseInt(s2[1]) - 1;
				face.normals[i] = Integer.parseInt(s2[2]) - 1;
			}
			
			return face;
		}
		else if (isValidFace_V_VT_Line(line))
		{
			Face face = new Face(s.length, true, false);
			
			for (int i = 0; i < s.length; ++i)
			{
				String s2[] = s[i].split("/");
				face.verticies[i] = Integer.parseInt(s2[0]) - 1;
				face.texVerticies[i] = Integer.parseInt(s2[1]) - 1;
			}
			
			return face;
		}
		else if (isValidFace_V_Line(line))
		{
			Face face = new Face(s.length, false, false);
			
			for (int i = 0; i < s.length; ++i)
			face.verticies[i] = Integer.parseInt(s[i]) - 1;
			
			return face;
		}
		
		System.err.println("[LM_Obj_Loader] Unknown face format: " + line);
		return null;
	}
	
	/** f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ... */
	private static boolean isValidFace_V_VT_VN_Line(String line)
	{
		if(!line.contains("/") || !line.contains(" ")) return false;
		
		String[] s = line.split(" ");
		if(s != null && s.length > 1)
		{
			String[] s1 = s[1].split("/");
			if(s1 != null && s1.length == 3) return true;
		}
		
		return false;
	}
	
	/** f v1/vt1 v2/vt2 v3/vt3 ... */
	private static boolean isValidFace_V_VT_Line(String line)
	{
		if(!line.contains("/") || !line.contains(" ")) return false;
		
		String[] s = line.split(" ");
		if(s != null && s.length > 1)
		{
			String[] s1 = s[1].split("/");
			if(s1 != null && s1.length == 2) return true;
		}
		
		return false;
	}
	
	/** f v1//vn1 v2//vn2 v3//vn3 ... */
	private static boolean isValidFace_V_VN_Line(String line)
	{ return line.contains("//") && line.contains(" "); }
	
	/** f v1 v2 v3 ... */
	private static boolean isValidFace_V_Line(String line)
	{ return line.contains(" "); }
}