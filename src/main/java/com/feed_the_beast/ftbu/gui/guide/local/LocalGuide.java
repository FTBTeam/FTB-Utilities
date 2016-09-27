package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.info.IImageProvider;
import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbl.lib.client.InvalidTextureCoords;
import com.feed_the_beast.ftbl.lib.info.WrappedImageProvider;
import com.feed_the_beast.ftbl.lib.io.LMConnection;
import com.feed_the_beast.ftbl.lib.io.RequestMethod;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class LocalGuide extends Guide
{
    private ResourceLocation icon;

    private final IResourceProvider RESOURCE_PROVIDER = s -> new LMConnection(RequestMethod.FILE, new File(s).getAbsolutePath());

    public LocalGuide(String id, GuideType t)
    {
        super(id, t);
        icon = InvalidTextureCoords.INSTANCE.getTexture();
    }

    @Override
    public void fromJson(JsonElement json)
    {
        super.fromJson(json);
        JsonObject o = json.getAsJsonObject();
        icon = o.has("icon") ? icon = new ResourceLocation(o.get("icon").getAsString()) : InvalidTextureCoords.INSTANCE.getTexture();
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = super.getSerializableElement().getAsJsonObject();
        o.add("icon", new JsonPrimitive(icon.toString()));
        return o;
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
        return new WrappedImageProvider(icon);
    }
}
