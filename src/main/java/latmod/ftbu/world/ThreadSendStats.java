package latmod.ftbu.world;

import java.io.DataOutputStream;
import java.net.*;
import java.util.UUID;

import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.mod.config.FTBUConfigStats;
import latmod.lib.LMUtils;

public class ThreadSendStats extends Thread
{
	public final String mode;
	public final String cid;
	public final int minutesPlayed;
	
	public ThreadSendStats(String m, UUID id, int played)
	{ mode = m; cid = id.toString(); minutesPlayed = played; }
	
	public String validate(String s)
	{
		if(s == null) return "";
		s = s.replace("/", "%2F");
		s = s.replace(" ", "%20");
		return s;
	}
	
	public void run()
	{
		if(FTBUConfigStats.trackingID.get().isEmpty()) return;
		
		try
		{
			StringBuilder data = new StringBuilder();
			data.append("v=1");
			//data.append("&ds=app");
			//data.append("&t=pageview");
			data.append("&t=item");
			data.append("&tid=");
			data.append(FTBUConfigStats.trackingID.get());
			data.append("&cid=");
			data.append(cid);
			data.append("&dp=");
			data.append("%2F");
			data.append(FTBUConfigStats.modpack.get());
			data.append("%2F");
			
			data.append("&in=");
			data.append("evolved");
			data.append("&iq=");
			data.append(minutesPlayed);
			
			//data.append("&dt=");
			//data.append("Modpack%20Stats");
			
			long startTime = LMUtils.millis();
			if(FTBLibFinals.DEV) System.out.println("Sending 'POST' data '" + data + "'");
			
			URL obj = new URL("http://www.google-analytics.com/collect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "HTTP/1.1");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data.toString());
			wr.flush();
			wr.close();
			
			int responseCode = con.getResponseCode();
			if(responseCode != 200)
				System.err.println("Received error code " + responseCode + " after " + (LMUtils.millis() - startTime) + "ms");
			else if(FTBLibFinals.DEV) System.out.println("Info sent after " + (LMUtils.millis() - startTime) + "ms");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
	}
}