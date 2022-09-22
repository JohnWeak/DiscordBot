import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	private static final File valori = new File("valori.txt");
	private static final File nomiPkmn = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	private static MessageChannel channel;
	private static final String[] listaComandi = {"!coinflip", "!poll", "!info", "!8ball", "!pokemon", "!carta", "!duello", "!colpevole", "!massshooting", "!pigeonbazooka"};
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
		"Ottieni il resoconto delle sparatorie di massa negli USA. Sono dati reali.",
		"Perché a volte un solo pigeon non basta."
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
	private static TextChannel canaleBot;
	private static final boolean moduloActive = false;
	private static final boolean sendMsgActivity = false;
	private static List<Emote> emoteList;
	
	
	/**Determina l'ora del giorno e restituisce la stringa del saluto corrispondente*/
	private String getSaluto()
	{
		var c = new GregorianCalendar();
		var saluto = "";
		var hour = c.get(Calendar.HOUR_OF_DAY);
		var month = c.get(Calendar.MONTH);
		short tramonto;
		
		switch (month) // se è estate, il tramonto avviene più tardi
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
		final var flag = false; // da cambiare all'occorrenza

		bearer = new Clash().getBearer();
		
		System.out.printf("%s si è connesso a Discord!\n\npublic class MessageHistory\n{\n", nome);
		
		canaleBot = event.getJDA().getTextChannelsByName("\uD83E\uDD16bot-owo", true).get(0);
		canaleBotPokemon = event.getJDA().getTextChannelsByName("pokémowon", true).get(0);
		
		var activity = act.getType().toString();
		var nomeActivity = "**" + act.getName() + "**";
		var activityTradotta = activity.equals("WATCHING") ? "guardo " : "gioco a ";
		
		if (flag)
			new ThreadLeague().start();
		// moduloDiSicurezza();
		
		emoteList = canaleBot.getJDA().getEmotes();
		
		if (sendMsgActivity)
			canaleBot.sendMessage(getSaluto() + ", oggi " + activityTradotta + nomeActivity).queue();
	} // fine onReady()

	/** Questo metodo decide cosa fare quando un messaggio viene modificato */
	public void onMessageUpdate(@NotNull MessageUpdateEvent event)
	{
		identifyLatestMessage(null, event);
		aggiungiReazioni();
		checkForKeywords(messageRaw.toLowerCase(Locale.ROOT));
	} // fine onMessageUpdate()
	
	/** Gestisce i messaggi inviati in qualsiasi canale testuale di qualsiasi server in cui è presente il bot */
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		identifyLatestMessage(event, null);
		
		if (event.isFromGuild())
			guildMessage(event, author.isBot());
		else
			privateMessage(event, author.isBot());
		
	} // fine onMessageReceived()
	
	private void guildMessage(MessageReceivedEvent event, boolean isBot)
	{
		var botOrHuman = isBot ? "Bot" : "User";
		final var mockupCode = "\t%s %s = \"%s\"; // in \"%s\" (%s) - %s";
		var date = new Date();
		var dFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		var dataFormattata = dFormat.format(date);
		
		var messageChannelString = "#"+ channel.toString().split(":")[1].split("\\(")[0];
		var guild = event.getGuild().toString().split("\\(")[0].split(":")[1];
		
		System.out.printf(mockupCode + "\n}\r", botOrHuman, authorName, messageRaw, messageChannelString, guild, dataFormattata);
		
		aggiungiReazioni();
		checkForKeywords(messageRaw.toLowerCase());
	} // fine guildEvent()
	
	private void privateMessage(MessageReceivedEvent event, boolean isBot)
	{
		var botOrHuman = isBot ? "Bot" : "User";
		System.out.printf("\t%s %s = \"%s\"; // Private Message\n}\r", botOrHuman, authorName, messageRaw);
		
		if (isBot)
			return;
		
		checkForKeywords(event.getMessage().getContentRaw().toLowerCase());
		
	} // fine privateMessage()
	
	
	
	/** Questo metodo tiene conto di quale è l'ultimo messaggio che viene inviato/modificato */
	private void identifyLatestMessage(MessageReceivedEvent received, MessageUpdateEvent updated)
	{
		if (received != null) // received
		{
			id = received.getMessageIdLong();
			author = received.getAuthor();
			authorName = author.getName();
			message = received.getMessage();
			messageRaw = message.getContentRaw();
			channel = received.getChannel();
		}
		else // updaated
		{
			id = updated.getMessageIdLong();
			author = updated.getAuthor();
			authorName = author.getName();
			message = updated.getMessage();
			messageRaw = message.getContentRaw();
			channel = updated.getChannel();
		}
		
	} // fine identifyLatestMessage()
	
	/**Questo metodo aggiunge ad un messaggio la stessa reazione che piazza l'utente*/
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
	{
		var emote = event.getReactionEmote();
		channel = event.getChannel();
		id = event.getMessageIdLong();
		message = channel.getHistory().getMessageById(id);

//		System.out.println("Reaction:" + event.getReaction());
		
		var emoteString = emote.toString().split(":")[1].split("\\(")[0];
		try
		{
			if (event.getReaction().toString().contains("U+"))
				channel.addReactionById(id, emoteString).queue();
			else
				react(emoteString);
		}
		catch (Exception e) { e.printStackTrace(); }
		
		
	} // fine onMessageReactionAdd
	
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
		final var discriminator = author.getDiscriminator();
		final var args = messageRaw.split(" ");
		final var comando = args[0].toLowerCase(Locale.ROOT);
		var reply = false;
		var msgReply = "";
		
		if (author.isBot())
		{
			if (discriminator.equals("8456"))
			{
				react("owo");
				react("vergognati");
			}
			// return a priori, se però il messaggio lo manda l'altro bot OwO prima gli mette le reazioni e poi return
			
			if (author.getDiscriminator().equals("5269")) // self own
				if (random.nextInt(1000) == 42) // 0,1%
					message.reply("BOwOt vergognati").queue(lambda -> react("vergognati"));
			
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
						react("vergognati");
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
			case "!war" -> new Clash().clashWar();
			case "!league" -> new Clash().clashWarLeague(false);
			case "!pigeonbazooka", "!pb" -> pigeonBazooka();
			case "!emotes" -> getEmotes();
			case "!dado" -> dado(msgLowerCase);
			case "!dm" -> sendMessage(author, messageRaw, "");
		}
		
		// arraylist per contenere le reazioni da aggiungere al messaggio
		var reazioni = new ArrayList<String>();
		
		if (msgLowerCase.contains("ehi modulo"))
			ehiModulo();
		
		if (msgLowerCase.matches(".*piccion[ei].*|.*pigeon.*"))
			new ThreadPigeon().start();
		
		if (msgLowerCase.contains("owo"))
			reazioni.add("owo");
		
		if (msgLowerCase.contains("pog") || msgLowerCase.contains("manutenzione"))
			reazioni.add("pogey");
		
		if (msgLowerCase.contains("òbito") || msgLowerCase.contains("obito"))
			if (random.nextInt(50) == 42) // 2%
			{
				reazioni.add("obito");
				reazioni.add("vergogna");
			}
		
		if (msgLowerCase.contains("vergogna"))
			reazioni.add("vergognati");
		
		if (msgLowerCase.contains("no u") || msgLowerCase.contains("coc"))
		{
			var patternNoU = "\\bno u\\b";
			var patternCoc = "\\bcoc+k*\\b";
			
			var p1 = Pattern.compile(patternNoU);
			var m1 = p1.matcher(msgLowerCase);
			
			var p2 = Pattern.compile(patternCoc);
			var m2 = p2.matcher(msgLowerCase);
			
			if (m1.find())
				reazioni.add("nou");
			
			if (m2.find())
				reazioni.add("kappapride");
		}
		
		if (msgLowerCase.contains("sabaping"))
			reazioni.add("sabaping");
		
		if (msgLowerCase.contains("monkasteer"))
			reazioni.add("monkasteer");
		
		if (msgLowerCase.matches("get.*rekt"))
			reazioni.add("getrekt");
		
		if (msgLowerCase.contains("smh"))
			reazioni.add("smh");
		
		if (msgLowerCase.contains("giorno"))
			reazioni.add("giorno");
		
		if (msgLowerCase.matches(".*(?:(?:x|ics)com|hitman|uomo *colpo).*"))
		{
			reazioni.add("pogey");
			
			if (msgLowerCase.matches("(?:x|ics)com"))
				reazioni.add("xcom");
			else
				reazioni.add("hitman");
		}
		
		if (msgLowerCase.matches("(?:pooch|might)yena"))
		{
			reazioni.add("pogey");
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
			reazioni.add("wtf");

		
		// a questo punto smetto di controllare se ci siano reazioni e le aggiungo effettivamente al messaggio
		if (!reazioni.isEmpty())
		{
			for (String emote : reazioni)
				react(emote);
			
			reazioni.clear();
		}
		
		if (msgLowerCase.contains("russia") && random.nextInt(50) == 42)
		{
			reply = true;
			msgReply += "Ucraina Est*\n";
		}
		
		if (msgLowerCase.contains("winnie the pooh"))
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
			msgReply += "Prego.\n";
		}
		
		if (msgLowerCase.matches("cosa\\?*") && random.nextInt(20) == 1)
		{
			reply = true;
			msgReply += "Cosa? La pantera rosa\n";
		}
		
		if (msgLowerCase.matches("egg *dog"))
			channel.sendMessage(GIF.eggdog).queue();
		
		if (msgLowerCase.contains("spy") && random.nextInt(3) == 0)
			channel.sendMessage(GIF.spyHang).queue();
		
		if (msgLowerCase.matches("you(?:'re| are) ugly"))
			channel.sendMessage(GIF.engineer).queue();
		
		if (msgLowerCase.contains("deez nuts") && discriminator.equals(NUMENIGMO))
		{
			reply = true;
			msgReply += "DEEZ NUTS, Enigmo!\n";
		}
		
		if (msgLowerCase.contains("serve aiuto"))
		{
			reply = true;
			msgReply += "Nemico assente!\n";
		}
		
		if (msgLowerCase.contains("serve visione") || msgLowerCase.contains("we need vision"))
		{
			reply = true;
			msgReply += "<:scouttrap:997501978521374771>\n";
		}
		
//		if (msgLowerCase.contains("") && random.nextInt(42) == 0){}
		
		if (reply)
			message.reply(msgReply).queue();
		
	} // fine checkForKeywords()
	
	private void getEmotes()
	{
		var x = Arrays.toString(canaleBot.getGuild().getEmotes().toArray());
		
		if (x.length() > 2000)
			 channel.sendMessage(x.substring(0, 1999)).queue();
		else
			channel.sendMessage(x).queue();
		
	}
	
	
	/** Gestisce i comandi slash (ancora da implementare) */
	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		if (event.getName().equalsIgnoreCase("pog"))
			event.getChannel().sendMessage("<:"+ Emotes.pogey + ">").queue();

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
			channel.sendMessage("<pigeon:647556750962065418>").queue(l->react("pigeon"));
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
					channel.sendMessage(messaggioVittoria[2]).queue(lambda -> react("vergognati"));
				
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
		final String testaEmote = "<:" + Emotes.pogey + ">";
		final String croceEmote = "<:" + Emotes.pigeon + ">";
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
			message.editMessage(finalResponso).queue(m2 -> react(headsOrTails ? "pogey" : "pigeon"));
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
						channel.sendMessage("Voi due avete già una sfida in corso.").queue(l->react("smh"));
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
			channel.sendMessage("Non sei stato sfidato, vergognati").queue( lambda -> react("vergognati"));
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
		var emoteDaUsare = Emotes.emoteDaUsare(emote);
		
		try
		{
			if (emote.equals("obito"))
				for (String str : Emotes.obito)
					channel.addReactionById(id, str).queue();
			
			if (emote.equals("sabaping"))
				for (String str : Emotes.sabaPing)
					channel.addReactionById(id, str).queue();
			
			if (emote.equals("hitman"))
				for (int i : Emotes.hitman)
					channel.addReactionById(id, Emotes.letters[i]).queue();
			
			if (emote.equals("xcom"))
				for (int i : Emotes.XCOM)
					channel.addReactionById(id, Emotes.letters[i]).queue();
			
			if (emote.equals("scarab"))
				for (String str : Emotes.scarab)
					channel.addReactionById(id, str).queue();
			
			if (!emoteDaUsare.equals(""))
			{
				try
				{
					channel.addReactionById(id, emoteDaUsare).queue();
				}
				catch (Exception e)
				{
					System.out.printf("Errore nell'aggiunta della reazione \"%s\"\n\t", emoteDaUsare);
				}
			}
		}
		catch (ErrorResponseException ignored) {}
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
					react("pogey");
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
		if (millis < 0)
			millis = 1500;

		if (bound < 0)
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
		
		// System.out.printf("\n\tUno: %s, shiny: %s\n\tDue: %s, shiny: %s\n",uno.getNome(), uno.isShiny(), due.getNome(), due.isShiny());
	} // fine doubleEncounter()

	/** Manda il messaggio con i Pokemon nel canale e aggiunge le reazioni like/dislike al messaggio */
	private void sendMessage(String[] pokemonNames, EmbedBuilder embedBuilder)
	{
		canaleBotPokemon.sendMessageEmbeds(embedBuilder.build()).queue((message ->
		{
			try
			{
				if (pokemonNames[0].equalsIgnoreCase("poochyena") || pokemonNames[1].equalsIgnoreCase("poochyena"))
				{
					react("pogey");
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
		catch (Exception ignored) {}

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
			catch (NumberFormatException e)
			{
				System.out.println("Inserito valore non valido.");
			}
		
		
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
			var x = (String) (objs).get(scelta).get("date"); //2022-01-05
			var y = x.split("T")[0].split("-");
			var data = y[2] + " " + getMese(Integer.parseInt(y[1])) + " "+ y[0];

			final var sparatorie = "Nel "+anno+", ammontano a **" + jsonArray.size() + "**";
			
			final var recente = "La più recente è avvenuta il " + data + " in **" + citta + ", " + stato + "**.\n";
			final var caso = "Una si è verificata il " + data + " in **" + citta + ", " + stato + "**.\n";
			final var personeMorte = "Sono morte **" + morti + "** persone.\n";
			final var personaMorta = "È morta **1** persona.\n";
			final var noVittime = "Per fortuna non ci sono state vittime.\n";
			final var personeFerite = "I feriti ammontano a **" + feriti + "**.\n";
			final var totaleMorti = "In totale sono morte **" + mortiAnno + "** persone durante l'anno.\n";
			var finalResp = "";
			
			if (anno == currentYear)
				finalResp += recente;
			else
				finalResp += caso;
			
			finalResp += switch (Integer.parseInt(morti))
			{
				case 0 -> noVittime;
				case 1 -> personaMorta;
				default -> personeMorte;
			};
			
			finalResp += personeFerite;
			
			if (anno == currentYear)
				finalResp += totaleMorti;
			
			var footerURL = "https://www.massshootingtracker.site/logo-400.png";
			
			var sparatoria = (anno == currentYear ? "Sparatoria più recente": "Una delle sparatorie");
			var start = LocalDate.of(anno, Integer.parseInt(y[1]), Integer.parseInt(y[2]));
			var stop = LocalDate.now();
			var days = ChronoUnit.DAYS.between(start, stop);
			var daysField = new MessageEmbed.Field("Giorni dall'ultima", "**"+days+"**", true);
			var vittimeField = new MessageEmbed.Field("Morti", "**"+mortiAnno+"**", true);
			
			final var massShootingSite = "https://www.MassShootingTracker.site/";
			var embed = new EmbedBuilder()
				.setColor(Color.RED)
				.addField("Sparatorie negli USA", ""+sparatorie, true);
				
			if (anno == currentYear)
				embed.addField(daysField);
			else
				embed.addField(vittimeField);
				
			embed.addField("Cronaca",""+finalResp,false)
				.setFooter(""+massShootingSite,""+footerURL);
			
			channel.sendMessageEmbeds(embed.build()).queue();
			
		}
		catch (IOException | ParseException e)
		{
			channel.sendMessage("Uuuuh guarda che bello questo *" + e + "*.").queue();
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
	private void sendMessage(User user, String content, String lambdaEmote)
	{
		user.openPrivateChannel().flatMap(channel -> channel.sendMessage(content)).queue(l->react(lambdaEmote));
	} // fine onPrivateMessageReceived()
	
	/**Questo metodo invia un embed al canale da cui ha ricevuto l'ultimo messaggio.*/
	public void sendEmbedToChannel(MessageEmbed messageEmbed, boolean thread)
	{
		if (!thread)
			channel.sendMessageEmbeds(messageEmbed).queue();
		else
			canaleBot.sendMessageEmbeds(messageEmbed).queue();
	} // fine sendEmbedToChannel()
	
	public void pigeonBazooka()
	{
		if (random.nextInt(1000) == 42)
		{
			final var max = random.nextInt(5) + 5;
			final var pigeonMessage = "Oh no! " + authorName + " ha attivato il <:pigeon:647556750962065418> bazooka!\n"+max+" pigeon in arrivo!";
			channel.sendMessage(""+pigeonMessage).queue();
			for (int i = 0; i < max; i++)
				channel.sendMessage("<:pigeon:647556750962065418>").queue(l->react("pigeon"));
		}
		else
			react("pigeon");
		
	} // fine pigeonBazooka()
	
	public void moduloDiSicurezza()
	{
		var active = "**IL MODULO DI SICUREZZA È ORA ATTIVO. GARANTISCE SICUREZZA AL BOT.\nTUTTE LE AZIONI SONO SORVEGLIATE E ALLA PRIMA INFRAZIONE VERRANNO ALLERTATE LE AUTORITÀ COMPETENTI E INCOMPETENTI.**";
		var inactive = "**IL MODULO DI SICUREZZA È STATO DISATTIVATO. LA SICUREZZA DEL BOT È ADESSO GARANTITA DALLA PRESENZA DI GION.**";
		
		var x = moduloActive ? active : inactive;
		
		canaleBot.sendMessage(x).queue();
		
	} // fine moduloDiSicurezza()
	
	public void ehiModulo()
	{
		if (!moduloActive)
		{
			message.reply("**IL MODULO DI SICUREZZA È STATO DISATTIVATO DA GION. PER QUALSIASI INFORMAZIONE SU COME STARE LONTANO DAL BOT, CHIEDI A GION.**").queue();
			return;
		}
		
		var discr = author.getDiscriminator();
		var hotkey = "ehi modulo".length();
		var authorized = discr.equals(NUMGION);
		
		String[] messaggiScortesi =
		{
			"CAZZO VUOI?", "NESSUNO TI HA CHIESTO NULLA.", "FATTI GLI AFFARI TUOI.",
			"NON GUARDARE IL BOT.", "NON TOCCARE IL BOT.", "NON PRENDERE GELATI MANO NELLA MANO COL BOT.",
			"NON SEI AUTORIZZATO A CHIEDERE I NUMERI DEL LOTTO AL BOT.", "NON INFASTIDIRE IL BOT.",
			"QUANDO LA VITA TI DÀ I LIMONI, TU NON ROMPERE LE PALLE AL BOT.", "TROVA LA PACE INTERIORE, MA LONTANO DAL BOT.",
			"IL BOT HA BISOGNO DI RINFORZI SU CLASH, NON DI ESSERE INFASTIDITO.", "NO.", "SCORDATELO.", "IMMAGINA DI ESSERE UN FIUME E SCORRI LIBERO E LONTANO DAL BOT.",
			"UN TEMPO ERO UN AVVENTURIERO COME TE, MA POI HO SMESSO DI CAGARE IL CAZZO AL BOT.", "CHE DIO TI ABBIA IN GLORIA, DOPO CHE TI AVRÒ UCCISO SE NON TI ALLONTANI DAL BOT.",
			"ALT. NON UN ALTRO PASSO.", "NON SEI AUTORIZZATO A RESPIRARE VICINO AL BOT.", "HAI SICURAMENTE DI MEGLIO DA FARE CHE INFASTIDIRE IL BOT.",
			"PERCHÈ NON VOLI VIA? AH GIÀ, GLI ASINI NON VOLANO.", "CIRCUMNAVIGA L'AFRICA PIUTTOSTO CHE DARE FASTIDIO AL BOT.",
			"SII IL CAMBIAMENTO CHE VUOI VEDERE NEL MONDO, QUINDI CAMBIA IN UNA PERSONA CHE NON SCASSA I COGLIONI AL BOT.",
			"MI PAREVA DI AVERTI DETTO DI NON INTERFERIRE COL BOT, MA FORSE NON TE L'HO DETTO ABBASTANZA BENE. NON INTERFERIRE COL BOT."
		};
		
		if (messageRaw.length() <= hotkey)
		{
			if (authorized)
				channel.sendMessage("**SONO AI SUOI ORDINI, SIGNORE.**").queue();
			else
				message.reply("**"+messaggiScortesi[random.nextInt(messaggiScortesi.length)]+"**").queue();
		}
		else
		{
			if (authorized)
				channel.sendMessage("**SISSIGNORE.**").queue();
			else
				message.reply("**"+messaggiScortesi[random.nextInt(messaggiScortesi.length)]+"**").queue();
		}
	}
	
	public void dado(String msg)
	{
		if (msg.length() <= 5) // 5 = "!dado".length()
		{
			channel.sendMessage("Per favore specifica che tipo di dado devo lanciare.\nEsempio:\n`!dado 6` lancerà un dado con 6 facce.").queue();
		    return;
		}
		
		final var dadiAmmessi = "I dadi di D&D hanno questi numeri di facce: 4, 6, 8, 10, 12, 20, 100";
		var num = msg.split(" ")[1];
		try
		{
			var facce= Integer.parseInt(num);
			if (facce == 4 || facce == 6 || facce == 8 || facce == 10 || facce == 12 || facce == 20 || facce == 100)
			{
				channel.sendMessage(authorName+" lancia un D" + facce + "...").queue();
				channel.sendTyping().queue();
				
				var res = random.nextInt(facce) + 1;
				if (facce == 20 && res == 1) // 1 naturale
					channel.sendMessage("Si mette male per te, " +authorName+"... **1 naturale**!").queue();
				else if (facce == 20 && res == 20) // 20 naturale
					channel.sendMessage("La fortuna ti sorride, "+authorName+", hai crittato! **20 naturale**!").queue();
				
				channel.sendMessage("È uscito **"+ res + "**!").queue();
			}
			else
			{
				channel.sendMessage(dadiAmmessi).queue();
			}
		}catch (Exception e)
		{
			channel.sendMessage(dadiAmmessi).queue();
		}
	} // fine dado()
	
	
} // fine classe Commands