import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.time.LocalDate;
import java.util.List;
import java.util.TimerTask;

public class DailyTask extends TimerTask
{
	private final GuildMessageChannel canaleBot = Commands.canaleBot;
	
	@Override
	public void run()
	{
		boolean enigmosDaily = false;
		final LocalDate today = LocalDate.now();
		final List<Message> history = canaleBot
			.getHistory()
			.retrievePast(35)
			.complete()
			.stream()
			.filter(message -> message.getTimeCreated().toLocalDate().equals(today)).toList()
		;
		
		for (Message m : history)
		{
			if (m.getAuthor().getId().equals(Utente.getEnigmo().getId()))
			{
				if (m.getContentRaw().strip().toLowerCase().contains("owo daily"))
				{
					enigmosDaily = true;
					break;
				}
			}
		}
		
		if (!enigmosDaily)
		{
			final String tag = Utente.getEnigmo().getAsMention();
			final String msg = "ENIGMO DOV'È IL TUO DAILY?!?!?!? PERCHÉ NON HAI FATTO ANCORA IL DAILY?!?!?!?!";
			
			canaleBot.sendMessage(tag + msg).queue();
		}
	}
}
