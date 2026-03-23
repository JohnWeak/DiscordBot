import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.TimerTask;

public class DailyTask extends TimerTask
{
	
	@Override
	public void run()
	{
		final ZoneId rome = ZoneId.of("Europe/Rome");
		// ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), zid);
		// final ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), zid);
		try
		{
			final boolean hasEnigmoClaimedDaily = Commands.canaleBot.getHistory()
				.retrievePast(100)
				.complete()
				.stream()
				.anyMatch(message -> 
					message.getTimeCreated().atZoneSameInstant(rome).getDayOfYear() == ZonedDateTime.now(rome).getDayOfYear() &&
					message.getTimeCreated().atZoneSameInstant(rome).getHour() > 8 &&
					message.getAuthor().getId().equals(Utente.ID_ENIGMO) &&
					message.getContentRaw().strip().toLowerCase().contains("owo daily")
				);
			
			if (!hasEnigmoClaimedDaily)
			{
				final String tag = Utente.getEnigmo().getAsMention();
				final String msg = EnigmoMSG.getMessage();
				final String msgToSend = String.format("%s %s", tag, msg);
				
				if (msgToSend == null)
				{
					new Errore<String>().report(this, "il messaggio da inviare è nullo");
					return;
				}

				Commands.canaleBot.sendMessage(msgToSend).queue();
			}
		} catch (Exception e) { new Errore<Exception>().report(this, e); }
		
	}
}

abstract class EnigmoMSG
{
	final static Random r = new Random();
	static boolean gentile;
	final static String[] msg = {
		"ENIGMO DOV'È IL TUO DAILY?!?!?!? PERCHÉ NON HAI FATTO ANCORA IL DAILY?!?!?!?!",
		String.format("Il signor Enigmo è %scortesemente pregato di presentare il proprio daily. Pena la reclusione fino a 3 anni.", gentile ? "": "s"),
		"Se continui a non fare il daily, Enigmo, sarò costretto a chiamare i carabinieri.",
		"Dai Enigmo è solo un daily, dai, fallo. Su, dai ti prego. Fallo per me, solo stavolta. Dai.",
		"Se non fai il daily farai piangere Gesù.",
		"Il contratto che hai sottoscritto prevede di fare il daily, Enigmo.",
		"Io ho fatto il mio daily, e tu?!?!?",
		"😡😡😡",
		"Fai il daily. Altrimenti mi arrabbio.",
		"Se non fai il daily, chiederò il divorzio",
		"In questo server ci sono due cose che vanno sempre rispettate: la seconda è il daily.",
		"I L. D A I L Y.",
		"Chi non fa il daily un vergognato è, è! Chi non fa il daily un vergognato è, è!"
	};
	
	public static String getMessage()
	{
		gentile = r.nextBoolean();
		return msg[r.nextInt(msg.length)];
	}
}