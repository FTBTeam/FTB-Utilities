package latmod.ftbu.mod.config;

import java.io.File;
import java.util.*;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.util.LMJsonUtils;

import com.google.gson.annotations.Expose;

public class ConfigIRC
{
	private static File saveFile;
	
	@Expose public Boolean enabled;
	@Expose public String server;
	@Expose public String[] channels;
	@Expose public String displayName;
	@Expose public String nickservName;
	@Expose public String nickservPass;
	@Expose public Map<UUID, String> customPlayerNames;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/irc.txt");
		FTBUConfig.irc = LMJsonUtils.fromJsonFile(saveFile, ConfigIRC.class);
		if(FTBUConfig.irc == null) FTBUConfig.irc = new ConfigIRC();
		FTBUConfig.irc.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(enabled == null) enabled = false;
		if(server == null) server = "irc.esper.net";
		if(channels == null) channels = new String[0];
		if(displayName == null) displayName = "FTBU_IRC_Server_" + Integer.toHexString(LatCoreMC.rand.nextInt());
		if(nickservName == null) nickservName = "";
		if(nickservPass == null) nickservPass = "";
		if(customPlayerNames == null) customPlayerNames = new HashMap<UUID, String>();
	}
	
	public static void save()
	{
		if(FTBUConfig.irc == null) load();
		if(!LMJsonUtils.toJsonFile(saveFile, FTBUConfig.irc))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
	
	public static void saveReadme(FTBUReadmeEvent e)
	{
		FTBUReadmeEvent.ReadmeFile.Category backups = e.file.get("latmod/ftbu/irc.txt");
		backups.add("enabled", "true enables irc server", false);
		backups.add("server", "IRC Server", "irc.esper.net");
		backups.add("channels", "Server bot channels. Example: [\"#LatMod\", \"#EnkiGaming\"]", "[]");
		backups.add("displayName", "Bot's display username", "FTBU_IRC_Server_<random number>");
		backups.add("nickservName", "Bot's NickServ username", "");
		backups.add("nickservPass", "Bot's NickServ password", "");
		backups.add("customPlayerNames", "Map of custom player usernames (in case they don't want to be pinged)", "{}");
	}
}