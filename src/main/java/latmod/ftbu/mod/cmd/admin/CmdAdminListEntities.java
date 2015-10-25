package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.api.guide.GuideFile;
import latmod.ftbu.cmd.*;
import latmod.ftbu.net.MessageDisplayGuide;
import latmod.lib.FastMap;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;

public class CmdAdminListEntities extends CommandLM
{
	public CmdAdminListEntities(String s)
	{ super(s, CommandLevel.OP); }

	@SuppressWarnings("unchecked")
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		GuideFile file = new GuideFile("Entity List");
		file.main.getTitle();
		
		FastMap<String, Class<?>> map = new FastMap<String, Class<?>>();
		map.putAll(EntityList.stringToClassMapping);
		map.sortFromKeyStrings(true);
		
		for(int i = 0; i < map.size(); i++)
		{
			file.main.println(map.keys.get(i));
			file.main.println(map.values.get(i).getName());
			file.main.println("");
		}
		
		new MessageDisplayGuide(file).sendTo(ep);
		return null;
	}
}