import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.time.Instant;
import java.util.List;
import java.util.*;

public class Commands extends ListenerAdapter
{
	private static final File valori = new File("valori.txt");
	private static final File nomiPkmn = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	private static MessageChannel channel;
	private static final String[] listaComandi = {"!coinflip", "!poll", "!info", "!8ball", "!pokemon", "!carta", "!duello", "!colpevole", "!bestemmia", "!massshooting"};
	private static final String[] listaDescrizioni =
	{
		"Il bot lancerà una moneta.",
		"Permette di creare sondaggi.",
		"Visualizza le informazioni. Proprio quelle che stai leggendo!",
		"Chiedi un responso all'Entità Superiore: la magica palla 8.",
		"Acchiappali tutti!",
		"Genera una carta da gioco.",
		"Sfida un giocatore ad un duello di carte.",
		"Lascia che RNGesus decida la percentuale di colpevolezza di un altro utente.",
		"Quando serve incolpare qualcuno ai piani alti.",
		"Ottieni il resoconto delle sparatorie di massa negli USA. Sono dati reali."
	};
	private static int messaggiInviati = 0;
	private static int limite;
	private static String authorName;
	private static long id;
	private static final Locale locale = Locale.ITALIAN;
	private static Message message;
	private static String messageRaw;
	private static User author;
	private static ArrayList<Challenge> listaSfide = new ArrayList<>();
	private static final String[] challenge = {"Duello Carte", "Sasso Carta Forbici"};
	private static boolean duelloAttivo = false;
	private static boolean sfidaAttiva = false;
	private static User sfidante = null;
	private static User sfidato = null;
	private static final String[] simboli = {"♥️", "♦️", "♣️", "♠️"};
	private static String sceltaBot;
	private static TextChannel canaleBotPokemon;
	private static final int currentYear = new GregorianCalendar().get(Calendar.YEAR);
	private static final String NUMENIGMO = "7166";
	private static final String NUMOBITO = "2804";
	private static final String NUMGION = "0935";
	private static final String NUMLEX = "2241";
	private static String bearer;
	private static final String hashtag = "%23";
	private static final String clanTag = "PLQP8UJ8";
	private static final String tagCompleto = hashtag + clanTag;
	private static final JSONParser jsonParser = new JSONParser();
	
	
	/**Determina l'ora del giorno e restituisce la stringa del saluto corrispondente*/
	private String getSaluto()
	{
		var c = new GregorianCalendar();
		var saluto = "";
		var hour = c.get(Calendar.HOUR_OF_DAY);
		var x = c.get(Calendar.MONTH);
		short tramonto;
		
		switch (c.get(Calendar.MONTH)) // se è estate, il tramonto avviene più tardi
		{
			case 4, 5, 6, 7 -> tramonto = 20;
			default -> tramonto = 17;
		}
		
		if (hour > 0 && hour < 7)
			saluto = "Buona mattina";
		else if (hour >= 7 && hour < 13)
			saluto = "Buongiorno";
		else if (hour >= 13 && hour < tramonto)
			saluto = "Buon pomeriggio";
		else if (hour >= tramonto && hour < 23)
			saluto = "Buonasera";
		else
			saluto = "Buonanotte";
		
		return saluto;
	} // fine getSaluto()
	
	
	/** onReady() viene eseguita soltanto all'avvio del bot */
	public void onReady(@NotNull ReadyEvent event)
	{
		String nome = event.getJDA().getSelfUser().getName();
		Activity act = Objects.requireNonNull(event.getJDA().getPresence().getActivity());
		
		bearer = new Bearer().getBearer();
		
		System.out.printf("%s si è connesso a Discord!\n\n", nome);
		System.out.print("public class MessageHistory\n{\n");
		
		var canaleBot = event.getJDA().getTextChannelsByName("\uD83E\uDD16bot-owo", true).get(0);
		canaleBotPokemon = event.getJDA().getTextChannelsByName("pokémowon", true).get(0);
		
		var activity = act.getType().toString();
		var nomeActivity = "**" + act.getName() + "**";
		var activityTradotta = activity.equals("WATCHING") ? "guardo " : "gioco a ";
		
		// canaleBot.sendMessage(getSaluto() + ", oggi " + activityTradotta + nomeActivity).queue();
	} // fine onReady()

	/** Questo metodo decide cosa fare quando un messaggio viene modificato */
	public void onMessageUpdate(@NotNull MessageUpdateEvent event)
	{
		identifyLatestMessage(null, event);
		//var reactions = message.getReactions();
		
		aggiungiReazioni();
		checkForKeywords(messageRaw.toLowerCase(Locale.ROOT));
	} // fine onMessageUpdate()
	
	/** Gestisce i messaggi inviati in qualsiasi canale testuale di qualsiasi server in cui è presente il bot */
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		identifyLatestMessage(event, null);
		
		final String mockupCode = "\tString %s = \"%s\"; // in \"%s\" (%s) - %s";
		var date = new Date();
		var dFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		var dataFormattata = dFormat.format(date);

		String messageChannelString = "#"+ channel.toString().split(":")[1].split("\\(")[0];
		String guild = event.getGuild().toString().split("\\(")[0].split(":")[1];
		
		System.out.printf(mockupCode, authorName, messageRaw, messageChannelString, guild, dataFormattata);
		System.out.print("\n}\r");
		
		aggiungiReazioni();
		checkForKeywords(messageRaw.toLowerCase(Locale.ROOT));
		
	} // fine onMessageReceived()
	
	/** Questo metodo tiene conto di quale è l'ultimo messaggio che viene inviato/modificato */
	private void identifyLatestMessage(MessageReceivedEvent received, MessageUpdateEvent updated)
	{
		if (received != null)
		{
			id = received.getMessageIdLong();
			author = received.getAuthor();
			authorName = author.getName();
			message = received.getMessage();
			messageRaw = message.getContentRaw();
			channel = received.getChannel();
		}
		else
		{
			id = updated.getMessageIdLong();
			author = updated.getAuthor();
			authorName = author.getName();
			message = updated.getMessage();
			messageRaw = message.getContentRaw();
			channel = updated.getChannel();
		}
		
	} // fine identifyLatestMessage()
	
	/**Inserisce come reazioni tutte le emote che trova nel messaggio*/
	private void aggiungiReazioni()
	{
		List<Emote> emoteList = new ArrayList<>();
		
		if (message != null)
			emoteList = message.getEmotes();
		
		for (Emote emote : emoteList)
		{
			try
			{
				message.addReaction(emote).queue();
			}
			catch (Exception ignored) {}
		}
		
	} // fine aggiungiReazioni()
	
	
	/** Controlla che il messaggio abbia le parole chiave per attivare i comandi (o le reazioni) del bot*/
	public void checkForKeywords(String msgLowerCase)
	{
		final String discriminator = author.getDiscriminator();
		final String[] args = messageRaw.split(" ");
		final String comando = args[0].toLowerCase(Locale.ROOT);
		boolean reply = false;
		String msgReply = "";
		
		if (author.isBot())
		{
			if (discriminator.equals("8456"))
			{
				react("owo");
				react("vergogna");
			}
			// return a priori, se però il messaggio lo manda l'altro bot OwO prima gli mette le reazioni e poi return
			
			if (author.getDiscriminator().equals("5269")) // self own
				if (random.nextInt(1000) == 42) // 0,1%
					message.reply("BOwOt vergognati").queue(lambda -> react("vergogna"));
			
			return;
		}
		
		if (!msgLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
			spawnPokemon();
		
		if (random.nextInt(500) == 42) // chance di reagire con emote personali
		{
			var trigger = random.nextBoolean();
			
			if (trigger)
				triggera(discriminator);
			else
			{
				switch (discriminator)
				{
					case NUMOBITO ->
					{
						react("obito");
						react("vergogna");
						message.reply("Òbito vergognati").queue();
					}
					case NUMENIGMO ->
						react("pigeon");
					
					case NUMLEX ->
						channel.addReactionById(id, "🇷🇴").queue();
					
					case NUMGION ->
						react("smh");
					
				} // fine switch
				
				final String[] reazione = {"dansgame", "pigeon", "smh"};
				final var scelta = random.nextInt(reazione.length);
				
				message.reply(camelCase(messageRaw)).queue(lambda -> react(reazione[scelta]));
				
			} // fine else
			
		} // fine if reazioni
		
		
		switch (comando)
		{
			case "!coinflip", "!cf" -> coinflip();
			case "!scf" -> sassoCartaForbici();
			case "!poll" -> poll();
			case "!info" -> info();
			case "!8ball" -> eightBall();
			case "!pokemon" -> pokemon();
			case "!colpevolezza", "!colpevole" -> colpevolezza();
			case "!carta" -> sendCarta(new Card());
			case "!duello", "!duellocarte" -> duelloDiCarte();
			case "!accetto" -> accettaDuello(false);
			case "!rifiuto" -> rifiutaDuello();
			case "!massshooting", "!ms" -> massShooting();
			case "!bestemmia" -> bestemmia();
			case "!clash" -> clashCommands(messageRaw.toLowerCase());
		}
		
		// arraylist per contenere le reazioni da aggiungere al messaggio
		var reazioni = new ArrayList<String>();
		
		if (msgLowerCase.contains("pigeon") || msgLowerCase.contains("piccione"))
			reazioni.add("pigeon");
		
		if (msgLowerCase.contains("owo"))
			reazioni.add("owo");
		
		if (msgLowerCase.contains("pog"))
			reazioni.add("pog");
		
		if (msgLowerCase.contains("òbito") || msgLowerCase.contains("obito"))
			if (random.nextInt(50) == 42) // 2%
			{
				reazioni.add("obito");
				reazioni.add("vergogna");
			}
		
		if (msgLowerCase.contains("vergogna"))
			reazioni.add("vergogna");
		
		if (msgLowerCase.contains("no u"))
			reazioni.add("nou");
		
		if (msgLowerCase.contains("sabaping"))
			reazioni.add("sabaping");
		
		if (msgLowerCase.contains("get"))
			if (msgLowerCase.contains("rekt"))
				reazioni.add("getrekt");
		
		if (msgLowerCase.contains("smh"))
			reazioni.add("smh");
		
		if (msgLowerCase.contains("giorno"))
			reazioni.add("giorno");
		
		if (msgLowerCase.contains("uomo colpo") || msgLowerCase.contains("hitman") || msgLowerCase.contains("icscom") || msgLowerCase.contains("xcom"))
		{
			reazioni.add("pog");
			
			if (msgLowerCase.contains("icscom") || msgLowerCase.contains("xcom"))
				reazioni.add("xcom");
			else
				reazioni.add("hitman");
		}
		
		if (msgLowerCase.contains("poochyena"))
		{
			reazioni.add("pog");
			reazioni.add("♥");
		}
		
		if (msgLowerCase.contains("cl__z"))
		{
			reply = true;
			msgReply += "Sempre sia lodato\n";
		}
		
		if (msgLowerCase.contains("scarab"))
			reazioni.add("scarab");
		
		if (msgLowerCase.contains("ingredibile"))
			reazioni.add("ingredibile");
		
		if (msgLowerCase.contains("wtf") || msgLowerCase.contains("what the fuck"))
			react("wtf");
		
		// a questo punto smetto di controllare se ci siano reazioni e le aggiungo effettivamente al messaggio
		if (!reazioni.isEmpty())
		{
			for (String emote : reazioni)
				react(emote);
			
			reazioni.clear();
		}
		
		if (msgLowerCase.contains("russia"))
		{
			reply = true;
			msgReply += "Ucraina Est*\n";
		}
		
		if (msgLowerCase.contains("winnie the pooh") || msgLowerCase.contains("xi jinping"))
		{
			reply = true;
			msgReply += "⣿⣿⣿⣿⣿⠟⠋⠄⠄⠄⠄⠄⠄⠄⢁⠈⢻⢿⣿⣿⣿⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣿⠃⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠈⡀⠭⢿⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⡟⠄⢀⣾⣿⣿⣿⣷⣶⣿⣷⣶⣶⡆⠄⠄⠄⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⡇⢀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠄⠄⢸⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣇⣼⣿⣿⠿⠶⠙⣿⡟⠡⣴⣿⣽⣿⣧⠄⢸⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣿⣾⣿⣿⣟⣭⣾⣿⣷⣶⣶⣴⣶⣿⣿⢄⣿⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣿⣿⣿⣿⡟⣩⣿⣿⣿⡏⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣿⣿⣹⡋⠘⠷⣦⣀⣠⡶⠁⠈⠁⠄⣿⣿⣿⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣿⣿⣍⠃⣴⣶⡔⠒⠄⣠⢀⠄⠄⠄⡨⣿⣿⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⣿⣿⣿⣦⡘⠿⣷⣿⠿⠟⠃⠄⠄⣠⡇⠈⠻⣿⣿⣿⣿ \n" +
					    "⣿⣿⣿⣿⡿⠟⠋⢁⣷⣠⠄⠄⠄⠄⣀⣠⣾⡟⠄⠄⠄⠄⠉⠙⠻ \n" +
					    "⡿⠟⠋⠁⠄⠄⠄⢸⣿⣿⡯⢓⣴⣾⣿⣿⡟⠄⠄⠄⠄⠄⠄⠄⠄ \n" +
					    "⠄⠄⠄⠄⠄⠄⠄⣿⡟⣷⠄⠹⣿⣿⣿⡿⠁⠄⠄⠄⠄⠄⠄⠄⠄ \n";
		}
		
		if (msgLowerCase.equals("cancella questo messaggio"))
		{
			if (random.nextInt(50) == 42)
				message.reply("No.").queue(l -> react("getrekt"));
			else
			{
				channel.sendTyping().queue();
				pause(500, -1);
				message.delete().queue();
			}
		}
		
		if ((msgLowerCase.contains("ehil")) && author.getDiscriminator().equals("4781"))
		{
			reply = true;
			msgReply += "Salve!";
		}
			
		if (msgLowerCase.contains("non vedo l'ora") || msgLowerCase.contains("che ore sono") || msgLowerCase.contains("che ora è"))
		{
			reply = true;
			var date = new GregorianCalendar();
			var hour = date.get(Calendar.HOUR_OF_DAY);
			var minutes = date.get(Calendar.MINUTE);

			msgReply += switch (hour)
			{
				case 0 -> "È ";
				case 1 -> "È l' ";
				default -> "Sono le ";
			};

			
			if (random.nextInt(2) == 0)
			{
				msgReply += hour + ":";
				if (minutes < 10)
					msgReply += "0" + minutes + "\n";
				else
					msgReply += minutes + "\n";
			}
			else
			{
				var orario = new Ore(hour, minutes);
				
				msgReply += orario.getOra();

				msgReply += switch (minutes)
				{
					case 0 -> "";
					case 1 -> " e uno";
					default -> " e " + orario.getMinuti();
				};


			}
		}
		
		final String[] saluti = {"ciao", "buondì", "saluti", "distinti saluti", "buongiorno", "buon pomeriggio", "buonasera", "salve"};
		boolean flag = true;
		for (String s : saluti)
		{
			if (flag && msgLowerCase.contains(s))
			{
				flag = false;
				final int bound = 1000;
				if (random.nextInt(bound) < bound - 1)
					message.reply(getSaluto() + " anche a te").queue();
				else
					message.reply("No, vaffanculo >:(").queue();
			}
		}
		
		if (msgLowerCase.contains("dammi il 5") || msgLowerCase.contains("high five") || msgLowerCase.contains("dammi il cinque"))
		{
			reply = true;
			msgReply += "🤚🏻\n";
		}
		
		if (msgLowerCase.contains("grazie") && random.nextInt(50) == 42)
		{
			reply = true;
			msgReply += "Grazie, graziella e grazie al cazzo\n";
		}
		
		if (msgLowerCase.equals("cosa") && random.nextInt(20) == 1)
		{
			reply = true;
			msgReply += "Cosa? La pantera rosa";
		}
		
		if (msgLowerCase.contains("deez nuts") && discriminator.equals(NUMENIGMO))
		{
			reply = true;
			msgReply += "\"deez nuts\" DEEZ NUTS, Enigmo!";
		}
		
		if (msgLowerCase.contains("serve aiuto"))
		{
			reply = true;
			msgReply += "Nemico assente!\n";
		}
		
		//if (msgLowerCase.contains("") && random.nextInt(42) == 0){}
		
		if (reply)
			message.reply(msgReply).queue();
		
	} // fine checkForKeywords()
	
	/** Gestisce i comandi slash (ancora da implementare) */
	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		// ???

	} // fine onSlashCommand()

	/** Trasforma il testo da normale a CaMeL cAsE */
	private String camelCase(String msg)
	{
		var chars = msg.toCharArray();
		var len = chars.length;
		char c;
		
		for (int i = 0; i < len; i++)
		{
			c = chars[i];
			chars[i] = (i % 2 == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
		}
		
		return new String(chars);
	} // fine camelCase()
	
	/** Invia una carta in chat come se fosse stata pescata dal mazzo */
	private void sendCarta(Card carta)
	{
		final var titolo = titoloCarta(carta);
		final var immagineCartaAPI = linkImmagine(carta);
		final var color= coloreCarta(carta);
		final var seme = semeCarta(carta);
		var embed = new EmbedBuilder()
			.setTitle(titolo)
			.setImage(immagineCartaAPI)
			.setColor(color)
			.setFooter(seme);
		
		channel.sendMessageEmbeds(embed.build()).queue();
		
	} // fine sendCarta
	
	/** Restituisce il titolo della carta sotto forma di stringa */
	public String titoloCarta(Card carta)
	{
		return carta.getValoreString() + " di " + carta.getSeme();
	}
	
	/** Restituisce il link dell'immagine della carta sotto forma di stringa */
	public String linkImmagine(Card carta)
	{
		return carta.getLink();
	}
	
	/** Restituisce il colore della carta sotto forma di Color */
	public Color coloreCarta(Card carta)
	{
		return carta.getSeme().equals("Cuori") || carta.getSeme().equals("Quadri") ? Color.red : Color.black;
	}
	
	/** Restituisce il valore del seme della carta sotto forma di stringa */
	public String semeCarta(Card carta)
	{
		return switch (carta.getSeme())
		{
			case "Cuori" -> simboli[0];
			case "Quadri" -> simboli[1];
			case "Fiori" -> simboli[2];
			case "Picche" -> simboli[3];
			default -> null;
		};
	}
	
	/** Permette a due persone di pescare una carta ciascuno. Vince il valore maggiore. */
	private void duelloDiCarte()
	{
		if (duelloAttivo)
		{
			channel.sendMessage("C'è già un duello in atto! Attendi la fine dello scontro per iniziarne un altro.").queue();
			return;
		}
		
		var utenti = message.getMentionedUsers();
		var autore = author.getDiscriminator();
		final var link = "https://i.kym-cdn.com/photos/images/original/001/228/324/4a4.gif";
		if (utenti.isEmpty())
			channel.sendMessage("Devi menzionare un utente per poter duellare!\n`!duello @utente`").queue();
		else if (utenti.get(0).isBot())
		{
			if (utenti.get(0).getDiscriminator().equals("5269"))
			{
				setDuel(utenti.get(0));
				accettaDuello(true);
			}
			else
				channel.sendMessage("**" + authorName + ", non duellerai con alcun bot all'infuori di me**").queue(m -> react("smh"));
		}
		else if (utenti.get(0).getDiscriminator().equals(autore))
			channel.sendMessageEmbeds(new EmbedBuilder().setImage(link).setColor(0xFF0000).build()).queue(m -> react("pigeon"));
		else
		{
			channel.sendMessage(authorName+" ti sfida a duello! Accetti, <@" + utenti.get(0).getId() + ">?"
					                           + "\n*Per accettare, rispondi con* `!accetto`"
					                           + "\n*Per rifiutare, rispondi con* `!rifiuto`").queue();
			setDuel(utenti.get(0));
			//TODO: passare al metodo nuovo di sfida
			// quale?
		}
	} // fine duelloDiCarte()
	
	/** Se il duello inizia, imposta le variabili "duelloAttivo", "sfidante" e "sfidato" */
	private void setDuel(User utente)
	{
		duelloAttivo = true;
		sfidante = author;
		sfidato = utente;
	} // fine attivaDuello()
	
	/** Permette allo sfidato di accettare il duello */
	private void accettaDuello(boolean flag)
	{
		if (!duelloAttivo || sfidato == null)
		{
			react("pigeon");
			return;
		}
		
		var embed = new EmbedBuilder();
		int[] valori = new int[2];
		Card[] carte = new Card[2];
		User[] duellanti = new User[2];
		String[] messaggioVittoria =
		{
			"Vince lo sfidante: **" + sfidante.getName() + ".**",
			"Vince lo sfidato: **" + sfidato.getName() + ".**",
			"WTF, avete pescato la stessa carta dal mazzo? Vergognatevi."
		};
		
		if (flag || author.getDiscriminator().equals(sfidato.getDiscriminator()))
		{
			Card cardSfidante, cardSfidato;
			cardSfidante = new Card();
			cardSfidato = new Card();
			
			carte[0] = cardSfidante;
			carte[1] = cardSfidato;
			
			duellanti[0] = sfidante;
			duellanti[1] = sfidato;

			valori[0] = cardSfidante.getValoreInt();
			valori[1] = cardSfidato.getValoreInt();
			
			channel.sendTyping().queue();
			pause(500, 500);
			
			for (int i = 0; i < 2; i++)
			{
				embed
					.setTitle("Carta di " + duellanti[i].getName())
					.setImage(linkImmagine(carte[i]))
					.setColor(coloreCarta(carte[i]))
					.setFooter(semeCarta(carte[i]) + " " + titoloCarta(carte[i]));
		
				channel.sendMessageEmbeds(embed.build()).queue();
			}
			
			if (valori[0] > valori[1])
				channel.sendMessage(messaggioVittoria[0]).queue();
			else if (valori[0] < valori[1])
				channel.sendMessage(messaggioVittoria[1]).queue();
			else
			{
				// valori uguali? Allora confrontiamo i semi per decidere il risultato.
				// siccome i valori sono uguali, riciclo le variabili
				
				valori[0] = cardSfidante.getSemeInt();
				valori[1] = cardSfidante.getSemeInt();
				
				if (valori[0] < 1 || valori[1] < 1)
					channel.sendMessage("Errore Catastrofico:" +
							"\nValori[0] = " + valori[0] +
							"\nValori[1] = " + valori[1] +
							"\nAutodistruzione imminente.")
							.queue();
				
				if (valori[0] > valori[1])
					channel.sendMessage(messaggioVittoria[0]).queue();
				else if (valori[0] < valori[1])
					channel.sendMessage(messaggioVittoria[1]).queue();
				else
					channel.sendMessage(messaggioVittoria[2]).queue(lambda -> react("vergogna"));
				
			}
			
			resetDuel();
		}
	} // fine accettaDuello()
	
	/** Permette al duellante di rifiutare/ritirarsi il/dal duello prima che inizi */
	public void rifiutaDuello()
	{
		if (!duelloAttivo)
		{
			channel.sendMessage("Non c'è nessun duello, smh.").queue(lambda -> react("smh"));
			return;
		}
		
		final boolean x = author.getDiscriminator().equals(sfidato.getDiscriminator());
		
		String messaggioRifiuto = String.format("Lo %s %s il duello.",
				x ? "sfidato" : "sfidante",
				x ? "rifiuta" : "ritira");
		
		channel.sendMessage(messaggioRifiuto).queue();
		
		resetDuel();
	} // fine rifiutaDuello()
	
	/** Resetta le variabili "duelloAttivo", "sfidante" e "sfidato" a false, null e null rispettivamente */
	private void resetDuel()
	{
		duelloAttivo = false;
		sfidante = null;
		sfidato = null;
	} // fine resetDuel()
	
	/** Lancia una moneta */
	public void coinflip()
	{
		final String testaEmote = "<:pogey:733659301645910038>";
		final String croceEmote = "<:pigeon:647556750962065418>";
		String lancioMoneta = authorName + " lancia una moneta...";

		var headsOrTails = random.nextBoolean();
		var responso = lancioMoneta+"\n**È uscito** ";
		var testaStringa = "**"+testaEmote+"! (Testa)**";
		var croceStringa = "**"+croceEmote+"! (Croce)**";

		var finalResponso = responso.concat(headsOrTails ? testaStringa : croceStringa);

		channel.sendTyping().queue();
		pause(500, 500);
		message.reply(lancioMoneta).queue(m ->
		{
			pause(500, 500);
			message.editMessage(finalResponso).queue(m2 -> react(headsOrTails ? "pog" : "pigeon"));
		});

	} // fine coinflip()
	
	/** Genera una partita di Sasso-Carta-Forbici, sia contro il bot che contro un giocatore */
	public void sassoCartaForbici()
	{
		final int sfida = 1;
		final var immagineGiancarlo = "https://i.pinimg.com/originals/a7/68/bb/a768bbbb169aac9f0b445c80fa3b039a.jpg";
		String[] opzioni = {"sasso", "carta", "forbici"};
		var msgSpezzato = messageRaw.toLowerCase(Locale.ROOT).split(" ");
		var listaUtenti = message.getMentionedUsers();
		
		if (messageRaw.length() < 5 || msgSpezzato[1].isEmpty())
		{
			var embed = new EmbedBuilder()
				.setTitle("Sasso / Carta / Forbici")
				.setColor(Color.red)
				.addField("Utilizzo", "Scrivi `!scf` <\"sasso\" oppure \"carta\" oppure \"forbici\">", false);
			
			channel.sendMessageEmbeds(embed.build()).queue();
			
			return;
		}
		
		if (listaUtenti.isEmpty()) // gioca contro il bot
		{
			sceltaBot = opzioni[random.nextInt(3)];
			var sceltaUtente = msgSpezzato[1].toLowerCase(Locale.ROOT);
			
			if (!(sceltaUtente.equals("sasso") || sceltaUtente.equals("forbici") || sceltaUtente.equals("forbice") || sceltaUtente.equals("carta")))
			{
				channel.sendMessage("Non è una scelta valida, smh").queue(m -> react("smh"));
				return;
			}
			
			sceltaUtente = capitalize(sceltaUtente);
			sceltaBot = capitalize(sceltaBot);
				
			var embed = new EmbedBuilder()
				.setTitle("**SASSO/CARTA/FORBICI**")
				.setColor(Color.red)
				.addField("Tu hai scelto", "**" + sceltaUtente + "**", true)
				.addField("Io ho scelto", "**" + sceltaBot + "**", true)
				.setImage(immagineGiancarlo)
				.setFooter("Non siamo uguali.")
				.build();
			channel.sendMessageEmbeds(embed).queue();
			
			if (sceltaUtente.equalsIgnoreCase(sceltaBot))
				channel.sendMessage("Ingredibile, abbiamo scelto entrambi **" + sceltaBot + "**! Pareggio.").queue();
			else if (sceltaUtente.equalsIgnoreCase("sasso"))
			{
				if (sceltaBot.equalsIgnoreCase("carta"))
					channel.sendMessage("La carta avvolge il sasso. Hai perso.").queue();
				else
					channel.sendMessage("Il sasso rompe le forbici. Hai vinto!").queue();
			}
			else if (sceltaUtente.equalsIgnoreCase("carta"))
			{
				if (sceltaBot.equalsIgnoreCase("forbici"))
					channel.sendMessage("Le forbici tagliano la carta. Hai perso.").queue();
				else
					channel.sendMessage("La carta avvolge il sasso. Hai vinto!").queue();
			}
				
				
			return;
		}
		
		// A questo punto la lista utenti non è vuota, quindi gioca contro un utente
		
		//TODO: definire la sfida con l'utente
		
		setSfida(listaUtenti.get(0), challenge[sfida]);
		
		
	} // fine sassoCartaForbice()
	
	/**Prende in input una stringa e cambia la prima lettera da minuscola in maiuscola*/
	private String capitalize(String str)
	{
		// se la stringa è nulla/vuota oppure la prima lettera è già maiuscola, ignora
		if(str == null || str.isEmpty() || str.substring(0,1).matches("[A-Z]"))
			return str;
		
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/** Crea un oggetto di tipo Challenge e tiene conto di chi è sfidato, chi è lo sfidante e su quale gioco. */
	private void setSfida(User sfidato, String tipoSfida)
	{
		for (Challenge challenge : listaSfide)
		{
			if (challenge.getSfidante().equals(author))
			{
				if (challenge.getSfidato().equals(sfidato))
				{
					if (challenge.getTipoSfida().equals(tipoSfida))
					{
						channel.sendMessage("Voi due avete già una sfida in corso.").queue();
						return;
					}
					else
					{
						listaSfide.add(new Challenge(sfidante, sfidato, tipoSfida));
						sfidaAttiva = true;
					}
				}
			}
		}
		
	} // fine setSfida()
	
	/** Controlla che ci sia una sfida in atto, che chi ha inviato il comando di accettazione sia effettivamente
	 * lo sfidato e gestisce la sfida in corso */
	public void accettaSfida()
	{
		if (!sfidaAttiva || !authorName.equals(sfidato.getName()))
		{
			// se non c'è sfida o la persona che invia il comando non è lo sfidato => ignora
			channel.sendMessage("Non sei stato sfidato, vergognati").queue( lambda -> react("vergogna"));
			return ;
		}
		
		// se si arriva qui vuol dire che c'è una sfida attiva
		// e la persona che ha inviato il comando è proprio lo sfidato
		for (Challenge challenge : listaSfide)
			if (author.equals(challenge.getSfidato()))
				if (sfidante.equals(challenge.getSfidante()))
					switch (challenge.getTipoSfida())
					{
						case "Duello Carte" -> duelloDiCarte();
						case "Sasso Carta Forbici" -> sassoCartaForbici();
					}
		
	} // fine accettaSfida()
	
	/** Verifica ci siano le condizioni giuste per creare un sondaggio */
	public void poll()
	{
		// args[0] = "!poll"
		// args[1] = domanda
		// args[2, 3, ...] = risposte
		
		if (messageRaw.length() <= 5)
		{
			sondaggio("Pog?", new String[]{"Pog sì", "Pog no", "Porgo Tensing"}, true);
			//flag = true fa comparire il messaggio di utilizzo del comando !poll
			return;
		}
		
		String[] domandaERisposte = messageRaw.split("\\?");
		String domanda = domandaERisposte[0].substring("!poll".length());
		String[] risposte = messageRaw.substring("!poll".length()+domanda.length()+1).split("/");
		
		//System.out.printf("DomandaERisposte length: %d\nDomandaERisposte: %s\nDomanda length: %d\nDomanda: %s\nRisposte.length: %d\nRisposte: %s\n", domandaERisposte.length, Arrays.toString(domandaERisposte), domanda.length(), domanda, risposte.length, Arrays.toString(risposte));
		
		sondaggio(domanda, risposte, false);
	} // fine poll()
	
	/** Crea un sondaggio. Se non sono soddisfatte le condizioni, mostra un messaggio su come usare il comando !poll */
	public void sondaggio(String domanda, String[] risposte, boolean flag)
	{
		risposte[0] = risposte[0].substring(0, risposte[0].length()-1).trim();
		var sleepInterval = random.nextInt(500) + 1000;
		final var size = risposte.length;
		var embedBuilder = new EmbedBuilder();
		final String[] letters =
		{
			"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB",
			"\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
			"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7",
			"\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
			"\uD83C\uDDFE", "\uD83C\uDDFF"
		}; // array di lettere emoji A -> Z
		
		if (size < 2 || size > letters.length || flag)
		{
			//embedBuilder.setFooter("");
			embedBuilder.setTitle("`!poll` - Istruzioni per l'uso");
			embedBuilder.addField("Sondaggio", "Per creare un sondaggio devi usare il comando `!poll` + `domanda?` + `[risposte]`\nSepara le risposte con uno slash `/`.", false);
			embedBuilder.addField("Esempio", "`!poll domanda? opzione 1 / opzione 2 / opzione 3 ...`\n`!poll Cosa preferite? Pizza / Pollo / Panino / Sushi`", false);
			embedBuilder.addField("Votazione", "Per votare, usa le reazioni!", false);
			embedBuilder.setColor(0xFFFFFF);
			
			channel.sendTyping().queue();
			try { Thread.sleep(sleepInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			channel.sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else
		{
			String descrizione = "";
			embedBuilder.setTitle(domanda+"?");
			var lenghtRisposte = risposte.length;
			for (int i = 0; i < lenghtRisposte; i++)
				descrizione = descrizione.concat(letters[i] + "\t" + risposte[i]) + "\n";
			embedBuilder.setDescription(descrizione);
			embedBuilder.setColor(0xFF0000);
			
			channel.sendTyping().queue();
			
			try { Thread.sleep(sleepInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			
			channel.sendMessageEmbeds(embedBuilder.build()).queue((message) ->
			{
				for (int i = 0; i < size; i++)
					message.addReaction(letters[i]).queue();
			});
			
		}
		
	} // fine sondaggio()

	/** Infastidisce le persone */
	public void triggera(String discriminator)
	{
		String title, image, footer, color;
		int risultato;
		
		final String titolo = "Get rekt ";

		final String[] immagineObito =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fstocks2.com%2Fassets%2Fstocks%2Fgm%2Fgme%2Fgme-daily-rsi.png&f=1&nofb=1",
			"https://scontent.fnap5-1.fna.fbcdn.net/v/t1.6435-9/36686816_812263365633695_5089614023322763264_n.jpg?_nc_cat=102&ccb=1-5&_nc_sid=174925&_nc_ohc=sFL5GEX7jv4AX_KyE9g&_nc_ht=scontent.fnap5-1.fna&oh=00_AT_oGm-J17HN6LGRMIH_NOSDf3Fya36yAs9gtKmHKu-qxg&oe=6219896F"
		};

		final String[] immagineEnigmo =
		{
			"https://ramenparados.com/wp-content/uploads/2021/01/21.png"
		};
		
		final String[] immagineLex =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fst.motortrendenespanol.com%2Fuploads%2Fsites%2F5%2F2017%2F07%2FTesla-Model-3-lead-.jpg&f=1&nofb=1",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fvitaitaliantours.com%2Fwp-content%2Fuploads%2F2016%2F03%2FNeapolitan-Pizza-Margherita.jpg&f=1&nofb=1"
		};

		
		final String[] immagineGion =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2F1.bp.blogspot.com%2F-9rnsA1ZxZy8%2FVetkw9DnL9I%2FAAAAAAAABA0%2F7h5Svdbg9zw%2Fs1600%2Fminions%2B2015%2Bscreenshot%2B4.jpg&f=1&nofb=1",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.vox-cdn.com%2Fthumbor%2Fs3AsF2kLAo350xTuRsw7lxgs2Qg%3D%2F0x0%3A2000x1333%2F1200x800%2Ffilters%3Afocal(840x507%3A1160x827)%2Fcdn.vox-cdn.com%2Fuploads%2Fchorus_image%2Fimage%2F55188531%2Fhawaiian_pizza_sh.0.jpg&f=1&nofb=1"
		};
		
		final String testoFooter = "";
		
		channel.sendTyping().queue();
		pause(1000, 0);

		var embedBuilder = new EmbedBuilder();
		
		switch (discriminator)
		{
			case "2804" -> // Òbito
			{
				risultato = random.nextInt(immagineObito.length);
				title = titolo.concat("Òbito");
				image = immagineObito[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0xFFFFFF" : "0xC59FC9";
			}
			
			case "7166" -> // Enigmo
			{
				risultato = random.nextInt(immagineEnigmo.length);
				title = titolo.concat("Enigmo");
				image = immagineEnigmo[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0xCB4D4D" : "0xE5D152";
			}
			
			case "2241" -> // Lex
			{
				risultato = random.nextInt(immagineLex.length);
				title = titolo.concat("Lex");
				image = immagineLex[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0xD80000" : "0x207522";
			}
			
			case "0935" -> // Gion
			{
				risultato = random.nextInt(immagineGion.length);
				title = titolo.concat("Gion");
				image = immagineGion[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0XDDCD4f" : "0xEAE28A";
			}
			
			
			default ->
			{
				title = "Errore";
				image = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F55%2FQuestion_Mark.svg%2F1200px-Question_Mark.svg.png&f=1&nofb=1";
				footer = "Errore ingredibile";
				color = "0x424242";
			}
		}
		
		embedBuilder.setTitle(title);
		embedBuilder.setImage(image);
		embedBuilder.setFooter(footer);
		embedBuilder.setColor(Color.decode(color));

		channel.sendMessageEmbeds(embedBuilder.build()).queue();
	
	} // fine triggera()

	/** Aggiunge una reazione all'ultimo messaggio inviato */
	public void react(String emote)
	{
		final String emoteOwO = "OwO:604351952205381659";
		final String emoteNou = "nou:671402740186087425";
		final String emotePigeon = "pigeon:647556750962065418";
		final String emotePog = "pogey:733659301645910038";
		final String[] emoteObito = {"obi:670007761760681995", "ito:670007761697898527"};
		final String[] emoteVergognati = {"vergognati:670009511053885450", "vergognati2:880100281315098685"};
		final String[] emoteSabaPing = {"leftPowerUp:785565275608842250", "sabaPing:785561662605885502", "rightPowerUp:785565774953709578"};
		final String emoteGetRekt = "getrekt:742330625347944504";
		final String emoteSmh = "smh:880423534365659176";
		final String emoteGiorno = "GiOrNo:618591225582321703";
		final String emoteDansGame = "dansgame:848955120157720576";
		final String emoteIngredibile = "ingredibile:593532244434485259";
		final String[] emoteScarab = {"leftPowerUp:785565275608842250", "scarab:847008906994778122", "rightPowerUp:785565774953709578"};
		final String emoteWTF = "WTF:670033776524656641";
		final int[] emoteHitman = {7, 8, 19, 12, 0, 13}; // posizioni lettere nell'alfabeto
		final int[] emoteXCOM = {23, 2, 14, 12}; // posizioni lettere nell'alfabeto
		final String[] letters =
		{
			"\uD83C\uDDE6", // A
			"\uD83C\uDDE7", // B
			"\uD83C\uDDE8", // C
			"\uD83C\uDDE9", // D
			"\uD83C\uDDEA", // E
			"\uD83C\uDDEB", // F
			"\uD83C\uDDEC", // G
			"\uD83C\uDDED", // H
			"\uD83C\uDDEE", // I
			"\uD83C\uDDEF", // J
			"\uD83C\uDDF0", // K
			"\uD83C\uDDF1", // L
			"\uD83C\uDDF2", // M
			"\uD83C\uDDF3", // N
			"\uD83C\uDDF4", // O
			"\uD83C\uDDF5", // P
			"\uD83C\uDDF6", // Q
			"\uD83C\uDDF7", // R
			"\uD83C\uDDF8", // S
			"\uD83C\uDDF9", // T
			"\uD83C\uDDFA", // U
			"\uD83C\uDDFB", // V
			"\uD83C\uDDFC", // W
			"\uD83C\uDDFD", // X
			"\uD83C\uDDFE", // Y
			"\uD83C\uDDFF"  // Z
		};
		
		var emoteDaUsare = switch (emote)
		{
			case "pigeon" -> emotePigeon;
			case "nou" -> emoteNou;
			case "owo" -> emoteOwO;
			case "pog" -> emotePog;
			case "vergogna" -> emoteVergognati[random.nextInt(2)];
			case "getrekt" -> emoteGetRekt;
			case "smh" -> emoteSmh;
			case "giorno" -> emoteGiorno;
			case "dansgame" -> emoteDansGame;
			case "ingredibile" -> emoteIngredibile;
			case "wtf" -> emoteWTF;
			default -> "";
		};

		if (emote.equals("obito"))
			for (String str : emoteObito)
				channel.addReactionById(id, str).queue();
		
		if (emote.equals("sabaping"))
			for (String str : emoteSabaPing)
				channel.addReactionById(id, str).queue();
		
		if (emote.equals("hitman"))
			for (int i : emoteHitman)
				channel.addReactionById(id, letters[i]).queue();
		
		if (emote.equals("xcom"))
			for (int i : emoteXCOM)
				channel.addReactionById(id, letters[i]).queue();
		
		if (emote.equals("scarab"))
			for (String str : emoteScarab)
				channel.addReactionById(id, str).queue();
		
		if (!emoteDaUsare.equals(""))
		{
			try
			{
				channel.addReactionById(id, emoteDaUsare).queue();
			}
			catch (Exception e) { System.out.printf("Errore nell'aggiunta della reazione \"%s\"\n\t", emoteDaUsare); }
		}
		
	} // fine react()
	
	/** Lascia che RNGesus decida quanto è colpevole l'utente taggato */
	private void colpevolezza()
	{
		var utenteTaggato = message.getMentionedUsers();
		final String urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		
		if (utenteTaggato.isEmpty())
		{
			var emb = new EmbedBuilder()
				.setColor(Color.red)
				.setTitle("Scrivi `!colpevole <@utente> per usare questo comando`");
			channel.sendMessageEmbeds(emb.build()).queue();
		}
		else if (utenteTaggato.get(0).getDiscriminator().equals(author.getDiscriminator()))
			message.reply("Congratulazioni, sei colpevole al 100%.").queue(lambda -> react("pigeon"));
		else
		{
			final int colpa = random.nextInt(100) + 1;
			final String utente = utenteTaggato.get(0).getName();
			String[] particella = {"al ", "all'"};
			int index = switch (colpa)
			{
				case 1, 8, 11, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89 -> 1;
				default -> 0;
			};
			
			final String risposta = String.format("%s sostiene che %s sia colpevole %s%d%%", authorName, utente, particella[index], colpa);
			
			var embed = new EmbedBuilder()
				.setTitle(risposta)
				.setColor(0xFF0000)
				.setFooter("", urlOwO);
			
			channel.sendMessageEmbeds(embed.build()).queue(lambda ->
			{
				if (colpa < 20)
					react("pigeon");
				else if (colpa < 50)
					react("smh");
				else if (colpa < 80)
					react("dansgame");
				else
					react("pog");
			});
		}
		
	} // fine colpevolezza()
	
	/** Mostra un embed con le informazioni del bot */
	public void info()
	{
		var embedBuilder = new EmbedBuilder();
		var size = listaComandi.length;

		String urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		String urlTitle = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
		
		embedBuilder.setTitle("Informazioni", urlTitle);
		embedBuilder.setDescription("Questo bot permette di lanciare monete, creare sondaggi e, soprattutto, essere un rompiballe.");

		for (int i = 0; i < size; i++)
			embedBuilder.addField("`"+listaComandi[i]+"`", "*"+listaDescrizioni[i]+"*", false);
		
		embedBuilder.setThumbnail(urlOwO);
		embedBuilder.setColor(0xFF0000);
		embedBuilder.addBlankField(false);
		embedBuilder.setFooter("Creato con ❤️ da JohnWeak", urlOwO);
		
		MessageEmbed embed = embedBuilder.build();
		channel.sendMessageEmbeds(embed).queue();
		
	} // fine info()
	
	/** Genera un responso usando la magica palla 8 */
	public void eightBall()
	{
		final String ballResponse = "La 🎱 dichiara... ";
		final String[] risposte =
		{
			"Sì.",
			"È certo.",
			"È decisamente un sì.",
			"Senza dubbio.",
			"Puoi contarci.",
			"Molto probabile.",
			"Il responso è positivo.",
			"I segni presagiscono di sì.",
			"Il presagio non è né positivo né negativo.",
			"Non ci contare.",
			"La mia risposta è no.",
			"Le mie fonti dicono di no.",
			"Il responso non è favorevole.",
			"Ci sono molti dubbi al riguardo.",
			"Gli astri non ti sorridono.",
			"No."
		};

		channel.sendTyping().queue();

		pause(-1, -1);

		message.reply(ballResponse).queue(message1 ->
		{
			String newResponse = "La 🎱 dichiara: ";
			pause(-1,1500);
			message1.editMessage(newResponse+"**"+risposte[random.nextInt(risposte.length)]+"**").queue();
		});
		
	} // fine eightBall()

	/** Mette in pausa il thread per un totale di secondi pari a millisecondi + un valore casuale fra 0 e 'bound'.
	 * Parametri negativi faranno sì che vengano usati i valori di default (millis=1500 e bound=500) */
	private void pause(int millis, int bound)
	{
		if (millis < 1)
			millis = 1500;

		if (bound < 1)
			bound = 500;

		try { Thread.sleep(millis+random.nextInt(bound)); }
		catch (InterruptedException e) { e.printStackTrace(); }
	} // fine pause()
	
	/** Cerca un Pokemon nell'API. Se non lo trova mostra un messaggio di errore. */
	public void pokemon()
	{
		String msgLowercase = messageRaw.toLowerCase(Locale.ROOT);
		String[] msg = msgLowercase.split(" ");

		if (msgLowercase.contains("!pokemon"))
		{
			String[] tipo = {" ", " "};
			String generazione;
			String numeroPokedex;
			String[] lineaEvolutiva = {"1", "2", "3"};

			if (msg.length > 1 && !msg[1].isEmpty())
			{
				String nome = msg[1];
				canaleBotPokemon.sendTyping().queue();
				JSONArray jsonArray = search(nome);

				try
				{
					JSONObject jsonObject = (JSONObject) jsonArray.get(0);
					String description = (String) jsonObject.get("description");
					JSONArray types = (JSONArray) jsonObject.get("types");
					JSONObject family = (JSONObject) jsonObject.get("family");
					JSONArray evoLine = (JSONArray) family.get("evolutionLine");

					for (int i = 0; i < types.size(); i++)
						tipo[i] = types.get(i).toString();

					generazione = String.valueOf(jsonObject.get("gen"));
					numeroPokedex = (String) jsonObject.get("number");

					for (int i = 0; i < evoLine.size(); i++)
						lineaEvolutiva[i] = evoLine.get(i).toString();
					
					boolean flag = (msg.length > 2) && (msg[2].equals("shiny") || msg[2].equals("s"));
					var pokemon = new Pokemon(nome, description, flag);
					
					pokemon.setTipo(tipo);
					pokemon.setGenerazione(generazione);
					pokemon.setDexNumber(numeroPokedex);
					pokemon.setLineaEvolutiva(lineaEvolutiva);

					channel.sendTyping().queue();
					pause(1000, 500);
					channel.sendMessageEmbeds(buildEmbed(pokemon, true).build()).queue();
				}
				catch (IndexOutOfBoundsException e)
				{
					final String testo = "Il Pokedex non ha informazioni riguardo `" + nome + "`.";
					channel.sendMessage(testo + "\n" + e).queue();
					e.printStackTrace();
				}
			}
			else
				channel.sendMessage("Usa `!pokemon <nome> [shiny / s]` per cercare un Pokemon").queue();
			
		}
		else
		{
			var pokemon = new Pokemon();

			if (random.nextInt(20) == 9)
				doubleEncounter(pokemon, new Pokemon());
			else
				singleEncounter(pokemon);
		}

	} // fine pokemon()

	/** Genera un incontro con un pokemon selvatico */
	private void singleEncounter(Pokemon pokemon)
	{
		EmbedBuilder embedBuilder;
		String[] nomi = {pokemon.getNome(), ""};
		final var titolo = "A wild " + pokemon.getNome() + " appears!";
		embedBuilder = buildEmbed(pokemon, false).setTitle(titolo);
		canaleBotPokemon.sendTyping().queue();
		pause(500, 500);

		sendMessage(nomi, embedBuilder);

	} // fine singleEncounter

	/** Effettua la ricerca del pokemon nell'API */
	private JSONArray search(String pokemon)
	{
		URL url;
		JSONArray jsonArray = new JSONArray();
		JSONParser jsonParser = new JSONParser();

		try
		{
			url = new URL("https://pokeapi.glitch.me/v1/pokemon/" + pokemon);
			Scanner scanner = new Scanner(nomiPkmn);
			while (scanner.hasNext())
				if (pokemon.equalsIgnoreCase(scanner.nextLine()))
				{
					var connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("Accept", "application/json");
					
					var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					var response = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null)
							response.append(inputLine);

					jsonArray = (JSONArray) jsonParser.parse(String.valueOf(response));
				}
		} catch (IOException | ParseException e) { System.out.println("Errore nell'apertura del file: " + nomiPkmn); }

		return jsonArray;
	} // fine search()
	
	/** Genera un doppio incontro con Pokemon selvatici */
	private void doubleEncounter(Pokemon uno, Pokemon due)
	{
		EmbedBuilder embedBuilder;
		String[] titolo = {"Primo Pokemon!", "Secondo Pokemon!"};
		Pokemon[] pokemons = {uno, due};
		var nomi = new String[] { uno.getNome(), due.getNome() };
		canaleBotPokemon.sendMessage("Doppio Incontro!").queue();
		
		for (int i = 0; i < 2; i++)
		{
			embedBuilder = buildEmbed(pokemons[i], false);
			embedBuilder.setDescription(titolo[i]);
			//embedBuilder.setFooter("Catturalo con !catch","https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Images.png");

			sendMessage(nomi, embedBuilder);
		}
		
		System.out.printf("\n\tUno: %s, shiny: %s\n\tDue: %s, shiny: %s\n\t",uno.getNome(), uno.isShiny(), due.getNome(), due.isShiny());
	} // fine

	/** Manda il messaggio con i Pokemon nel canale e aggiunge le reazioni like/dislike al messaggio */
	private void sendMessage(String[] pokemonNames, EmbedBuilder embedBuilder)
	{
		canaleBotPokemon.sendMessageEmbeds(embedBuilder.build()).queue((message ->
		{
			try
			{
				if (pokemonNames[0].equalsIgnoreCase("poochyena") || pokemonNames[1].equalsIgnoreCase("poochyena"))
				{
					react("pog");
					message.addReaction("❤️").queue();
				}
				else
				{
					message.addReaction("👍🏻").queue();
					message.addReaction("❤️").queue();
					message.addReaction("👎🏻").queue();
				}
			}
			catch (ArrayIndexOutOfBoundsException ignored) {}
		}));

	} // fine sendMessage()

	/** Genera un embed con il Pokemon */
	private EmbedBuilder buildEmbed(Pokemon pokemon, boolean pokedex)
	{
		var embedBuilder = new EmbedBuilder();
		String descrizione;
		String[] tipi = pokemon.getTipo();
		var stringBuilder = new StringBuilder();
		var types = "";
		String[] lineaEvo = pokemon.getLineaEvolutiva();
		String lineaEvolutiva;

		if (pokedex)
		{
			stringBuilder.append(tipi[0]);
			if (!(tipi[1].equals(" ")))
			{
				stringBuilder.append(" / ").append(tipi[1]);
			}
			types = String.valueOf(stringBuilder);
			stringBuilder.delete(0, stringBuilder.length()); // pulizia per riciclarlo per la linea evolutiva
			stringBuilder.append(lineaEvo[0]); //esiste per forza
			if (!(lineaEvo[1].equals("2")))
			{
				stringBuilder.append(" > ").append(lineaEvo[1]);
				if (!(lineaEvo[2]).equals("3"))
				{
					stringBuilder.append(" > ").append(lineaEvo[2]);
				}
			}
			else
			{
				stringBuilder.append(" doesn't evolve.");
			}
			lineaEvolutiva = String.valueOf(stringBuilder);
			final String iconURL = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F53%2FPok%25C3%25A9_Ball_icon.svg%2F1026px-Pok%25C3%25A9_Ball_icon.svg.png&f=1&nofb=1";
			embedBuilder.setFooter(""+lineaEvolutiva, ""+iconURL);

		}
		embedBuilder.setTitle(pokemon.getNome().toUpperCase());
		if ((descrizione = pokemon.getDescrizione()) != null)
		{
			String type;
			if (tipi[1].equals(" "))
				type = "Type";
			else
				type = "Types";

			embedBuilder.addField("**"+type+"**", ""+types, true);
			embedBuilder.addField("Generation", ""+pokemon.getGenerazione(), true);
			embedBuilder.addField("National Dex", ""+pokemon.getDexNumber(), true);

			embedBuilder.addField("Pokedex Entry", "*"+descrizione+"*", false);
			embedBuilder.setThumbnail(pokemon.getImg());
		}
		else
		{
			embedBuilder.setImage(pokemon.getImg());
		}
		
		var color = pokemon.isShiny() ? 0xFFD020 : 0xFF0000;
		embedBuilder.setColor(color);
		
		if (pokemon.isShiny())
			embedBuilder.setFooter("✨ Shiny! ✨");

		return embedBuilder;
	} // fine buildEmbed()
	
	/** Fa spawnare un Pokemon */
	public void spawnPokemon()
	{
		int[] valori = new int[2];
		Scanner scanner;
		FileWriter fileWriter;

		try
		{
			scanner = new Scanner(Commands.valori);
			for (int i = 0; i < 2; i++)
				valori[i] = scanner.nextInt();

			scanner.close();

			limite = valori[0];
			messaggiInviati = valori[1];
		}
		catch (FileNotFoundException e) { System.out.println("File non trovato!"); }

		//valori[0] : limite (max) messaggi
		//valori[1] : messaggiInviati
		
		if (messaggiInviati == valori[0])
		{
			pokemon(); // genera un incontro
			messaggiInviati = 0; // resetta il contatore
			limite = random.nextInt(40) + 10; // genera un nuovo max per i messaggi
		}
		else
		{
			messaggiInviati++;
		}
		valori[0] = limite;
		valori[1] = messaggiInviati;
		
		try
		{
			fileWriter = new FileWriter(Commands.valori);
			fileWriter.write(valori[0]+"\n"+valori[1]);
			fileWriter.close();

		} catch (IOException e) { System.out.println("Errore nella scrittura del file!"); }
		
	} // fine spawnPokemon()
	
	/**Ottieni un resoconto della sparatoria più recente in USA oppure ottieni un resoconto di una sparatoria scelta casualmente nell'anno da te specificato.*/
	private void massShooting()
	{
		int anno = currentYear;
		var msg = messageRaw.toLowerCase().split(" ");
		if (msg.length > 1)
			try
			{
				anno = Integer.parseInt(msg[1]);
			}
			catch (NumberFormatException e) {e.printStackTrace();}
		
		
		if (anno < 2013 || anno > currentYear)
		{
			channel.sendMessage("`L'anno dev'essere compreso fra il 2013 e il "+currentYear+".`").queue();
			return;
		}
		
		JSONArray jsonArray;
		JSONParser jsonParser = new JSONParser();
		
		try
		{
			final var url = new URL("https://mass-shooting-tracker-data.s3.us-east-2.amazonaws.com/"+anno+"-data.json");
			
			var connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			
			var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			var response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			
			jsonArray = (JSONArray) jsonParser.parse(String.valueOf(response));
			var objs = new ArrayList<JSONObject>();
			var mortiAnno = 0;
			for (Object o : jsonArray)
			{
				objs.add((JSONObject) o);
				mortiAnno += Integer.parseInt((String)((JSONObject) o).get("killed"));
			}
			
			var scelta = 0; // se è anno corrente, prende più recente, altrimenti ne prende una a caso
			
			if (anno != currentYear)
				scelta = random.nextInt(objs.size());
			
			var citta = (String) objs.get(scelta).get("city");
			var stato = (String) objs.get(scelta).get("state");
			var morti = (String) objs.get(scelta).get("killed");
			var feriti = (String) objs.get(scelta).get("wounded");
			var x = (String) (objs).get(scelta).get("date");
			var y = x.split("T")[0].split("-");
			var data = y[2] + " " + getMese(Integer.parseInt(y[1])) + " "+ y[0];

			final var sparatorie = "Negli Stati Uniti ci sono state **" + jsonArray.size() + "** sparatorie nel " + anno + ".\n";
			final var recente = "La più recente è avvenuta il " + data + " in **" + citta + ", " + stato + "**.\n";
			final var caso = "Per esempio, una si è verificata il " + data + " in **" + citta + ", " + stato + "**.\n";
			final var personeMorte = "Sono morte **" + morti + "** persone.\n";
			final var personaMorta = "È morta **1** persona.\n";
			final var noVittime = "Per fortuna non ci sono state vittime.\n";
			final var personeFerite = "I feriti ammontano a **" + feriti + "**.\n";
			final var totaleMorti = "In totale sono morte **" + mortiAnno + "** persone durante l'anno.\n";
			var finalResp = "";
			
			if (anno == currentYear)
				finalResp = sparatorie + recente;
			else
				finalResp = sparatorie + caso;
			
			finalResp += switch (Integer.parseInt(morti))
			{
				case 0 -> noVittime;
				case 1 -> personaMorta;
				default -> personeMorte;
			};
			
			finalResp += personeFerite + totaleMorti;
			
			channel.sendMessage(finalResp).queue();

		}
		catch (IOException | ParseException e)
		{
			channel.sendMessage("S'è svampato.\n" + e).queue();
			e.printStackTrace();
		}
	} // fine massShooting()
	
	private static String getMese(int mese)
	{
		return switch (mese)
		{
			case 1 -> "gennaio";
			case 2 -> "febbraio";
			case 3 -> "marzo";
			case 4 -> "aprile";
			case 5 -> "maggio";
			case 6 -> "giugno";
			case 7 -> "luglio";
			case 8 -> "agosto";
			case 9 -> "settembre";
			case 10 -> "ottobre";
			case 11 -> "novembre";
			case 12 -> "dicembre";
			
			default -> throw new IllegalStateException("Unexpected value: " + mese);
		};
	} // fine getMese()
	
	/***/
	private void bestemmia()
	{
		if (true) //todo: da finire e poi rimuovere l'if
		{
			final var est = random.nextInt(97)+3; // 3-99
			final String[] tempo = {"mesi", "giorni", "ore", "minuti", "secondi"};
			channel.sendMessage("`Work in progress. ETA al completamento: "+est+" "+tempo[random.nextInt(tempo.length)]+"`").queue();
			return;
		}
		
		final var spazio = " ";
		String particella;
		
		String[] divinita = {"Dio", "Gesù", "Gesù bambino", "Madonna"};
		particella = particella(random.nextInt(divinita.length));
		String[] animale = {"cane", "porco", "anfibio", "rettile", "pesce", "capra"};
		String[] azione = {"che guarda", "che ride", "che corre", "che cammina", "che scappa", "che mangia", "che fa una capriola", "che guida"};
		String[] guardaCosa = {"la nazionale che perde contro una squadra fondata ieri", "una celebrità su instagram, ricca, che sostiene che la ricchezza non serva"};
		String[] toccaCosa = {"che tocca una pianta carnivora, facendosi mordere il dito"};
		String[] rideDiCosa = {"di quella volta che hai chiamato \"mamma\" la maestra alle elementari", "del goal che non hai segnato"+random.nextInt(10)+2 +" anni fa a calcetto"};
		String[] correVersoCosa = {""};
		String[] scappaDaCosa = {"da un leone affamato", "da un marito cornificato che vuole por"+particella+" un paio di domande"};
		String[] mangiaCosa = {"una porzione di sushi avariato", "una pizza con l'ananas sopra", "una puttanesca", ""};
		String[] guidaCosa = {"un fottutissimo pickup lungo 5 metri che consuma tantissimo (25L/100km)", "una macchina piccola con la marmitta modificata che fa un casino tremendo", "un motorino dalla cilindrata minore di zero e impenna pure"};
		
		var bstm = "";
		bstm += divinita[random.nextInt(divinita.length)] + spazio;
		bstm += animale[random.nextInt(animale.length)] + spazio;
		bstm += azione[0] + spazio; //todo: cambiare con random
		bstm += guardaCosa[random.nextInt(guardaCosa.length)] + spazio;
		
		channel.sendMessage(bstm).queue();
		
	} // fine bestemmia()
	
	private String particella(int x)
	{
		return switch (x)
		{
			case 0 -> "lui";
			case 1 -> "lo";
			case 2 -> "gli";
			case 3 -> "le";
			default -> "";
		};
	}
	
	/**Manda un messaggio in chat privata*/
	private void sendMessage(User user, String content)
	{
		user.openPrivateChannel().flatMap(channel -> channel.sendMessage(content)).queue();
	} // fine onPrivateMessageReceived()
	
	
	public void clashCommands(String msgLowerCase)
	{
		if (msgLowerCase.contains("war"))
			clashWar();
		
		if (msgLowerCase.contains("lega") || msgLowerCase.contains("league"))
			clashWarLeague();
		
		
	} // fine clashCommands()
	
	private static String getResponse(URL url) throws IOException
	{
		var connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestProperty("authorization", bearer);
		
		var responseStream = connection.getInputStream();
		
		var in = new BufferedReader(new InputStreamReader(responseStream));
		var inputLine = "";
		
		var response = new StringBuilder();
		
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		
		in.close();
		
		return String.valueOf(response);
	} // fine getResponse()
	
	private void clashWar()
	{
		final var currentWar = "https://api.clashofclans.com/v1/clans/" + tagCompleto + "/currentwar";
		try
		{
			final var currentWarURL = new URL(currentWar);
			
			var response = getResponse(currentWarURL);
			
			var jsonParser = new JSONParser();
			Object obj = jsonParser.parse(response);
			var jsonObject = (JSONObject) obj;
			
			var state = (String) jsonObject.get("state");
			
			if (state.equalsIgnoreCase("notinwar"))
			{
				channel.sendMessage("Non siamo in guerra con nessun clan al momento. smh.").queue(m->react("smh"));
				return;
			}
			
			double[] percentage = new double[2];
			long[] attacks = new long[2];
			long[] stars = new long[2];
			
			var clan = (JSONObject) jsonObject.get("clan");
			percentage[0] = (double) clan.get("destructionPercentage");
			attacks[0] = (long) clan.get("attacks");
			stars[0] = (long) clan.get("stars");
			
			var badge = (JSONObject) clan.get("badgeUrls");
			var badgeM = (String) badge.get("medium");
			
			var opponent = (JSONObject) jsonObject.get("opponent");
			percentage[1] = (double) opponent.get("destructionPercentage");
			attacks[1] = (long) opponent.get("attacks");
			stars[1] = (long) opponent.get("stars");
			
			var attacchiNoi = "TheLegends. Attacchi: **" +attacks[0]+"**. Stelle: **"+stars[0]+"**. Distruzione: **"+percentage[0]+"%**.\n";
			var attacchiLoro = "Avversari. Attacchi: **" +attacks[1]+"**. Stelle: **"+stars[1]+"**. Distruzione: **"+percentage[1]+"%**.\n";
			
			channel.sendMessage("**WAR**\n"+attacchiNoi+attacchiLoro).queue();
			
		}
		catch (IOException | ParseException e){System.out.println("\noh noes\n");}
	} // fine clashWar()
	
	private void clashWarLeague()
	{
		var c = new GregorianCalendar();
		var day = c.get(Calendar.DAY_OF_MONTH);
		var dayOfWar = day-4; // -3 perché lega iniziata 3 giorni fa, -1 perché array parte da 0
		
		final var warLeague = "https://api.clashofclans.com/v1/clans/"+tagCompleto+"/currentwar/leaguegroup";
		try
		{
			final var warLeagueURL = new URL(warLeague);
			var response = getResponse(warLeagueURL);
			
			var jsonParser = new JSONParser();
			Object obj = jsonParser.parse(response);
			var jsonObject = (JSONObject) obj;
			
			if (((String) jsonObject.get("state")).equalsIgnoreCase("notinwar"))
			{
				channel.sendMessage("Non siamo in lega, smh.").queue(l->react("smh"));
				return;
			}
			
			var warTagsArray = (JSONArray) jsonObject.get("rounds");
			
			var warDays = (JSONObject) warTagsArray.get(dayOfWar);
			var warTags = (JSONArray) warDays.get("warTags");
			
			search(warTags, dayOfWar);
			
		}
		catch (IOException | ParseException ignored){}
		
	} // fine clashWarLeague()
	
	private static void search(JSONArray tags, int dayOfWar)
	{
		dayOfWar++;
		final var legaURL = "https://api.clashofclans.com/v1/clanwarleagues/wars/%23";
		
		for (int i = 0; i < 4; i++)
		{
			var x = (String) tags.get(i);
			x = x.substring(1);
			
			try
			{
				var url = new URL(legaURL+x);
				var response = getResponse(url);
				
				Object obj = jsonParser.parse(response);
				var jsonObject = (JSONObject) obj;
				var clan = (JSONObject) jsonObject.get("clan");
				var badgeUrls = (JSONObject) clan.get("badgeUrls");
				var badgeM = (String) badgeUrls.get("medium");
				var name = (String) clan.get("name");
				var opponent = (JSONObject) jsonObject.get("opponent");
				var oppName = (String) opponent.get("name");
				var nameIsUs = true;
				if (name.equalsIgnoreCase("the legends") || oppName.equalsIgnoreCase("the legends"))
				{
					nameIsUs = name.equalsIgnoreCase("the legends");
					
					String[] percentage = new String[2];
					long[] attacks = new long[2];
					long[] stars = new long[2];
					
					percentage[0] = String.format("%.2f", (double) clan.get("destructionPercentage"));
					attacks[0] = (long) clan.get("attacks");
					stars[0] = (long) clan.get("stars");
					
					percentage[1] = String.format("%.2f", (double) opponent.get("destructionPercentage"));
					attacks[1] = (long) opponent.get("attacks");
					stars[1] = (long) opponent.get("stars");
					
					var st = (nameIsUs ? stars[0]:stars[1]) + " vs " + (nameIsUs?stars[1]:stars[0]);
					var attacchi = (nameIsUs ? attacks[0]:attacks[1])+" vs "+ (nameIsUs?attacks[1]:attacks[0]);
					var distr = (nameIsUs?percentage[0]:percentage[1])+ "% vs "+ (nameIsUs?percentage[1]:percentage[0])+"%";
					
					var embed = new EmbedBuilder()
						.setTitle("**War contro " + (nameIsUs ? oppName : name)+"** ("+dayOfWar+"/7)")
						.setColor(Color.RED)
						.setThumbnail(badgeM)
						.addField("Stelle",""+st+"\t", true)
						.addField("Attacchi", ""+attacchi+"\t", true)
						.addField("Distruzione",""+distr+"\t",true)
					;
					
					channel.sendMessageEmbeds(embed.build()).queue();
					
				}
				
				
			}catch (IOException | ParseException ignored){}
		}
	} // fine search()
	
	
	
	
} // fine classe Commands