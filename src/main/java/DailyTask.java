import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.time.LocalDate;
import java.util.TimerTask;

public class DailyTask extends TimerTask
{
	private final GuildMessageChannel canaleBot = Commands.canaleBot;
	
	@Override
	public void run()
	{
		final boolean enigmosDaily = canaleBot.getHistory()
			.retrievePast(15)
			.complete()
			.stream()
			.anyMatch(message ->
				message.getTimeCreated().toLocalDate().equals(LocalDate.now()) &&
				message.getAuthor().getId().equals(Utente.ID_ENIGMO) &&
				message.getContentRaw().strip().toLowerCase().contains("owo daily")
			);
		
		if (!enigmosDaily)
		{
			final String tag = Utente.getEnigmo().getAsMention();
			final String msg = "ENIGMO DOV'È IL TUO DAILY?!?!?!? PERCHÉ NON HAI FATTO ANCORA IL DAILY?!?!?!?!";
			
			try
			{
				canaleBot.sendMessage(tag + msg).queue();
			}catch (Exception e) { new Error<Exception>().print(this, e); }
		}
	}
}
