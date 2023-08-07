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
	private Message m;
	
	private final Error<Exception> error = new Error<>();
	private final PrivateMessage gion = new PrivateMessage(Utente.getGion());
	
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
		try
		{
			tc.sendMessageEmbeds(eb.build()).queue();
			
			sleep(500); // necessario così che non si confonda fra i due ultimi messaggi inviati nel canale
			
			var history = Utilities.channelHistory(tc, false, 3);
			if (history.isEmpty())
			{
				gion.send("Errore nel caricamento del channel history");
				return;
			}
			var latest = history.get(0);
			m = tc.retrieveMessageById(latest.getId()).complete();
			
			
			if (m == null)
			{
				gion.send("AAAAAAAAAAA");
				return;
			}
				
			if (pokemon.getNome().toLowerCase().matches("(?:pooch|might)yena"))
			{
				m.addReaction(Emotes.pogey).queue();
				m.addReaction("❤️").queue();
			}
			else
			{
				m.addReaction("👍🏻").queue();
				m.addReaction("❤️").queue();
				m.addReaction("👎🏻").queue();
			}
		}
		catch (Exception e) { error.print(object,e); }
		
		try
		{
			Thread.sleep(timeout);
			runAway();
			
		}catch (Exception e) { error.print(object,e); }
		
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
		
		m.clearReactions().queue();
		m.editMessageEmbeds(eb.build()).queue();
		
		
	} // fine runAway()
	
	
	
	
} // fine ThreadPokemon
