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
	 * È possibile stabilire quanti secondi, minuti oppure ore dovranno passare prima che scappi.
	 * @param type il tempo da impostare: ore, minuti oppure secondi.
	 * @param timeout quanto tempo dovrà passare prima che il pokemon non sia più disponibile.
	 * */
	public void timeoutTime(String type, long timeout)
	{
		this.timeout = switch (type)
		{
			case HOURS -> timeout * 60 * 60 * 1000;
			case MINUTES -> timeout * 60 * 1000;
			case SECONDS -> timeout * 1000;
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
			var pokemonNome = pokemon.getNome();
			if (pokemonNome.equalsIgnoreCase("poochyena") || pokemonNome.equalsIgnoreCase("mightyena"))
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
			
			var msgFooter = pokemonNome + "ran away";
			var types = pokemon.getTipo();
			
			for (String s : types)
			{
				if (s.equalsIgnoreCase("flying"))
				{
					msgFooter = pokemonNome + " flew away.";
					break;
				}
			}
			
			
			eb.setTitle("The wild " + pokemonNome + " fled.");
			eb.setFooter(msgFooter);
			eb.setColor(Color.GRAY);
			
			l.clearReactions().queue();
			
			l.editMessageEmbeds(eb.build()).queue();
		});
		
		
		
	} // fine run()
	
} // fine ThreadPokemon
