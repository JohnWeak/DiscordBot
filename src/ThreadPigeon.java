import net.dv8tion.jda.api.entities.MessageChannel;
import java.util.Random;

public class ThreadPigeon extends Thread
{
	private final String authorName;
	private final MessageChannel channel;
	private final Random random;
	private static final Object object = ThreadPigeon.class;
	
	/** Il pigeon avrà la sua vendetta
	 * @param authorName colui che ha osato (o è stato sfortunato abbastanza da) evocare il <b>pigeon bazooka</b><br>
	 * @param channel il canale in cui il <b>pigeon bazooka</b> dovrà fare fuoco
	 * */
	public ThreadPigeon(String authorName, MessageChannel channel)
	{
		this.authorName = authorName;
		this.channel = channel;
		random = new Random();
	} // fine costruttore
	
	public void run()
	{
		if (random.nextInt(1000) == 42)
		{
			System.out.println("\t<"+Thread.currentThread().getName() + "> PIGEON BAZOOKAAAAAAAA");
			final var max = random.nextInt(5) + 5;
			final var pigeonMessage = "Oh no! " + authorName + " ha attivato il <:"+Emotes.pigeon+"> bazooka!\n"+max+" pigeon in arrivo!";
			channel.sendMessage(pigeonMessage).queue();
			channel.sendTyping().queue();
			Commands.pause(500,500);
			for (int i = 0; i < max; i++)
				channel.sendMessage("<:"+Emotes.pigeon+">").queue(l-> Commands.react("pigeon"));
		}
		else
			Commands.react("pigeon");
		
	} // fine metodo run()
	
} // fine ThreadPigeon