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
		this.timeout = switch (type)
		{
			case "ore" -> timeout * 60 * 60 * 1000;
			case "minuti" -> timeout * 60 * 1000;
			case "secondi" -> timeout * 1000;
			default -> timeout;
		};
		
	} // fine timeoutTime()
	
	
	@Override
	public void run()
	{
		var activePokemons = pokemon.getActivePokemons();
		if (!activePokemons.contains(pokemon))
			activePokemons.add(pokemon);
		
		tc.sendMessage("test: "+Thread.currentThread()).queue();
		tc.sendMessageEmbeds(eb.build()).queue(l ->
		{
			var pkn = pokemon.getNome();
			if (pkn.equalsIgnoreCase("poochyena") || pkn.equalsIgnoreCase("mightyena"))
			{
				Commands.react("pogey");
				l.addReaction("❤️").queue();
			}
			else
			{
				l.addReaction("👍🏻").queue();
				l.addReaction("❤️").queue();
				l.addReaction("👎🏻").queue();
			}
			
			try
			{
				Thread.sleep(timeout);
			} catch (InterruptedException ignored) { }
			
			pokemon.setActive(false);
			activePokemons.remove(pokemon);
			
			eb.setTitle("The wild " + pokemon.getNome() + " fled.");
			eb.setFooter(pokemon.getNome() + " ran away.");
			eb.setColor(Color.GRAY);
			
			l.clearReactions().queue();
			
			l.editMessageEmbeds(eb.build()).queue();
		});
		
		
		
	} // fine run()
	
} // fine ThreadPokemon
