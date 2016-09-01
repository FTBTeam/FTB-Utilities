package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.info.IImageProvider;
import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbl.api.info.impl.URLImageProvider;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.List;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class LocalGuide extends Guide
{
    private GuideType type;
    private String name;
    private List<String> authors;
    private List<String> guide_authors;
    private List<String> modes;
    private IImageProvider icon;

    private final IResourceProvider RESOURCE_PROVIDER = s -> new LMConnection(RequestMethod.FILE, new File(s).getAbsolutePath());

    public LocalGuide(String id, GuideType t)
    {
        super(id, t);
    }

    @Override
    public boolean isLocal()
    {
        return true;
    }

    @Override
    public IResourceProvider getResourceProvider()
    {
        return RESOURCE_PROVIDER;
    }

    @Override
    public IImageProvider getIcon()
    {
        if(icon == null)
        {
            try
            {
                //BufferedImage img = new LMConnection(RequestMethod.FILE, new File().getAbsolutePath()).connect().asImage();
                //icon = new WrappedImageProvider(Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("ftbu_guide/" + getName() + ".png", new DynamicTexture(img));
                icon = new URLImageProvider(new File("icon.png").toURI().toURL().getPath());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return icon;
    }
}
