import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.time.LocalDateTime;

public class ThreadReminder extends Thread
{
	private final int tempo;
	private final MessageChannel channel;
	private final EmbedBuilder eb;
	
	private final LocalDateTime start, end;
	private boolean active;
	
	public ThreadReminder(int tempo, MessageChannel channel, EmbedBuilder eb)
	{
		this.tempo = tempo;
		this.channel = channel;
		this.eb = eb;
		
		start = LocalDateTime.now();
		end = start.plusSeconds(tempo/1000);
		active = true;
	}

	public boolean isActive()
	{
		return active;
	}
	
	/**Restituisce il tempo di partenza in cui è stato impostato promemoria
	 * @return la data in cui il promemoria è stato creato*/
	public LocalDateTime getStart()
	{
		return start;
	}
	
	/**Restituisce il tempo in cui il promemoria scadrà
	 * @return la data in cui il promemoria suonerà*/
	public LocalDateTime getEnd()
	{
		return end;
	}
	
	@Override
	public void run()
	{
		final LocalDateTime end;
		try
		{
			Thread.sleep(tempo);
			end = LocalDateTime.now();
			
			channel.sendMessageEmbeds(eb.build()).queue();
			
		}catch (InterruptedException e)
		{
			new Error<>().print(this,e);
		}
		finally
		{
			active = false;
		}
		
		
	} // run()
	
} // Reminder
