import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.apache.commons.collections4.iterators.UnmodifiableIterator;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	public static final File nomiPkmn = new File("src/nomiPokemon.txt");
	public static final String botChannel = "\uD83E\uDD16bot-owo";
	private static final Random random = new Random();
	public static MessageChannel channel;
	public static String authorName;
	private static long id;
	private static HashMap<String,String> commandsHashMap;
	private static final Locale locale = Locale.ITALIAN;
	public static Message message;
	public static String messageRaw;
	public static User author;
	private static final ArrayList<Challenge> listaSfide = new ArrayList<>();
	private static final String[] challenge = {"Duello Carte", "Sasso Carta Forbici"};
	private static boolean duelloAttivo = false;
	private static boolean sfidaAttiva = false;
	private static User sfidante = null;
	private static User sfidato = null;
	private static final String[] simboli = {"♥️", "♦️", "♣️", "♠️"};
	private static String sceltaBot;
	public static TextChannel canaleBotPokemon;
	private static final int currentYear = new GregorianCalendar().get(Calendar.YEAR);
	public static TextChannel canaleBot;
	private static final boolean moduloActive = true;
	private static final boolean sendMsgActivity = false;
	private static List<Emote> emoteList;
	private static ThreadActivity threadActivity;
	
	
	/**Determina l'ora del giorno e restituisce la stringa del saluto corrispondente*/
	private String getSaluto()
	{
		var roma = TimeZone.getTimeZone("Europe/Rome");
		var c = new GregorianCalendar(roma, Locale.ITALY);
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
		commandsHashMap = Cmd.init();
		
		var jda = event.getJDA();
		var nome = jda.getSelfUser().getName();
		var act = jda.getPresence().getActivity();
		String activityType, nomeActivity, activityTradotta;
		
		System.out.printf("%s si è connesso a Discord!\n\npublic class MessageHistory\n{\n", nome);
		
		canaleBot = jda.getTextChannelsByName(botChannel, true).get(0);
		canaleBotPokemon = jda.getTextChannelsByName("pokémowon", true).get(0);
		
		if (act != null)
		{
			activityType = act.getType().toString();
		    nomeActivity = "**" + act.getName() + "**";
		    activityTradotta = activityType.equals("WATCHING") ? "guardo " : "gioco a ";
		}
		
		moduloDiSicurezza();
		
		emoteList = canaleBot.getJDA().getEmotes();
		
		threadActivity = new ThreadActivity(true);
		threadActivity.start();
		
		if (sendMsgActivity)
			canaleBot.sendMessage(getSaluto() + ", oggi " + activityTradotta + nomeActivity).queue();
	} // fine onReady()

	/** Questo metodo decide cosa fare quando un messaggio viene modificato */
	public void onMessageUpdate(@NotNull MessageUpdateEvent event)
	{ //
		identifyLatestMessage(null, event);
		aggiungiReazioni();
		checkForKeywords(message.getContentStripped().toLowerCase());
	} // fine onMessageUpdate()
	
	/** Gestisce i messaggi inviati in qualsiasi canale testuale di qualsiasi server in cui è presente il bot */
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		identifyLatestMessage(event, null);
		
		if (event.isFromGuild())
			guildMessage(event, author.isBot());
		else
		{
			if (moduloActive)
			{
				if (author.getDiscriminator().equals(Utente.ENIGMO))
				{
					PrivateMessage pm = new PrivateMessage(Utente.getUtenteFromID(author.getId()));
					pm.send("<:"+Emotes.ragey+">");
				}
			}
			else
			{
				privateMessage(author.isBot());
			}
		}
		
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
		checkForKeywords(message.getContentStripped().toLowerCase());
	} // fine guildEvent()
	
	/**Gestisce i messaggi privati che il bot riceve. Se è un altro bot a inviarli, li ignora.
	 * @param isBot <code>true</code> se l'autore del messaggio è un bot a sua volta, <code>false</code> altrimenti. */
	public void privateMessage(boolean isBot)
	{
		var botOrHuman = isBot ? "Bot" : "User";
		System.out.printf("\t%s %s = \"%s\"; // Private Message\n}\r", botOrHuman, authorName, messageRaw);
		
		if (isBot)
			return;
		
		if (!authorName.equalsIgnoreCase("john weak"))
		{
			var pm = new PrivateMessage(Utente.getGion());
			pm.send(authorName + " ha scritto: \"" + messageRaw + "\"");
		}
		
		checkForKeywords(message.getContentStripped().toLowerCase());
		
	} // fine privateMessage()
	
	
	
	/** Questo metodo tiene conto di quale è l'ultimo messaggio che viene inviato/modificato.
	 * @param received l'ultimo messaggio ricevuto. Sarà <code>null</code> se si tratta di un messaggio modificato.<br>
	 * @param updated l'ultimo messaggio modificato. Sarà <code>null</code> se si tratta di un nuovo messaggio ricevuto.*/
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
		else // updated
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

		// System.out.println("Reaction: " + event.getReaction());
		
		var emoteString = emote.toString().split(":")[1].split("\\(")[0];
		try
		{
			if (event.getReaction().toString().contains("U+"))
				channel.addReactionById(id, emoteString).queue();
			else
				react(emoteString);
		}
		catch (Exception e)
		{
			canaleBot.sendMessage("È esploso qualcosa in `onMessageReactionAdd`: " + e).queue();
		}
		
		
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
	
	
	/** Controlla che il messaggio abbia le parole chiave per attivare i comandi (o le reazioni) del bot
	 * @param msgStrippedLowerCase la stringa del messaggio inviato convertita in minuscolo.
	 * */
	public void checkForKeywords(String msgStrippedLowerCase)
	{
		final var discriminator = author.getDiscriminator();
		final var args = messageRaw.split(" ");
		final var comando = args[0].toLowerCase(locale);
		var reply = false;
		var msgReply = "";
		
		// se è un bot a mandare il messaggio, ignoralo per evitare loop di messaggi
		if (author.isBot())
		{
			// se però è il bot owo a mandare il messaggio, prima fai un paio di robe e poi return
			if (discriminator.equals(Utente.OWOBOT))
			{
				react("owo");
				react("vergognati");
				
				if (msgStrippedLowerCase.contains("daily streak"))
				{
					var msgSplittato = msgStrippedLowerCase.split(" ");
					var size = msgSplittato.length;
					var auth = "";
					var numGiorni = 0;
					var pvtMsg = new PrivateMessage(Utente.getGion());
					var channelHistory = channel.getHistory().retrievePast(3).complete();
					
					try
					{
						auth = channelHistory.get(1).getAuthor().getName();
						
						for (short i = 0; i < size; i++)
						{
							if (msgSplittato[i].contains("daily") && msgSplittato[i+1].startsWith("streak"))
							{
								numGiorni = Integer.parseInt(msgSplittato[i-1]);
								break;
							}
						}
						pvtMsg.send("Daily di " + auth +": "+numGiorni);
					} // fine try
					catch (Exception e)
					{
						var msgErrore = "<@"+Utente.ID_GION+">\n" + e;
						canaleBot.sendMessage(msgErrore).queue();
					} // fine catch
					
					if (numGiorni == 0 || !(numGiorni % 365 == 0))
					{
						return;
					}
					else
					{
						if (auth.equals(""))
						{
							canaleBot.sendMessage("<@"+Utente.ID_GION+">\n`auth è una stringa vuota`.").queue();
							return;
						}
						
						var years = (numGiorni / 365);
						var mess = "Complimenti, " + auth + "! Sono " + years + " anni di OwO daily!";
						channel.sendMessage(mess).queue();
					}
				} // fine if daily streak
			} // fine if equals 8456
			
			
			if (author.getDiscriminator().equals(Utente.BOWOT)) // self own
				if (random.nextInt(1000) == 42) // 0,1%
					message.reply("BOwOt vergognati").queue(lambda -> react("vergognati"));
			
			return;
		} // fine if isBot
		
		if (!msgStrippedLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
			Pokemon.spawnPokemon();
		
		if (random.nextInt(500) == 42) // chance di reagire con emote personali
		{
			var trigger = random.nextBoolean();
			
			if (trigger)
			{
				triggera(discriminator);
			}
			else
			{
				switch (discriminator)
				{
					case Utente.OBITO ->
					{
						react("obito");
						react("vergognati");
						message.reply("Òbito vergognati").queue();
					}
					case Utente.ENIGMO -> react("pigeon");
					case Utente.LEX -> channel.addReactionById(id, "🇷🇴").queue();
					case Utente.GION -> react("smh");
					
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
			case "!pokemon" -> Pokemon.pokemon();
			case "!colpevolezza", "!colpevole" -> colpevolezza();
			case "!carta" -> sendCarta(new Card());
			case "!duello", "!duellocarte" -> duelloDiCarte();
			case "!accetto" -> accettaDuello(false);
			case "!rifiuto" -> rifiutaDuello();
			case "!massshooting", "!ms" -> massShooting();
			case "!war" -> new Clash().clashWar();
			case "!league" -> new Clash().clashWarLeague(false);
			case "!emotes" -> getEmotes();
			case "!dado" -> dado(msgStrippedLowerCase);
			case "!cattura", "!catch" -> Pokemon.catturaPokemon();
			case "!f", "+f" -> payRespect();
			case "!timer" -> timer();
			case "!dm" -> dm(msgStrippedLowerCase);
			case "!ch" -> channelHistory();
			case "!toggleactivity", "!ta" -> toggleActivity(msgStrippedLowerCase, threadActivity);
		}
		
		// arraylist per contenere le reazioni da aggiungere al messaggio
		var reazioni = new ArrayList<String>();
		
		if (msgStrippedLowerCase.contains("random number") || msgStrippedLowerCase.contains("numero casuale"))
			new PrivateMessage(author)
				.send("Numero casuale: **"+(random.nextInt(42)+1)+"**");
		
		
		if (msgStrippedLowerCase.contains("ehi modulo"))
			ehiModulo();
		
		if (msgStrippedLowerCase.matches(".*piccion[ei].*|.*pigeon.*"))
			new ThreadPigeon(authorName, channel).start();
		
		if (msgStrippedLowerCase.contains("owo"))
			reazioni.add(Emotes.OwO);
		
		//if (msgStrippedLowerCase.contains("splitta questo"))
		//	splitMsgAndReply();
		
		if (contains(msgStrippedLowerCase, new String[]{"pog", "manutenzione"}))
			reazioni.add(Emotes.pogey);
		
		if (contains(msgStrippedLowerCase, new String[]{"òbito", "obito", "óbito"}))
			if (random.nextInt(50) == 42) // 2%
			{
				reazioni.add("obito");
				reazioni.add("vergogna");
			}
		
		if (msgStrippedLowerCase.contains("vergogna"))
			reazioni.add("vergognati");
		
		if (contains(msgStrippedLowerCase, new String[]{"no u"}))
			reazioni.add(Emotes.NoU);
		
		if (contains(msgStrippedLowerCase, new String[]{"coc", "cock", "cocktail", "clash of clans", "cocco"}))
			reazioni.add(Emotes.kappaPride);
		
		if (msgStrippedLowerCase.contains("sabaping"))
			reazioni.add("sabaping");
		
		if (msgStrippedLowerCase.matches(".*get *rekt"))
			reazioni.add(Emotes.getRekt);
		
		if (msgStrippedLowerCase.contains("smh"))
			reazioni.add("smh");
		
		if (msgStrippedLowerCase.contains("giorno"))
			reazioni.add("giorno");
		
		if (msgStrippedLowerCase.matches(".*(?:hitman|uomo *colpo)"))
		{
			reazioni.add(Emotes.pogey);
			reazioni.add("hitman");
		}
		
		if (msgStrippedLowerCase.matches(".*(?:x|ics)com"))
		{
			reazioni.add(Emotes.pogey);
			reazioni.add("xcom");
		}
		
		if (msgStrippedLowerCase.matches("(?:pooch|might)yena"))
		{
			reazioni.add(Emotes.pogey);
			reazioni.add("❤️");
		}
		
		if (contains(msgStrippedLowerCase, new String[]{"ape", "api", "apecar", "apicoltore"}))
			reazioni.add("🐝");
		
		if (msgStrippedLowerCase.contains("cl__z"))
		{
			reply = true;
			msgReply += "Sempre sia lodato\n";
		}
		
		if (msgStrippedLowerCase.contains("scarab"))
			reazioni.add("scarab");
		
		if (contains(msgStrippedLowerCase, new String[]{"ingredibile", "andonio gonde"}))
			reazioni.add(Emotes.ingredibile);
		
		if (contains(msgStrippedLowerCase, new String[]{"wtf", "what the fuck"}))
			reazioni.add(Emotes.wtf);
		
		if (msgStrippedLowerCase.matches(".*(?:guid|sto.*guidando|monkasteer)"))
			reazioni.add(Emotes.monkaSTEER);
		
		if (msgStrippedLowerCase.contains("boris"))
			reazioni.add(Emotes.borisK);

		if (msgStrippedLowerCase.contains("òbito") && msgStrippedLowerCase.contains("india"))
		{
			reazioni.add("òbito");
			reazioni.add("🇮🇳");
		}
		
		if (msgStrippedLowerCase.contains("live") && author.getDiscriminator().equals(Utente.OBITO))
			reazioni.add(Emotes.harry_fotter);
		
		
		// a questo punto smetto di controllare se ci siano reazioni e le aggiungo effettivamente al messaggio
		if (!reazioni.isEmpty())
		{
			for (String emote : reazioni)
				react(emote);
			
			reazioni.clear();
		}
		
		if (msgStrippedLowerCase.contains("russia") && random.nextInt(50) == 42)
		{
			reply = true;
			msgReply += "Ucraina Est*\n";
		}
		
		if (msgStrippedLowerCase.contains("winnie the pooh"))
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
		
		if (msgStrippedLowerCase.equalsIgnoreCase("cancella questo messaggio"))
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
		
		if ((msgStrippedLowerCase.contains("ehil")) && author.getDiscriminator().equals("4781"))
		{
			reply = true;
			msgReply += "Salve!";
		}
			
		if (msgStrippedLowerCase.contains("non vedo l'ora") || msgStrippedLowerCase.contains("che ore sono") || msgStrippedLowerCase.contains("che ora è"))
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
			if (flag && msgStrippedLowerCase.contains(s))
			{
				flag = false;
				final int bound = 1000;
				if (random.nextInt(bound) < bound - 1)
					message.reply(getSaluto() + " anche a te").queue();
				else
					message.reply("No, vaffanculo >:(").queue();
			}
		}
		
		if (msgStrippedLowerCase.contains("dammi il 5") || msgStrippedLowerCase.contains("high five") || msgStrippedLowerCase.contains("dammi il cinque"))
		{
			reply = true;
			msgReply += "🤚🏻\n";
		}
		
		if (msgStrippedLowerCase.contains("grazie") && random.nextInt(50) == 42)
		{
			reply = true;
			msgReply += "Prego.\n";
		}
		
		if (msgStrippedLowerCase.matches("cosa\\?*") && random.nextInt(20) == 1)
		{
			reply = true;
			msgReply += "Cosa? La pantera rosa\n";
		}
		
		if (msgStrippedLowerCase.matches("egg *dog"))
			message.reply(GIF.eggdog).queue();
		
		if (msgStrippedLowerCase.contains("spy") && random.nextInt(3) == 0)
			message.reply(GIF.spyHang).queue();
		
		if (msgStrippedLowerCase.matches("you(?:'re| are) ugly"))
			message.reply(GIF.engineer).queue();
		
		if (msgStrippedLowerCase.contains("deez nuts") && discriminator.equals(Utente.ENIGMO))
		{
			reply = true;
			msgReply += "DEEZ NUTS, Enigmo!\n";
		}
		
		if (msgStrippedLowerCase.contains("serve aiuto"))
		{
			reply = true;
			msgReply += "Nemico assente!\n";
		}
		
		if (contains(msgStrippedLowerCase, new String[]{"serve visione","we need vision"}))
		{
			reply = true;
			msgReply += "<:"+Emotes.scoutTrap+">\n";
		}
		
		
		/*if (msgStrippedLowerCase.contains("activity"))
		{
			var a = Main.selectActivity();
			Commands.message.getJDA().getPresence().setActivity(a);
			PrivateMessage pm = new PrivateMessage(Utente.getUtenteFromID(author.getId()));
			pm.send(a.getName());
		}*/
		
//		if (msgStrippedLowerCase.contains("") && random.nextInt(42) == 0){}
		
		if (reply)
			message.reply(msgReply).queue();
		else if (random.nextInt(1000000) == 42)
			message.reply("***ULTIMO AVVISO: __NON FARLO MAI PIÙ__.***").queue();
		
	} // fine checkForKeywords()
	
	/**Ottiene le emote custom del canale*/
	private void getEmotes()
	{
		var x = Arrays.toString(canaleBot.getGuild().getEmotes().toArray());
		
		// discord limita i messaggi a 2000 caratteri per messaggio
		if (x.length() > 2000)
			 channel.sendMessage(x.substring(0, 1999)).queue();
		else
			channel.sendMessage(x).queue();
		
	} // fine getEmotes()
	
	private void channelHistory()
	{
		final var amount = 3;
		var history = channel.getHistory().retrievePast(amount).complete();
		var pm = new PrivateMessage(Utente.getGion());
		for (int i = 0; i < 3; i++)
		{
			var auth = history.get(i).getAuthor();
			var name = auth.getName();
			var disc = auth.getDiscriminator();
			var m = history.get(i).getContentStripped();
			
			pm.send("Messaggio numero "+i+":\t"+auth+" --- "+name+" ("+disc+"): " + m);
		}
		
	} // fine metodo channelHistory()
	
	/**Questo metodo fa sì che il bot invii un messaggio privato all'utente che lo esegue
	 * @param content il messaggio da inviare all'utente. */
	private void dm(String content)
	{
		final String[] msg = content.split(" ");
		final String[] nomi = {Utente.NOME_JOHN, Utente.NOME_JOHN2, Utente.NOME_OBITO, Utente.NOME_OBITO2, Utente.NOME_ENIGMO, Utente.NOME_LEX};
		PrivateMessage privateMessage;
		final int length = msg.length;
		StringBuilder msgToSend = new StringBuilder("Prova test 123");
		
		if (length > 2)
		{
			msgToSend = new StringBuilder();
			for (int i = 2; i < length; i++)
			{
				msgToSend.append(msg[i].concat(" "));
			}
		}
		if (length > 1)
		{
			boolean matchFound = false;
			
			for (String n : nomi)
			{
				if (n.equalsIgnoreCase(msg[1]))
				{
					privateMessage = new PrivateMessage(Utente.getUtenteFromName(n));
					privateMessage.send(msgToSend.toString());
					matchFound = true;
				}
			}
			
			if (!matchFound)
			{
				Commands.message.reply("Utente non trovato!").queue();
			}
		}
		else
		{
			var msgUso = "Usa `!dm <utente> [messaggio]` per fare sì che io importuni in privato l'utente da te specificato.";
			Commands.channel.sendMessage(msgUso).queue();
		}
		
	} // fine metodo dm()
	
	public static void toggleActivity(String messaggio, ThreadActivity thrActivity)
	{
		String option, msg;
		boolean active;
		
		if (!thrActivity.isAlive())
		{
			message.reply("Il thread non è più attivo.").queue();
			return;
		}
		
		try
		{
			if (messaggio.split(" ").length == 1)
			{
				// nessun messaggio dopo il comando, quindi funzione toggle
				active = !(thrActivity.isKeepGoing());
				thrActivity.setKeepGoing(active); // false -> true / true -> false
				msg = "Ok, il cambio casuale delle activity è stato " + (active ? "attivato" : "disattivato") + ".";
				
				message.reply(msg).queue();
				return;
			}
			else
			{
				option = messaggio.split(" ")[1];
			}
			
			switch (option.toLowerCase(Locale.ITALIAN))
			{
				case "true", "t" ->
				{
					if (thrActivity.isKeepGoing())
					{
						message.reply("Il cambio delle activity era già attivo.").queue();
						return;
					}
					thrActivity.setKeepGoing(false); // disattiva il vecchio thread
					message.reply("Ok, ho attivato il cambio delle activity.").queue();
					
					
					threadActivity = new ThreadActivity(true);
					threadActivity.start();
				}
				case "false", "f" ->
				{
					if (thrActivity.isAlive() && !thrActivity.isKeepGoing())
					{
						message.reply("Il cambio delle activity era già disattivato.").queue();
						return;
					}
					message.reply("Ok, ho disattivato il cambio delle activity.").queue();
					thrActivity.setKeepGoing(false);
				}
				default -> message.reply("<:"+Emotes.harry_fotter+">").queue();
			}
			
		}catch (Exception e)
		{
			canaleBot.sendMessage("diocane\n"+e).queue();
		}
		
	}
	
	private boolean contains(String source, String[] subItem)
	{
		String pattern;
		Pattern p;
		Matcher m;
		
		for (String s : subItem)
		{
			pattern = "\\b" + s + "\\b";
			p = Pattern.compile(pattern);
			m = p.matcher(source);
			if (m.find())
				return true;
		}
		return false;
	} // fine metodo contains()
	
	private void timer()
	{
		final var max = 604800; // 604800 secondi = 1 settimana
		String[] msgSplittato;
		try
		{
			//channel.sendMessage("sto nel try, prima di `messageRaw.split(\" \")`, sto na favola").queue();
			msgSplittato = messageRaw.split(" ");
			//channel.sendMessage("msgSplittato.length: "+msgSplittato.length+"\nmsgSplittato: "+ Arrays.toString(msgSplittato)).queue();
		}catch (Exception e)
		{
			channel.sendMessage(""+e).queue();
			return;
		}
		
		if (msgSplittato.length == 1) // !timer senza argomenti
		{
			var m = "Usa `!timer <tempo> [nome del timer] per impostare un timer.`\nEsempio: `!timer 5` imposterà un timer per 5 secondi.";
			channel.sendMessage(m).queue();
		}
		else
		{
			var timeInSeconds = Integer.parseInt(msgSplittato[1]); // time to sleep in seconds
			String reason = "";
			
			if (msgSplittato.length >= 3)
				reason = msgSplittato[2];
			
			//channel.sendMessage("Sto dopo `msgSplittato[1]` e `msgSplittato[2]`, sto na favola").queue();
			//channel.sendMessage("msgSplittato[1]: "+msgSplittato[1]+"\nmsgSplittato[2]: "+msgSplittato[2]).queue();
			
			try
			{
				//channel.sendMessage("Sto nel secondo try, sto na favola").queue();
				if (timeInSeconds < 0 || timeInSeconds > max)
				{
					channel.sendMessage("Hai inserito un numero non valido. Timer non impostato.").queue();
					return;
				}
				new ThreadTimer(message, timeInSeconds, author, reason).start();
			//	channel.sendMessage("Sto dopo il `thread.start()`, sto na favola").queue();
			}
			catch (Exception e)
			{
				channel.sendMessage("Hai inserito un numero non valido.").queue();
			}
			//channel.sendMessage("sto prima di chiudere la funzione `timer()`, sto na favola.").queue();
		}
	} // fine timer()
	
	/** Gestisce i comandi slash (ancora da implementare) */
	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		if (event.getName().equalsIgnoreCase("pog"))
			event.getChannel().sendMessage("<:"+ Emotes.pogey + ">").queue();

	} // fine onSlashCommand()

	/** Trasforma il testo da normale a parodia simil-CaMeL cAsE
	 * @param msg il testo originale.
	 * @return la stringa originale adesso trasformata.
	 * */
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
	
	/** Crea un oggetto di tipo Bot.Challenge e tiene conto di chi è sfidato, chi è lo sfidante e su quale gioco. */
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
	
	/**Metodo che rende omaggio al defunto specificato dall'utente.<br>Uso: <b>!f < stringa ></b>*/
	public void payRespect()
	{
		if (messageRaw.split(" ")[1] == null)
		{
			channel.sendMessage("`Usa !f per omaggiare qualcuno.`").queue();
			return;
		}
		
		var ded = messageRaw.split(" ")[1];
		var mentionedUsers = message.getMentionedUsers();
		if (!mentionedUsers.isEmpty())
			ded = mentionedUsers.get(0).getName();
		
		if (ded.equalsIgnoreCase(authorName))
		{
			message.reply("<:"+Emotes.harry_fotter+">").queue();
			return;
		}
		
		String[] cuori = {"❤️", "💛", "💙", "🧡", "💚", "💜"};
		String[] imgs =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.imgflip.com%2F4rf5nr.jpg&f=1&nofb=1&ipt=0a6b54aa3965c4ec92081a03fdb37f8d1d490426003b6cbeb6ec2420619515dd&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.kym-cdn.com%2Fentries%2Ficons%2Ffacebook%2F000%2F017%2F039%2Fpressf.jpg&f=1&nofb=1&ipt=0a56a685ea4605c86c4d6caea860a6c1480a6e88a982538acc34623ac5204bdc&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--1H4GzubW--%2Fb_rgb%3A908d91%2Ct_Heather%2520Preview%2Fc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1496153439%2Fproduction%2Fdesigns%2F1634415_1.jpg&f=1&nofb=1&ipt=bb9133ef4feef513b2605c621fed68f5232116b5d0fdf22a1def833954f7121a&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Flarepublica.pe%2Fresizer%2FtCNDKWPuGG-0WEGZssIsQEfLQME%3D%2F1200x660%2Ftop%2Farc-anglerfish-arc2-prod-gruporepublica.s3.amazonaws.com%2Fpublic%2F2V3AHQ3PKJGRHJKYX3H2STL7YA.png&f=1&nofb=1&ipt=278b01944e9ca2ea01cddd6f132edbb2bd89a0dc280ea67a9ba3dd9fd3f212f7&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.imgflip.com%2F3ni7oz.jpg&f=1&nofb=1&ipt=2cf84114527ff5e7b02fb19ae74fe596b1d66baba35daa7eb7c8862adcdfe9af&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.F17KjnL0a6N4Eat2f_ZOmwHaFH%26pid%3DApi&f=1&ipt=ff5e29783603bb76136acf9dc5ae713b54335765885fcd62b93be760cac71b9a&ipo=images"
		};
		var cuoreDaUsare = cuori[random.nextInt(cuori.length)];
		var imgDaUsare = imgs[random.nextInt(imgs.length)];
		
		var embed = new EmbedBuilder()
			.setTitle("In loving memory of " + ded + " " + cuoreDaUsare)
			.setColor(Color.black)
			.setDescription("F")
			.setImage(imgDaUsare)
			.setFooter(authorName + " pays his respects.")
			.build();
		
		channel.sendMessageEmbeds(embed).queue(l->react(Emotes.o7));
		
	} // fine payRespect()
	
	
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
		String domanda = domandaERisposte[0].substring(5); // !poll.length() = 5
		String[] risposte = messageRaw.substring(5+domanda.length()+1).split("/");
		
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
	public static void react(String emote)
	{
		var emoteDaUsare = Emotes.emoteDaUsare(emote);
		
		if (emoteDaUsare.equals(""))
			return;
		
		try
		{
			switch (emoteDaUsare)
			{
				case "obito" ->
				{
					for (String str : Emotes.obito)
						channel.addReactionById(id, str).queue();
				}
				case "sabaping" ->
				{
					for (String str : Emotes.sabaPing)
						channel.addReactionById(id, str).queue();
				}
				case "hitman", "uomo colpo" ->
				{
					for (int i : Emotes.hitman)
						channel.addReactionById(id, Emotes.letters[i]).queue();
				}
				case "xcom", "icscom" ->
				{
					for (int i : Emotes.XCOM)
						channel.addReactionById(id, Emotes.letters[i]).queue();
				}
				case "scarab" ->
				{
					for (String str : Emotes.scarab)
						channel.addReactionById(id, str).queue();
				}
				default -> channel.addReactionById(id, emoteDaUsare).queue();
			} // fine switch
		} // fine try
		catch (ErrorResponseException e)
		{
			System.out.printf("Errore nell'aggiunta della reazione \"%s\"\n\t", emoteDaUsare);
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
			final int colpa = random.nextInt(100);
			final String utente = utenteTaggato.get(0).getName();
			String[] particella = {"allo ", "all'", "al "};
			int index = switch (colpa)
			{
				case 0 -> 0;
				case 1, 8, 11, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89 -> 1;
				default -> 2;
			};
			
			final String risposta = String.format("%s è colpevole %s%d%%", utente, particella[index], colpa);
			
			var embed = new EmbedBuilder()
				.setTitle(risposta)
				.setColor(0xFF0000)
				.setFooter("", urlOwO);
			
			channel.sendMessageEmbeds(embed.build()).queue(lambda ->
			{
				String[] emotes = {"pigeon", "smh", "dansgame", "pogey"};
				react(emotes[random.nextInt(emotes.length)]);
			});
		}
		
	} // fine colpevolezza()
	
	/** Mostra un embed con le informazioni del bot */
	public void info()
	{
		var embedBuilder = new EmbedBuilder();
		var size = commandsHashMap.size();
		var urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		var urlTitle = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
		
		embedBuilder.setTitle("Informazioni", urlTitle);
		embedBuilder.setDescription("Questo bot permette di lanciare monete, creare sondaggi e, soprattutto, essere un rompiballe.");

		for (String s : commandsHashMap.keySet())
			embedBuilder.addField("`"+s+"`", "*"+commandsHashMap.get(s)+"*", false);
		
		embedBuilder.setThumbnail(urlOwO)
			.setColor(0xFF0000)
			.addBlankField(false)
			.setFooter("Creato con ❤️ da JohnWeak", urlOwO);
		
		var embed = embedBuilder.build();
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

	/** Mette in pausa il thread per un totale di secondi pari a <code>millis</code> + un valore casuale fra <code>0</code> e <code>bound</code>.
	 * <br>Parametri negativi faranno sì che vengano usati i valori di default:<br><code>millis=1500</code><br><code>bound=500</code>.
	 * @param millis tempo minimo per il quale deve dormire il thread.
	 * @param bound valore casuale da sommare a <code>millis</code>. 0 = nessun tempo di sleep aggiuntivo.
	 * */
	public static void pause(int millis, int bound)
	{
		if (millis < 0)
			millis = 1500;

		if (bound < 0)
			bound = 500;

		try { Thread.sleep(millis + random.nextInt(bound)); }
		catch (InterruptedException e)
		{
			canaleBot.sendMessage("`"+Commands.class+"\nInterruptedException`\n"+ e).queue();
		}
	} // fine pause()
	
	
	
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
		var mortiAnno = 0;
		
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
			var x = (String) (objs).get(scelta).get("date"); // es.: 2022-01-05
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
	
	/**Questo metodo invia un embed al canale da cui ha ricevuto l'ultimo messaggio.*/
	public void sendEmbedToChannel(MessageEmbed messageEmbed, boolean thread)
	{
		if (!thread)
			channel.sendMessageEmbeds(messageEmbed).queue();
		else
			canaleBot.sendMessageEmbeds(messageEmbed).queue();
	} // fine sendEmbedToChannel()
	
	public void moduloDiSicurezza()
	{
		var active = "**IL MODULO DI SICUREZZA È ORA ATTIVO. ESSO GARANTISCE SICUREZZA AL BOT.\nTUTTE LE AZIONI SONO SORVEGLIATE E ALLA PRIMA INFRAZIONE VERRANNO ALLERTATE LE AUTORITÀ COMPETENTI E INCOMPETENTI.**";
		var inactive = "**IL MODULO DI SICUREZZA È STATO DISATTIVATO. LA SICUREZZA DEL BOT È ADESSO GARANTITA DALLA PRESENZA DI GION.**";
		
		var isActive = moduloActive ? active : inactive;
		
		canaleBot.sendMessage(isActive).queue();
		
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
		var authorized = discr.equals(Utente.GION);
		
		String[] messaggiScortesi =
		{
			"CAZZO VUOI?", "NESSUNO TI HA CHIESTO NULLA.", "FATTI GLI AFFARI TUOI.",
			"NON GUARDARE IL BOT.", "NON TOCCARE IL BOT.", "NON PRENDERE GELATI MANO NELLA MANO COL BOT.",
			"NON SEI AUTORIZZATO A CHIEDERE I NUMERI DEL LOTTO AL BOT.", "NON INFASTIDIRE IL BOT.",
			"QUANDO LA VITA TI DÀ I LIMONI, TU NON ROMPERE LE PALLE AL BOT.", "TROVA LA PACE INTERIORE, MA LONTANO DAL BOT.",
			"IL BOT HA BISOGNO DI RINFORZI SU CLASH, NON DI ESSERE INFASTIDITO.", "NO.", "SCORDATELO.", "IMMAGINA DI ESSERE UN FIUME E SCORRI LIBERO E LONTANO DAL BOT.",
			"UN TEMPO ERO UN AVVENTURIERO COME TE, MA POI HO SMESSO DI CAGARE IL CAZZO AL BOT.", "CHE DIO TI ABBIA IN GLORIA, DOPO CHE TI AVRÒ UCCISO SE NON TI ALLONTANI DAL BOT.",
			"ALT. NON UN ALTRO PASSO.", "NON SEI AUTORIZZATO A RESPIRARE VICINO AL BOT.", "HAI SICURAMENTE DI MEGLIO DA FARE CHE INFASTIDIRE IL BOT.",
			"PERCHÈ NON VOLI VIA? AH GIÀ, GLI ASINI NON VOLANO.", "CIRCUMNAVIGA L'ASIA PIUTTOSTO CHE DARE FASTIDIO AL BOT.",
			"SII IL CAMBIAMENTO CHE VUOI VEDERE NEL MONDO, QUINDI CAMBIA IN UNA PERSONA CHE NON SCASSA I COGLIONI AL BOT.",
			"MI PAREVA DI AVERTI DETTO DI NON INTERFERIRE COL BOT, MA FORSE NON TE L'HO DETTO ABBASTANZA BENE: NON INTERFERIRE COL BOT.",
			"AVVICINATI AL BOT E PRENDERAI LE BOT", "VAI A PASCOLARE CAZZI LONTANO DAL BOT", "PERCHÈ NON DIVENTI UN ASTRONAUTA? COSÌ PUOI ANDARE GIRANDO NELLO SPAZIO INVECE DI INFASTIDIRE IL BOT.",
			"SALPA PER I SETTE MARI ALLA RICERCA DI \"UN PEZZO\" INVECE CHE AVVICINARTI AL BOT.", "IL BOT NON DESIDERA LA TUA COMPAGNIA.", "CI SONO 206 OSSA NEL CORPO UMANO. SO ROMPERLE TUTTE E LO FARÒ SE NON TI ALLONTANI DAL BOT.",
			"CERCA LA RISPOSTA TRAMITE MEDITAZIONE INVECE CHE CHIEDERLA AL BOT.", "ESISTONO INFINITI UNIVERSI, EPPURE IN NESSUNO DI QUESTI TU SEI AUTORIZZATO A STARE VICINO AL BOT",
			"ESPLORA LA SINGOLARITÀ DI UN BUCO NERO INVECE DI AVVICINARTI IL BOT.", "IL BOT NON RISPONDERÀ ALLE TUE AVANCE.",
			"FAI UNA SPEEDRUN SU VAINGLORY INVECE CHE GUARDARE IL BOT", "CI SONO MOLTE COSE CHE PUOI GUARDARE AD OCCHIO NUDO INVECE CHE IL BOT: IL SOLE, AD ESEMPIO.",
			"SE IL BOT È IL ROAD RUNNER, TU SEI WILE E. COYOTE", "SONO CERTO CHE HAI DI MEGLIO DA FARE CHE INFASTIDIRE IL BOT.",
			"NESSUNO TOCCA IL BOT E SOPRAVVIVE PER RACCONTARLO.", "IL BOT È ANDATO A FARE LA SPESA: LASCI UN MESSAGGIO E NON SARÀ RICONTATTATO."
		};
		
		String reply = "";
		
		if (messageRaw.length() <= hotkey)
			reply = authorized ? "**SONO AI SUOI ORDINI, SIGNORE.**" : "**"+messaggiScortesi[random.nextInt(messaggiScortesi.length)]+"**";
		else
			reply = authorized ? "**RICEVUTO.**" : "**"+messaggiScortesi[random.nextInt(messaggiScortesi.length)]+"**";
		
		message.reply(reply).queue();
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
			channel.sendMessage(""+e+"\n"+e.getStackTrace()[0]).queue();
		}
	} // fine dado()
	
	
} // fine classe Bot.Commands