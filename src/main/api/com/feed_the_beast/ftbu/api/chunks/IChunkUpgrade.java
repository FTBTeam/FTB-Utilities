package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbl.lib.LangKey;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public interface IChunkUpgrade extends IStringSerializable
{
    //TODO: Move this to a registry someday
    int getId();

    LangKey getLangKey();
}