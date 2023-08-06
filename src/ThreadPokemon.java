import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;

public class ThreadPokemon extends Thread
{
	private final Object object = ThreadPokemon.class;
	
	public final String HOURS = "ore";
	public final String MINUTES = "minuti";
	public final String SECONDS = "secondi";
	
	private long timeout;
	private final Pokemon pokemon;
	private final TextChannel tc;
	private final EmbedBuilder eb;
	private Message l;
	
	private final Error<Exception> error = new Error<>();
	
	public ThreadPokemon(Pokemon pokemon, TextChannel tc, EmbedBuilder eb)
	{
		this.pokemon = pokemon;
		this.tc = tc;
		this.eb = eb;
	}
	
	/**Imposta il tempo in cui il pokemon resta attivo nel canale prima di scappare.<br>
	 * È possibile stabilire quanti secondi, minuti oppure ore dovranno passare prima che scappi.
	 * @param type il tempo da impostare: ore, minuti oppure secondi.
	 * @param timeout quanto tempo dovrà passare prima che il pokemon non sia più disponibile.
	 * */
	public void setTimeoutTime(String type, long timeout)
	{
		this.timeout = switch (type)
		{
			case HOURS -> timeout * 1000 * 60 * 60;
			case MINUTES -> timeout * 1000 * 60;
			case SECONDS -> timeout * 1000;
			default -> timeout;
		};
		
	} // fine timeoutTime()
	
	
	@Override
	public void run()
	{
		var gion = new PrivateMessage(Utente.getGion());
		gion.send("Prima del try");
		
		try
		{
			gion.send("Dentro il try, parte 1");
			tc.sendMessageEmbeds(eb.build()).queue(x-> this.l = x);
			
			gion.send("Dentro il try, parte 2");
			gion.send("Pokemon spawnato.");
			
			if (pokemon.getNome().toLowerCase().matches("(?:pooch|might)yena"))
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
			
			
			
		}
		catch (Exception e) { error.print(object,e); }
		
		try
		{
			gion.send("Prima della sleep ("+timeout+").");
			Thread.sleep(timeout);
			gion.send("Dopo la sleep.");
			runAway();
		}catch (Exception e) { error.print(object,e); }
		
		
		gion.send("Il thread ha finito.");
	} // fine run()
	
	public void runAway()
	{
		var pokemonNome = pokemon.getNome();
		var msgFooter = pokemonNome + " ran away.";
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
		
		pokemon.setCatturato(false);
		pokemon.setCatturabile(false);
		pokemon.setOwner(null);
		
		l.clearReactions().queue();
		l.editMessageEmbeds(eb.build()).queue();
		
		
	} // fine runAway()
	
	
	
	
} // fine ThreadPokemon
