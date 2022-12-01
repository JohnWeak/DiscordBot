import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;


public class ThreadPokemon extends Thread
{
	public final String HOURS = "ore";
	public final String MINUTES = "minuti";
	public final String SECONDS = "secondi";
	
	private long timeout;
	private Pokemon pokemon;
	private TextChannel tc;
	private EmbedBuilder eb;
	
	
	// metodi
	public void setPokemon(Pokemon pokemon)
	{
		this.pokemon = pokemon;
	}
	public void setTc(TextChannel tc)
	{
		this.tc = tc;
	}
	public void setEmbedBuilder(EmbedBuilder eb)
	{
		this.eb = eb;
	}
	
	public ThreadPokemon(Pokemon pokemon)
	{
		this.pokemon = pokemon;
	}
	
	/**Imposta il tempo in cui il pokemon resta attivo nel canale prima di scappare.<br>
	 * È possibile stabilire quanti secondi, minuti oppure ore dovranno passare prima che scappi.*/
	public void timeoutTime(String type, long timeout)
	{
		final long time = 1000;
		
		this.timeout = switch (type)
		{
			case "ore" -> timeout * 60 * 60 * time;
			case "minuti" -> timeout * 60 * time;
			case "secondi" -> timeout * time;
			default -> timeout;
		};
		
	} // fine timeoutTime()
	
	
	@Override
	public void run()
	{
		tc.sendMessageEmbeds(eb.build()).queue(l ->
		{
			try
			{
				Thread.sleep(timeout);
			} catch (InterruptedException ignored) { }
			
			pokemon.setActive(false);
			eb.setFooter(pokemon.getNome() + " ran away.");
			eb.setColor(Color.GRAY);
			l.editMessageEmbeds(eb.build()).queue();
		});
		
		
		
	} // fine run()
	
} // fine ThreadPokemon
