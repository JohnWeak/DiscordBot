import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;


import java.time.LocalDateTime;

public class ThreadReminder extends Thread
{
	private final int tempo;
	private final GuildMessageChannel channel;
	private final EmbedBuilder eb;
	@Getter private final LocalDateTime start, end;
	@Getter private boolean active;
	
	/**@param tempo il tempo in millisecondi
	 * @param channel in quale canale inviare il messaggio
	 * @param eb l'EmbedBuilder da costruire
	 * */
	public ThreadReminder(int tempo, GuildMessageChannel channel, EmbedBuilder eb)
	{
		this.tempo = tempo;
		this.channel = channel;
		this.eb = eb;
		
		start = LocalDateTime.now();
		end = start.plusSeconds(tempo/1000);
		active = true;
	}
	
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(tempo);
			
			channel.sendMessageEmbeds(eb.build()).queue();
			
		}catch (InterruptedException e)
		{
			new Errore<>().print(this,e);
		}
		finally
		{
			active = false;
		}
		
		
	} // run()
	
} // Reminder
