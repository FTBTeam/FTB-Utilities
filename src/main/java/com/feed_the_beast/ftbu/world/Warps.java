package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Warps
{
    private final HashMap<String, BlockDimPos> warps = new HashMap<>();
    
    public void readFromNBT(NBTTagCompound tag, String s)
    {
        warps.clear();
        
        NBTTagCompound tag1 = (NBTTagCompound) tag.getTag(s);
        
        if(tag1 != null && !tag1.hasNoTags())
        {
            for(String s1 : LMNBTUtils.getMapKeys(tag1))
            {
                set(s1, new BlockDimPos(tag1.getIntArray(s1)));
            }
        }
    }
    
    public void writeToNBT(NBTTagCompound tag, String s)
    {
        NBTTagCompound tag1 = new NBTTagCompound();
        
        for(Map.Entry<String, BlockDimPos> e : warps.entrySet())
        {
            tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
        }
        
        tag.setTag(s, tag1);
    }
    
    public Collection<String> list()
    {
        if(warps.isEmpty()) { return new ArrayList<>(); }
        return warps.keySet();
    }
    
    public BlockDimPos get(String s)
    { return warps.get(s); }
    
    public boolean set(String s, BlockDimPos pos)
    {
        if(pos == null) { return warps.remove(s) != null; }
        return warps.put(s, pos.copy()) == null;
    }
    
    public int size()
    { return warps.size(); }
}