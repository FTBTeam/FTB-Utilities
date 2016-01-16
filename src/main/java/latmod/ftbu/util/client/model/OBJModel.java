package latmod.ftbu.util.client.model;

import ftb.lib.FTBLib;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.util.*;

/**
 * Made by LatvianModder
 */
@SideOnly(Side.CLIENT)
public class OBJModel
{
	public final List<Face> totalFaces;
	public final List<Vector3f> vertices;
	public final List<Vector3f> vertexNormals;
	public final List<Vector3f> texVertices;
	public final HashMap<String, Group> groups;
	
	private Group current = null;
	public double sizeV = 0D;
	
	protected OBJModel()
	{
		totalFaces = new ArrayList<>();
		vertices = new ArrayList<>();
		vertexNormals = new ArrayList<>();
		texVertices = new ArrayList<>();
		groups = new HashMap<>();
	}
	
	public static OBJModel load(ResourceLocation rl)
	{
		try { return OBJModel.load(OBJModel.class.getResourceAsStream(FTBLib.getPath(rl))); }
		catch(Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static OBJModel load(InputStream is) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		OBJModel m = new OBJModel();
		
		if(br.ready())
		{
			double minSizeV = Double.POSITIVE_INFINITY;
			double maxSizeV = Double.NEGATIVE_INFINITY;
			
			String s = null;
			while((s = br.readLine()) != null)
			{
				if(s.length() > 0 && s.charAt(0) != '#')
				{
					String[] s3 = s.split(" ");
					
					if(s3[0].equals("o"))
					{
						Group g = new Group(m, s3[1]);
						if(m.current != null) m.groups.put(m.current.groupName, m.current);
						m.current = g;
					}
					else if(s3[0].equals("g"))
					{
					}
					else if(s3[0].equals("s"))
					{
					}
					else if(s3[0].equals("mtllib"))
					{
					}
					else if(s3[0].equals("usemtl"))
					{
					}
					else if(s3[0].equals("v"))
					{
						float x = Float.parseFloat(s3[1]);
						float y = Float.parseFloat(s3[2]);
						float z = Float.parseFloat(s3[3]);
						m.vertices.add(new Vector3f(x, y, z));
						
						if(y < minSizeV) minSizeV = y;
						if(y > maxSizeV) maxSizeV = y;
					}
					else if(s3[0].equals("vn"))
					{
						float x = Float.parseFloat(s3[1]);
						float y = Float.parseFloat(s3[2]);
						float z = Float.parseFloat(s3[3]);
						m.vertexNormals.add(new Vector3f(x, y, z));
					}
					else if(s3[0].equals("vt"))
					{
						if(s3.length == 3)
						{
							float x = Float.parseFloat(s3[1]);
							float y = Float.parseFloat(s3[2]);
							m.texVertices.add(new Vector3f(x, 1F - y, -1F));
						}
						else if(s3.length == 4)
						{
							float x = Float.parseFloat(s3[1]);
							float y = Float.parseFloat(s3[2]);
							float z = Float.parseFloat(s3[3]);
							m.texVertices.add(new Vector3f(x, 1F - y, z));
						}
					}
					else if(s3[0].equals("f"))
					{
						if(m.current == null) m.current = new Group(m, "Default");
						
						Face f = Face.parseFace(m, s, s3);
						if(f != null)
						{
							m.current.faces.add(f);
							m.totalFaces.add(f);
						}
					}
				}
			}
			
			//if(!m.groups.contains(m.current));
			m.groups.put(m.current.groupName, m.current);
			m.sizeV = maxSizeV - minSizeV;
		}
		
		if(m != null && m.texVertices != null) GlStateManager.enableTexture2D();
		else GlStateManager.disableTexture2D();
		
		m.renderAll();
		
		return m;
	}
	
	public void renderAll()
	{
		for(int i = 0; i < groups.size(); i++)
			groups.get(i).render();
	}
	
	public void render(int... index)
	{
		for(int i = 0; i < index.length; i++)
		{
			if(index[i] >= 0 && index[i] < groups.size()) groups.get(index[i]).render();
		}
	}
	
	public void renderAllExcept(int... index)
	{
		for(int i = 0; i < groups.size(); i++)
		{
			for(int j = 0; j < index.length; j++)
				if(i == index[j]) continue;
			groups.get(i).render();
		}
	}
	
	public void render(String... name)
	{
		for(int i = 0; i < name.length; i++)
		{
			int index = getGroupIndex(name[i]);
			if(index >= 0 && index < groups.size()) groups.get(index).render();
		}
	}
	
	public int getGroupIndex(String s)
	{
		for(int i = 0; i < groups.size(); i++)
		{
			if(groups.get(i).groupName.equalsIgnoreCase(s)) return i;
		}
		return -1;
	}
	
	public OBJModel copy(boolean render)
	{
		OBJModel m = new OBJModel();
		m.vertices.addAll(vertices);
		//if(texVertices.size() > 0)
		m.texVertices.addAll(texVertices);
		//if(vertexNormals.size() > 0)
		m.vertexNormals.addAll(vertexNormals);
		m.groups.putAll(groups);
		m.totalFaces.addAll(totalFaces);
		m.sizeV = sizeV;
		
		if(render)
		{
			if(m != null && m.texVertices != null) GlStateManager.enableTexture2D();
			else GlStateManager.disableTexture2D();
			
			m.renderAll();
		}
		
		return m;
	}
}