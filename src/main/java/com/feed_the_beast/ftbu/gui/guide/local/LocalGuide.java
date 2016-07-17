package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by PC on 17.07.2016.
 */
public class LocalGuide extends Guide
{
    public final File folder;
    private ResourceLocation icon;

    public LocalGuide(GuideType t, String u, JsonObject o)
    {
        super(t, u, o);
        folder = new File(FTBLib.folderLocal, "guides/" + type.group + "/" + getID());
    }

    @Override
    public boolean isLocal()
    {
        return true;
    }

    @Override
    public IResourceProvider getResourceProvider(String path)
    {
        return s -> new LMConnection(RequestMethod.FILE, new File(folder, path).getAbsolutePath());
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
                icon = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("ftbu_guide/" + getID() + ".png", new DynamicTexture(img));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return icon;
    }
}
