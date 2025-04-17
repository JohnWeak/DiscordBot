
import java.time.LocalDate;
import java.util.Random;
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
				final String msg = EnigmoMSG.getMessage();
				final String msgToSend = String.format("%s %s", tag, msg);
				
				Commands.canaleBot.sendMessage(msgToSend).queue();
			}
		} catch (Exception e) { new Error<Exception>().print(this, e); }
	}
}

abstract class EnigmoMSG
{
	static final Random r = new Random();
	static boolean gentile;
	static final String[] msg = {
		"ENIGMO DOV'È IL TUO DAILY?!?!?!? PERCHÉ NON HAI FATTO ANCORA IL DAILY?!?!?!?!",
		String.format("Il signor Enigmo è %scortesemente pregato di presentare il proprio daily. Pena la reclusione fino a 3 anni.", gentile ? "": "s"),
		"Se continui a non fare il daily, Enigmo, sarò costretto a chiamare i carabinieri.",
		"Dai Enigmo è solo un daily, dai, fallo. Su, dai ti prego. Fallo per me, solo stavolta. Dai.",
		"È un daily, non un bonifico: non serve un giorno lavorativo per approvarlo >:(",
	};
	
	public static String getMessage()
	{
		gentile = r.nextBoolean();
		return msg[r.nextInt(msg.length)];
	}
}