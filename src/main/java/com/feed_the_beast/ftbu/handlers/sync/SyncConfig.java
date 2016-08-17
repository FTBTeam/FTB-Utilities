package com.feed_the_beast.ftbu.handlers.sync;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by LatvianModder on 17.08.2016.
 */
public class SyncConfig implements INBTSerializable<NBTTagCompound>
{
    @Override
    public NBTTagCompound serializeNBT()
    {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
    }
}
