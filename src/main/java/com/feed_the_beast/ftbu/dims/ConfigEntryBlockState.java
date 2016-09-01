package com.feed_the_beast.ftbu.dims;

import com.feed_the_beast.ftbl.api.config.ConfigEntryCustom;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class ConfigEntryBlockState extends ConfigEntryCustom
{
    private IBlockState blockState;

    public ConfigEntryBlockState(IBlockState state)
    {
        blockState = state;
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

    public IBlockState getBlockState()
    {
        return blockState;
    }
}