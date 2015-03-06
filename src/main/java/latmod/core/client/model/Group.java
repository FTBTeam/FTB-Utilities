package latmod.core.client.model;
import latmod.core.client.LMRenderHelper;
import latmod.core.util.FastList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.*;

/** Made by LatvianModder */
@SideOnly(Side.CLIENT)
public class Group
{
	public OBJModel parent;
	public String groupName;
	public FastList<Face> faces = new FastList<Face>();
	private int listID = -1;
	public Vector3f pos, rotation, offset;
	
	public Group(OBJModel m, String s)
	{
		parent = m;
		groupName = s;
		
		pos = new Vector3f();
		rotation = new Vector3f();
		offset = new Vector3f();
	}
	
	public void render()
	{
		if(listID == -1)
		{
			if(parent.texVertices == null) LMRenderHelper.disableTexture();
			else LMRenderHelper.enableTexture();
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
			
			listID = GL11.glGenLists(1);
			GL11.glNewList(listID, GL11.GL_COMPILE);
			
			float posX0 = 0F, posY0 = 0F, posZ0 = 0F;
			float vSize = 0F;
			
			for(int i = 0; i < faces.size(); i++)
			{
				Face f = faces.get(i);
				GL11.glBegin(f.drawMode);
				
				//TODO: Normals
				
				for(int j = 0; j < f.verticies.length; j++)
				{
					Vector3f v = parent.vertices.get(f.verticies[j]);
					
					if(f.texVerticies != null)
					{
						Vector3f vt = parent.texVertices.get(f.texVerticies[j]);
						if(vt.z == -1F) GL11.glTexCoord2f(vt.x, vt.y);
						else GL11.glTexCoord3f(vt.x, vt.y, vt.z);
					}
					
					Vector3f vn = parent.vertexNormals.get(f.normals[j]);
					if(vn != null) GL11.glNormal3f(vn.x, vn.y, vn.z);
					else GL11.glNormal3f(0F, 1F, 0F);
					
					GL11.glVertex3f(v.x, v.y, v.z);
					posX0 += v.x;
					posY0 += v.y;
					posZ0 += v.z;
					
					vSize++;
				}
				
				GL11.glEnd();
			}
			
			GL11.glEndList();
			
			pos.x = posX0 / vSize;
			pos.y = posY0 / vSize;
			pos.z = posZ0 / vSize;
		}
		
		boolean hasOffset = offset.lengthSquared() != 0F;
		boolean hasRotation = rotation.lengthSquared() != 0F;
		
		if(hasOffset || hasRotation)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(pos.x, pos.y, pos.z);
			if(hasOffset) GL11.glTranslatef(offset.x, offset.y, offset.z);
			if(hasRotation)
			{
				GL11.glRotatef(rotation.y, 0F, 1F, 0F);
				GL11.glRotatef(rotation.x, 1F, 0F, 0F);
				GL11.glRotatef(rotation.z, 0F, 0F, 1F);
			}
			if(hasOffset) GL11.glTranslatef(offset.x, offset.y, offset.z);
			GL11.glTranslatef(-pos.x, -pos.y, -pos.z);
			GL11.glCallList(listID);
			GL11.glPopMatrix();
		}
		else GL11.glCallList(listID);
	}
}