package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class LocalGuide extends Guide
{
    private final File folder;
    private GuideType type;
    private String name;
    private List<String> authors;
    private List<String> guide_authors;
    private List<String> modes;
    private ResourceLocation icon;

    public LocalGuide(String id, GuideType t, File f)
    {
        super(id, t);
        folder = f;
    }

    public File getFolder()
    {
        return folder;
    }

    @Override
    public boolean isLocal()
    {
        return true;
    }

    @Override
    public IResourceProvider getResourceProvider()
    {
        return s -> new LMConnection(RequestMethod.FILE, new File(folder, s).getAbsolutePath());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getIcon()
    {
        if(icon == null)
        {
            try
            {
                BufferedImage img = new LMConnection(RequestMethod.FILE, new File(folder, "icon.png").getAbsolutePath()).connect().asImage();
                icon = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("ftbu_guide/" + getName() + ".png", new DynamicTexture(img));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return icon;
    }
}
