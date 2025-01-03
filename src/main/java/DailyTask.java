
import java.time.LocalDate;
import java.util.TimerTask;

public class DailyTask extends TimerTask
{
	
	@Override
	public void run()
	{
		try
		{
			final boolean enigmosDaily = Commands.canaleBot.getHistory()
				.retrievePast(100)
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
				
				final String msgToSend = String.format("%s %s", tag, msg);
				
				Commands.canaleBot.sendMessage(msgToSend).queue();
				
			}
		} catch (Exception e) { new Error<Exception>().print(this, e); }
	}
}
