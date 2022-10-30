package Bot;

import net.dv8tion.jda.api.entities.Emote;

import java.util.ArrayList;
import java.util.List;

public class EmoteClassRetriever
{
	private static List<Emote> emotes;
	private static List<String> names;
	private static List<String> ids;
	
	public EmoteClassRetriever()
	{
		names = new ArrayList<>();
	}
	
	
	public void setEmotes(List<Emote> list)
	{
		for (var e : emotes)
		{
			names.add(getS(e));
			ids.add(getIDs(e));
		}
	}
	
	public List<String> getEmoteNames()
	{
		return names;
	}
	public List<String> getEmoteIDs()
	{
		return ids;
	}
	
	
	private String getS(Emote e)
	{
		return e.toString().toLowerCase().split(":")[1].split("\\(")[0];
	}
	private String getIDs(Emote e)
	{
		return e.toString().split("\\(")[1].split("\\)")[0];
	}
	
}
