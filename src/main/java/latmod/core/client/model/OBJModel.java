package latmod.core.client.model;
import java.io.*;

import latmod.core.*;
import latmod.core.client.LMRenderHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.*;

/** Made by LatvianModder */
@SideOnly(Side.CLIENT)
public class OBJModel
{
	public FastList<Face> totalFaces;
	public FastList<Vector3f> vertices;
	public FastList<Vector3f> vertexNormals;
	public FastList<Vector3f> texVertices;
	public FastList<Group> groups;
	private Group current = null;
	public String[] groupNames = null;
	public double sizeV = 0D;
	public FastMap<Integer, Group> groupIDMap;
	
	protected OBJModel()
	{
		totalFaces = new FastList<Face>();
		vertices = new FastList<Vector3f>();
		vertexNormals = new FastList<Vector3f>();
		texVertices = new FastList<Vector3f>();
		groups = new FastList<Group>();
		groupIDMap = new FastMap<Integer, Group>();
	}
	
	public static OBJModel load(ResourceLocation rl)
	{
		try { return OBJModel.load(OBJModel.class.getResourceAsStream(LatCoreMC.getPath(rl))); }
		catch(Exception e) { e.printStackTrace(); } return null;
	}
	
	public static OBJModel load(InputStream is) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		OBJModel m = new OBJModel();
		
		if(br.ready())
		{
			double minSizeV = Double.POSITIVE_INFINITY;
			double maxSizeV = Double.NEGATIVE_INFINITY;
			
			FastList<String> gnames = new FastList<String>();
			
			String s = null;
			while((s = br.readLine()) != null)
			{
				if(s.length() > 0 && s.charAt(0) != '#')
				{
					String[] s3 = LatCore.split(s, " ");
					
					if(s3[0].equals("o"))
					{
						Group g = new Group(m, s3[1]);
						if(m.current != null)
						m.groups.add(m.current);
						m.current = g;
						gnames.add(g.groupName);
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
						if(m.current == null)
						m.current = new Group(m, "Default");
						
						Face f = Face.parseFace(m, s, s3);
						if(f != null) { m.current.faces.add(f); m.totalFaces.add(f); }
					}
				}
			}
			
			//if(!m.groups.contains(m.current));
			m.groups.add(m.current);
			m.groupNames = gnames.toArray(new String[]{});
			m.sizeV = maxSizeV - minSizeV;
		}
		
		if(m != null && m.texVertices != null)
			LMRenderHelper.enableTexture();
		else LMRenderHelper.disableTexture();
		
		m.renderAll();
		
		return m;
	}

	public void renderAll()
	{
		for(int i = 0; i < groups.size(); i++)
		groups.get(i).render();
	}
	
	public void render(String... s)
	{
		if(s == null || s.length == 0) throw new RuntimeException("Can't render no faces!");
		for(int i = 0; i < groups.size(); i++)
		{
			Group g = groups.get(i);
			
			if(s.length > 1)
			{
				for(int j = 0; j < s.length; j++)
				if(s[j].equalsIgnoreCase(g.groupName))
				g.render();
			}
			else { if(s[0].equalsIgnoreCase(g.groupName))
			g.render(); }
		}
	}
	
	public void render(int... index)
	{
		for(int i = 0; i < index.length; i++)
		{
			if(index[i] >= 0 && index[i] < groups.size())
			groups.get(index[i]).render();
		}
	}
	
	public void renderAllExcept(String... s)
	{
		for(int i = 0; i < groups.size(); i++)
		{
			Group g = groups.get(i);
			for(int j = 0; j < s.length; j++)
			if(!s[j].equalsIgnoreCase(g.groupName))
			g.render();
		}
	}
	
	public void renderAllExcept(int... index)
	{
		for(int i = 0; i < groups.size(); i++)
		{
			Group g = groups.get(i);
			boolean render = true;
			
			for(int j = 0; j < index.length; j++)
			{ if(!render) continue;
			if(i == index[j]) render = false; }
			
			if(render) g.render();
		}
	}
	
	public boolean hasGroup(String s)
	{ return getGroup(s) != null; }
	
	public Group getGroup(String s)
	{
		for(int i = 0; i < groups.size(); i++)
		{ Group g = groups.get(i);
		if(g.groupName.equalsIgnoreCase(s))
		return g; } return null;
	}
	
	public int getGroupIndex(String s)
	{
		for(int i = 0; i < groups.size(); i++)
		{ Group g = groups.get(i);
		if(g.groupName.equalsIgnoreCase(s))
		return i; } return -1;
	}
	
	public OBJModel copy()
	{
		OBJModel m = new OBJModel();
		m.vertices.addAll(vertices);
		//if(texVertices.size() > 0)
		m.texVertices.addAll(texVertices);
		//if(vertexNormals.size() > 0)
		m.vertexNormals.addAll(vertexNormals);
		m.groups.addAll(groups);
		m.groupNames = groupNames.clone();
		m.totalFaces.addAll(totalFaces);
		m.sizeV = sizeV;
		
		if(m != null && m.texVertices != null)
			LMRenderHelper.enableTexture();
		else LMRenderHelper.disableTexture();
		
		m.renderAll();
		
		return m;
	}
}