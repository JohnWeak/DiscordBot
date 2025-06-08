import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.Random;

public class ThreadPigeon extends Thread
{
	private final String authorName;
	private final GuildMessageChannel channel;
	private final Random random;
	private final Object object = this;
	private final Errore<Exception> error = new Errore<>();
	
	/** Il pigeon avrà la sua vendetta
	 * @param authorName colui che ha osato (o è stato sfortunato abbastanza da) evocare il <b>pigeon bazooka</b><br>
	 * @param channel il canale in cui il <b>pigeon bazooka</b> dovrà fare fuoco
	 * */
	public ThreadPigeon(String authorName, GuildMessageChannel channel)
	{
		this.authorName = authorName;
		this.channel = channel;
		random = new Random();
	} // fine costruttore
	
	public void run()
	{
		final int num = random.nextInt(500);
		try
		{
			if (num == 42)
			{
				final int pigeons = random.nextInt(5,11); // 5-10
				final String pigeonEmote = Emotes.readyToSend(Emotes.pigeon);
				final String pigeonMessage = String.format("Oh no! %s ha attivato il %s bazooka!\nCi sono %d pigeon in arrivo!", authorName, pigeonEmote, pigeons);
				
				channel.sendMessage(pigeonMessage).queue();
				for (int i = 0; i < pigeons; i++)
				{
					channel.sendMessage(pigeonEmote).queue(m -> Commands.react("pigeon"));
				}
			}
			else
			{
				Commands.react("pigeon");
			}
		}
		catch (Exception e)
		{
			error.print(object,e);
		}
		
	} // fine metodo run()
	
} // fine ThreadPigeon