package com.feed_the_beast.ftbu.dims;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.config.impl.PropertyCustom;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class ConfigEntryBlockState extends PropertyCustom
{
    private IBlockState blockState;

    public ConfigEntryBlockState(IBlockState state)
    {
        blockState = state;
    }

    public IBlockState getBlockState()
    {
        return blockState;
    }

    @Override
    public IConfigValue copy()
    {
        return new ConfigEntryBlockState(blockState);
    }

    @Override
    public void fromJson(JsonElement o)
    {
        blockState = Blocks.AIR.getDefaultState();

        if(o.isJsonPrimitive())
        {
            blockState = Block.REGISTRY.getObject(new ResourceLocation(o.getAsString())).getDefaultState();
        }
    }

    @Override
    public JsonElement getSerializableElement()
    {
        return new JsonPrimitive(Block.REGISTRY.getNameForObject(blockState.getBlock()).toString());
    }

    @Override
    public NBTBase serializeNBT()
    {
        return new NBTTagInt(Block.getStateId(blockState));
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        blockState = Block.getStateById(((NBTPrimitive) nbt).getInt());
    }
}