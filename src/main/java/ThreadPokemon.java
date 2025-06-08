import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;

import java.awt.Color;
import java.util.List;

public class ThreadPokemon extends Thread
{
	private final Object object = ThreadPokemon.class;
	
	public final String HOURS = "ore";
	public final String MINUTES = "minuti";
	public final String SECONDS = "secondi";
	
	private long timeout;
	private final Pokemon pokemon;
	private final TextChannelImpl tc;
	private final EmbedBuilder eb;
	private Message m;
	
	private final Errore<Exception> error = new Errore<>();
	private final PrivateMessage gion = new PrivateMessage(Utente.getGion());
	
	public ThreadPokemon(Pokemon pokemon, TextChannelImpl tc, EmbedBuilder eb)
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
			
			final List<Message> history = Utilities.channelHistory(tc, false, 3);
			if (history.isEmpty())
			{
				gion.send("Errore nel caricamento del channel history");
				return;
			}
			final Message latest = history.getFirst();
			m = tc.retrieveMessageById(latest.getId()).complete();
			
			
			if (m == null)
			{
				gion.send("AAAAAAAAAAA");
				return;
			}
				
			if (pokemon.getNome().toLowerCase().matches("(?:pooch|might)yena"))
			{
				m.addReaction(Emoji.fromFormatted(Emotes.pogey)).queue();
				m.addReaction(Emoji.fromFormatted("❤️")).queue();
			}
			else
			{
				m.addReaction(Emoji.fromFormatted("👍🏻")).queue();
				m.addReaction(Emoji.fromFormatted("❤️")).queue();
				m.addReaction(Emoji.fromFormatted("👎🏻")).queue();
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
		final String pokemonNome = pokemon.getNome();
		final String msgFooter;
		final String[] types = pokemon.getTipo();
		final String title = String.format("The wild %s fled.", pokemonNome);
		
		msgFooter = (types[0].equalsIgnoreCase("flying") || types[1].equalsIgnoreCase("flying") ? String.format("%s flew away",pokemonNome) : String.format("%s ran away",pokemonNome));
		
		
		eb.setTitle(title);
		eb.setFooter(msgFooter);
		eb.setColor(Color.GRAY);
		
		pokemon.setCatturato(false);
		pokemon.setCatturabile(false);
		pokemon.setOwner(null);
		
		m.clearReactions().queue();
		m.editMessageEmbeds(eb.build()).queue();
		
		
	} // fine runAway()
	
	
	
	
} // fine ThreadPokemon
