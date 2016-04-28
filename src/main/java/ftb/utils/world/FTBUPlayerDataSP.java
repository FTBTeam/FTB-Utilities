package ftb.utils.world;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.ForgePlayer;
import ftb.lib.api.ForgePlayerSP;
import ftb.utils.FTBUFinals;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LatvianModder on 23.02.2016.
 */
@SideOnly(Side.CLIENT)
public class FTBUPlayerDataSP extends FTBUPlayerData
{
	public static FTBUPlayerDataSP get(ForgePlayer p)
	{ return (FTBUPlayerDataSP) p.getData(FTBUFinals.MOD_ID); }
	
	public short claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	
	public FTBUPlayerDataSP(ForgePlayerSP p)
	{
		super(FTBUFinals.MOD_ID, p);
	}
	
	@Override
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		IntMap map = new IntMap();
		map.list.setDefVal(0);
		map.list.addAll(tag.getIntArray("F"));
		
		flags = (byte) map.get(0);
		blocks = PrivacyLevel.VALUES_3[map.get(1)];
		
		if(self)
		{
			claimedChunks = (short) map.get(10);
			loadedChunks = (short) map.get(11);
			maxClaimedChunks = (short) map.get(12);
			maxLoadedChunks = (short) map.get(13);
		}
	}
}
