package com.feed_the_beast.ftbu.client.gui.claims;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import latmod.lib.LMColorUtils;
import latmod.lib.MathHelperLM;
import latmod.lib.PixelBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ThreadReloadArea extends Thread
{
    public static final PixelBuffer pixels = new PixelBuffer(GuiClaimChunks.tiles_tex * 16, GuiClaimChunks.tiles_tex * 16);
    public static final short[] heightMap = new short[pixels.pixels.length];
    private static final short defHeight = -1;
    private static final Map<IBlockState, Integer> colorCache = new HashMap<>();
    public final World worldObj;
    public final GuiClaimChunks gui;
    public Chunk chunkMC;
    public short maxHeight = 0;

    public ThreadReloadArea(World w, GuiClaimChunks m)
    {
        super("LM_MapReloader");
        setDaemon(true);
        worldObj = w;
        gui = m;
        Arrays.fill(heightMap, defHeight);
        Arrays.fill(pixels.pixels, 0);
    }

    @Override
    public void run()
    {
        try
        {
            for(int cz = 0; cz < GuiClaimChunks.tiles_gui; cz++)
            {
                for(int cx = 0; cx < GuiClaimChunks.tiles_gui; cx++)
                {
                    //if(worldObj.getChunkProvider().getLoadedChunk(gui.startX, gui.startY) != null)
                    {
                        chunkMC = worldObj.getChunkFromChunkCoords(gui.startX, gui.startY);
                        maxHeight = (short) Math.max(255, chunkMC.getTopFilledSegment() + 15);

                        int x = (gui.startX + cx) * 16;
                        int y = (gui.startY + cz) * 16;

                        for(int i = 0; i < 256; i++)
                        {
                            pixels.setRGB(cx * 16 + (i % 16), cz * 16 + (i / 16), getBlockColor(x + (i % 16), y + (i / 16)));
                        }
                    }
                }
            }

            GuiClaimChunks.pixelBuffer = FTBLibClient.toByteBuffer(pixels.pixels, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getBlockColor(int bx, int bz)
    {
        short by = getTopY(bx, bz);
        if(by == defHeight || by > 255)
        {
            return 0;
        }

        BlockPos pos = new BlockPos(bx, by, bz);

        IBlockState state = worldObj.getBlockState(pos);

        if(!state.getBlock().isAir(state, worldObj, pos))
        {
            int col = getBlockColor(state);

            short bw = getTopY(bx - 1, bz);
            short be = getTopY(bx + 1, bz);
            short bn = getTopY(bx, bz - 1);
            short bs = getTopY(bx, bz + 1);

            if((bw != defHeight && bw < by) || (bn != defHeight && bn < by))
            {
                return LMColorUtils.addBrightness(col, 25);
            }
            else if((be != defHeight && be < by) || (bs != defHeight && bs < by))
            {
                return LMColorUtils.addBrightness(col, -25);
            }

            return col;
        }

        return 0;
    }

    private short getTopY(int bx, int bz)
    {
        int x = MathHelperLM.wrap(bx, 16);
        int z = MathHelperLM.wrap(bz, 16);

        Chunk c = chunkMC;
        short max = maxHeight;
        boolean mapValue = false;

        int cx = MathHelperLM.chunk(bx);
        int cz = MathHelperLM.chunk(bz);

        if(cx == gui.startX && cz == gui.startY)
        {
            mapValue = true;
            if(heightMap[x + z * 16] != defHeight)
            {
                return heightMap[x + z * 16];
            }
        }
        else
        {
            c = worldObj.getChunkFromChunkCoords(MathHelperLM.chunk(bx), MathHelperLM.chunk(bz));
            max = (short) Math.max(255, c.getTopFilledSegment() + 15);
        }

        for(short y = max; y > 0; --y)
        {
            IBlockState state = c.getBlockState(x, y, z);
            if(state.getBlock() == Blocks.TALLGRASS || worldObj.isAirBlock(new BlockPos(bx, y, bz)))
            {
                continue;
            }

            if(mapValue)
            {
                heightMap[x + z * 16] = y;
            }
            return y;
        }

        return defHeight;
    }

    private int getBlockColor(IBlockState state)
    {
        return 0xFF000000 | getBlockColor0(state);
        
        /*
        Integer col = colorCache.get(state);

        if(col == null)
        {
            col = 0xFF000000 | getBlockColor0(state);
            colorCache.put(state, col);
        }

        return col;
        */
    }

    private int getBlockColor0(IBlockState state)
    {
        Block b = state.getBlock();

        if(b == Blocks.SANDSTONE) { return MapColor.SAND.colorValue; }
        else if(b == Blocks.FIRE) { return MapColor.RED.colorValue; }
        else if(b == Blocks.YELLOW_FLOWER) { return MapColor.YELLOW.colorValue; }
        else if(b == Blocks.LAVA) { return MapColor.ADOBE.colorValue; }
        else if(b == Blocks.END_STONE) { return MapColor.SAND.colorValue; }
        else if(b == Blocks.OBSIDIAN) { return 0x150047; }
        else if(b == Blocks.GRAVEL) { return 0x8D979B; }
        else if(b == Blocks.GRASS) { return 0x74BC7C; }
        //else if(b.getMaterial(state) == Material.water)
        //	return LMColorUtils.multiply(MapColor.waterColor.colorValue, b.colorMultiplier(worldObj, pos), 200);
        else if(b == Blocks.RED_FLOWER)
        {
            switch(state.getValue(Blocks.RED_FLOWER.getTypeProperty()))
            {
                case POPPY:
                    return MapColor.RED.colorValue;
                case BLUE_ORCHID:
                    return MapColor.LIGHT_BLUE.colorValue;
                case ALLIUM:
                    return MapColor.MAGENTA.colorValue;
                case HOUSTONIA:
                    return MapColor.SILVER.colorValue;
                case RED_TULIP:
                    return MapColor.RED.colorValue;
                case ORANGE_TULIP:
                    return MapColor.ADOBE.colorValue;
                case WHITE_TULIP:
                    return MapColor.SNOW.colorValue;
                case PINK_TULIP:
                    return MapColor.PINK.colorValue;
                case OXEYE_DAISY:
                    return MapColor.SILVER.colorValue;
            }
        }
        else if(b == Blocks.PLANKS)
        {
            switch(state.getValue(BlockPlanks.VARIANT))
            {
                case OAK:
                    return 0xC69849;
                case SPRUCE:
                    return 0x7C5E2E;
                case BIRCH:
                    return 0xF2E093;
                case JUNGLE:
                    return 0xC67653;
                case ACACIA:
                    return 0xE07F3E;
                case DARK_OAK:
                    return 0x512D14;
            }
        }

        //if(b == Blocks.leaves || b == Blocks.vine || b == Blocks.waterlily)
        //	return LMColorUtils.addBrightness(b.colorMultiplier(worldObj, pos), -40);
        //else if(b == Blocks.grass && state.getValue(BlockGrass.SNOWY))
        //	return LMColorUtils.addBrightness(b.colorMultiplier(worldObj, pos), -15);

        return b.getMapColor(state).colorValue;
    }
}