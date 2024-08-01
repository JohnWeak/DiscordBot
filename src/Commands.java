import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	private static final Object object = Commands.class;
	private static final Error<Exception> error = new Error<>();
	private static final Error<String> errorString = new Error<>();
	public static final String botChannel = "\uD83E\uDD16bot-owo";
	private static final Random random = new Random();
	public static MessageChannel channel;
	private static long messageID;
	public static Message message;
	public static User author;
	public static TextChannel canaleBotPokemon;
	public static TextChannel canaleBot;
	
	private final int MAX_REMINDERS = 3;
	private final Locale italian = Locale.ITALIAN;
	private final int currentYear = new GregorianCalendar().get(Calendar.YEAR);
	private final boolean moduloSicurezza = false;
	private final ThreadReminder[] remindersArray = new ThreadReminder[MAX_REMINDERS];
	
	private User user;
	public String authorName;
	private String authorID;
	private HashMap<String,String> commandsHashMap;
	public String messageRaw;
	private JDA jda;
	private Pokemon pokemon;
	
	
	/** onReady() viene eseguita soltanto all'avvio del bot */
	public void onReady(@NotNull ReadyEvent event)
	{
		commandsHashMap = Cmd.init();
		
		jda = event.getJDA();
		final var act = jda.getPresence().getActivity();
		
		String activityType="act_type", nomeActivity="act_name", activityTradotta="act_trad";
		final PrivateMessage gion = new PrivateMessage(Utente.getGion());
		
		try
		{
			canaleBot = jda.getTextChannelsByName(botChannel, true).get(0);
			canaleBotPokemon = jda.getTextChannelsByName("pokémowon", true).get(0);
		}
		catch (Exception e)
		{
			error.print(object, e);
		}
		
		if (act != null)
		{
			activityType = act.getType().toString();
		    nomeActivity = "**" + act.getName() + "**";
		    activityTradotta = activityType.equals("WATCHING") ? "guardo " : "gioco a ";
		}
		
		// moduloDiSicurezza();
		
		gion.send("Riavvio completato.");
		
	} // fine onReady()

	/** Questo metodo decide cosa fare quando un messaggio viene modificato */
	public void onMessageUpdate(@NotNull MessageUpdateEvent event)
	{ //
		user = event.getAuthor();
		identifyLatestMessage(null, event);
		aggiungiReazioni();
		checkForKeywords(message.getContentStripped().toLowerCase());
	} // fine onMessageUpdate()
	
	/** Gestisce i messaggi inviati in qualsiasi canale testuale di qualsiasi server in cui è presente il bot */
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		user = event.getAuthor();
		authorID = user.getId();
		
		identifyLatestMessage(event, null);
		
		if (event.isFromGuild())
			guildMessage(event, author.isBot());
		else
			privateMessage(author.isBot());
	
		if (random.nextInt(100) == 42)
			jda.getPresence().setActivity(Main.selectActivity());
		
		
	} // fine onMessageReceived()
	
	
	private void guildMessage(MessageReceivedEvent event, boolean isBot)
	{
		aggiungiReazioni();
		checkForKeywords(message.getContentStripped().toLowerCase());
	} // fine guildEvent()
	
	/**Gestisce i messaggi privati che il bot riceve. Se è un altro bot a inviarli, li ignora.
	 * @param isBot <code>true</code> se l'autore del messaggio è un bot a sua volta, <code>false</code> altrimenti. */
	public void privateMessage(boolean isBot)
	{
		var botOrHuman = isBot ? "Bot" : "User";
		System.out.printf("\t%s %s = \"%s\"; // Private Message\n}\r", botOrHuman, authorName, messageRaw);
		
		if (isBot || authorID.equals(Utente.ID_GION))
			return;
		
		var attachments = message.getAttachments();
		var toSend = authorName + " ha scritto: \"" + messageRaw + "\"";
		var gion = new PrivateMessage(Utente.getGion());
		
		if (attachments.isEmpty())
			gion.send(toSend);
		else
			gion.send(toSend, attachments.get(0));
		
		
		if (moduloSicurezza)
		{
			if (author.getId().equals(Utente.ID_ENIGMO))
			{
				PrivateMessage enigmo = new PrivateMessage(Utente.getEnigmo());
				enigmo.send("<:"+Emotes.ragey+">");
			}
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
			messageID = received.getMessageIdLong();
			author = received.getAuthor();
			authorName = author.getName();
			message = received.getMessage();
			messageRaw = message.getContentRaw();
			channel = received.getChannel();
		}
		else // updated
		{
			messageID = updated.getMessageIdLong();
			author = updated.getAuthor();
			authorName = author.getName();
			message = updated.getMessage();
			messageRaw = message.getContentRaw();
			channel = updated.getChannel();
		}
		
	} // fine identifyLatestMessage()
	
	/**Questo metodo aggiunge a un messaggio la stessa reazione che piazza l'utente*/
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
	{
		var emote = event.getReactionEmote();
		channel = event.getChannel();
		messageID = event.getMessageIdLong();
		message = channel.getHistory().getMessageById(messageID);
		boolean isEmoji = emote.isEmoji();
		
		String emoteName = emote.getName(), emoteId = "";
		
		if (!isEmoji)
			emoteId = emote.getId();
		
		String reaction = (isEmoji ? emoteName : emoteName+":"+emoteId);
		
		try
		{
			react(reaction);
		}
		catch (Exception e)
		{
			error.print(object, e);
		}
	} // fine onMessageReactionAdd
	
	/**Inserisce come reazioni tutte le emote che trova nel messaggio*/
	private void aggiungiReazioni()
	{
		if (message != null)
		{
			List<Emote> emoteList = message.getEmotes();
			
			for (Emote emote : emoteList)
			{
				try
				{
					message.addReaction(emote).queue();
				} catch (Exception ignored) {}
			}
		}
		
	} // fine aggiungiReazioni()
	
	
	/** Controlla che il messaggio abbia le parole chiave per attivare i comandi (o le reazioni) del bot
	 * @param msgStrippedLowerCase la stringa del messaggio inviato convertita in minuscolo.
	 * */
	public void checkForKeywords(String msgStrippedLowerCase)
	{
		final String[] args = messageRaw.split(" ");
		final String comando = args[0].toLowerCase(italian);
		boolean reply = false;
		String msgReply = "";
		
		// se è un bot a mandare il messaggio, ignoralo per evitare loop di messaggi
		if (author.isBot())
		{
			// se però è il bot owo a mandare il messaggio, prima fai un paio di robe e poi return
			if (authorID.equals(Utente.ID_OWOBOT))
			{
				react("owo");
				react("vergognati");
				
				if (msgStrippedLowerCase.contains("daily streak"))
				{
					var msgSplittato = msgStrippedLowerCase.split(" ");
					final var size = msgSplittato.length;
					var auth = "";
					var numGiorni = 0;
					var channelHistory = Utilities.channelHistory(channel,false,3);
					
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
					} // fine try
					catch (Exception e)
					{
						error.print(object, e);
					} // fine catch
					
					if (numGiorni == 0 || !(numGiorni % 365 == 0))
					{
						return;
					}
					else
					{
						if (auth.isEmpty())
						{
							errorString.print(object,"<@"+Utente.ID_GION+">\n`auth è una stringa vuota`.");
							return;
						}
						
						var years = (numGiorni / 365);
						anniversario(auth, years);
					}
				} // fine if daily streak
			} // fine if equals bot
			
			
			if (authorID.equals(Utente.ID_BOWOT)) // self own
				if (random.nextInt(1000) == 42) // 0,1%
					message.reply("BOwOt vergognati").queue(lambda -> react("vergognati"));
			
			return;
		} // fine if isBot
		
		//if (!msgStrippedLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
		//	encounter();
		
		if (random.nextInt(500) == 42) // chance di reagire con emote personali
		{
			final var trigger = random.nextBoolean();
			
			if (trigger)
			{
				triggera(authorID);
			}
			else
			{
				switch (authorID)
				{
					case Utente.ID_OBITO ->
					{
						react("obito");
						react("vergognati");
						message.reply("Òbito vergognati").queue();
					}
					case Utente.ID_ENIGMO -> react("pigeon");
					case Utente.ID_LEX -> channel.addReactionById(authorID, "🇷🇴").queue();
					case Utente.ID_GION -> react("smh");
					
				} // fine switch
				
				final String[] reazione = {"dansgame", "pigeon", "smh"};
				final var scelta = random.nextInt(reazione.length);
				
				message.reply(Utilities.camelCase(messageRaw)).queue(lambda -> react(reazione[scelta]));
				
			} // fine else
			
		} // fine if reazioni
		
		if (random.nextInt(100) == 42)
		{
			canaleBot.sendMessage(Emotes.readyToSend(Emotes.pogey)).queue();
		}
		
		switch (comando)
		{
			case "!coinflip", "!cf" -> coinflip();
			case "!poll" -> poll();
			case "!info" -> info();
			case "!8ball" -> eightBall();
			case "!pokemon" -> encounter();
			case "!colpevolezza", "!colpevole" -> colpevolezza();
			case "!carta" -> {Card c = new Card(); c.sendCarta(c);}
			case "!massshooting", "!ms" -> massShooting();
			// Nota: i comandi di clash sono disabilitati poiché la loro API
			//  richiede un token diverso ogni volta che cambia l'indirizzo IP
			//case "!war" -> new Clash().clashWar();
			//case "!league" -> new Clash().clashWarLeague(false);
			case "!smh" -> new ThreadSmh(channel).start();
			case "!dado" -> dado();
			case "!cattura", "!catch" -> cattura(pokemon);
			case "!f" -> payRespect();
			case "!r", "!reminder" -> reminder();
			case "!certificazione" -> certificazione();
			case "!pigeons" -> pigeons();
			// case "!dm" -> dm();
		}
		
		// arraylist per contenere le reazioni da aggiungere al messaggio
		var reazioni = new ArrayList<String>();
		// var emojis = findEmojis(msgStrippedLowerCase);
		
		if (msgStrippedLowerCase.contains("ehi modulo"))
			ehiModulo();
		
		final var pigeonRegex = ".*piccion[ei].*|.*pigeon.*|.*igm[oa].*";
		final var enigmoRegex = ".*igm[oa].*";
		final var gionRegex = ".*gion.*|.*john.*";
		
		if (msgStrippedLowerCase.matches(pigeonRegex))
			new ThreadPigeon(authorName, channel).start();
		
		if (msgStrippedLowerCase.matches(enigmoRegex))
			react(Emotes.pigeon);
		
		if (msgStrippedLowerCase.matches(gionRegex))
			react(Emotes.boo2);
		
		if (msgStrippedLowerCase.contains("owo"))
			reazioni.add(Emotes.OwO);
		
		//if (msgStrippedLowerCase.contains("splitta questo"))
		//	splitMsgAndReply();
		
		if (contains(msgStrippedLowerCase, new String[]{"pog", "manutenzione"}))
			reazioni.add(Emotes.pogey);
		
		if (contains(msgStrippedLowerCase, new String[]{"òbito", "obito", "óbito"}))
		{
			if (random.nextInt(50) == 42) // 2%
			{
				reazioni.add("obito");
				reazioni.add("vergogna");
			}
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
			reazioni.add(Emoji.INDIA_BANDIERA);
		}
		
		if (msgStrippedLowerCase.contains("live") && authorID.equals(Utente.ID_OBITO))
			reazioni.add(Emotes.harry_fotter);

		// a questo punto smetto di controllare se ci siano reazioni e le aggiungo effettivamente al messaggio
		if (!reazioni.isEmpty())
		{
			for (String emote : reazioni)
				react(emote);
			
			reazioni.clear();
		}
		
		if (msgStrippedLowerCase.contains("http"))
			detectTwitterLink();
		
		if (msgStrippedLowerCase.contains("random") || msgStrippedLowerCase.contains("numero casuale"))
		{
			reply = true;
			int n = random.nextInt(0, 100);
			msgReply += "Numero casuale: **"+n+"**";
		}
		
		if (msgStrippedLowerCase.equalsIgnoreCase("cancella questo messaggio"))
		{
			if (random.nextInt(50) == 42)
				message.reply("No.").queue(l -> react("getrekt"));
			else
			{
				channel.sendTyping().queue();
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
			var date = Utilities.getCurrentTime();
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
				final var orario = new Ore(hour, minutes);
				
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
					message.reply(Utilities.getSaluto() + " anche a te").queue();
				else
					message.reply("No, vaffanculo >:(").queue();
			}
		}
		
		if (msgStrippedLowerCase.matches("dammi il (?:cinque|5)") || msgStrippedLowerCase.contains("high five"))
		{
			reply = true;
			msgReply += "🤚🏻\n";
		}
		
		if (msgStrippedLowerCase.contains("grazie") && random.nextInt(50) == 42)
		{
			reply = true;
			msgReply += "Prego.\n";
		}
		
		if (msgStrippedLowerCase.matches("egg *dog"))
			message.reply(GIF.eggdog).queue();
		
		if (msgStrippedLowerCase.contains("spy") && random.nextInt(3) == 0)
			message.reply(GIF.spyHang).queue();
		
		if (msgStrippedLowerCase.matches("you(?:'re| are) ugly"))
			message.reply(GIF.engineer).queue();
		
		if (msgStrippedLowerCase.contains("deez nuts") && authorID.equals(Utente.ID_ENIGMO))
		{
			reply = true;
			msgReply += "DEEZ NUTS, Enigmo!\n";
			reazioni.add(Emoji.ARACHIDI);
		}
		
		if (msgStrippedLowerCase.contains("serve aiuto"))
		{
			reply = true;
			msgReply += "Nemico assente!\n";
		}
		
		var users = message.getMentionedUsers();
		
		if (!users.isEmpty() && users.get(0).getId().equals(Utente.ID_BOWOT))
		{
			boolean matchFound = false;
			final String[] cases = {"che fai", "cosa fai", "che stai facendo", "cazzo fai", "minchia fai"};
			for (String m : cases)
			{
				if (msgStrippedLowerCase.contains(m))
				{
					matchFound = true;
					break;
				}
			}
			
			if (matchFound)
			{
				final String actTrad, name, tipo, saluto, activity;
				String risposta;
				
				actTrad = Main.getActivityTradotta();
				name = Main.getActivity().getName();
				tipo = Main.getTipo();
				saluto = (random.nextInt(5) == 3) ? Utilities.getSaluto() : "Ciao";
				activity = actTrad.equals("guardo") ? "guardando " : "giocando a";
				
				risposta = String.format("%s, sto %s **%s**", saluto, activity, name);
				risposta += tipo.equals("gioco") ? " ("+tipo+")." : ".";
				
				message.reply(risposta).queue();
			}
		}
		
//		if (msgStrippedLowerCase.contains("") && random.nextInt(42) == 0){}
		
		if (reply)
			message.reply(msgReply).queue();
		else if (random.nextInt(1000000) == 42)
			message.reply("***ULTIMO AVVISO: __NON FARLO MAI PIÙ__.***").queue();
		
	} // fine checkForKeywords()
	
	/**Ottiene le emote custom del canale*/
	private void getEmotes()
	{
		final var emotesArray = Arrays.toString(canaleBot.getGuild().getEmotes().toArray());
		// discord limita i messaggi a 2000 caratteri per messaggio
		final var msg = emotesArray.length() > 2000 ? emotesArray.substring(0,1999) : emotesArray;
		channel.sendMessage(msg).queue();
		
		/* ********
		// discord limita i messaggi a 2000 caratteri per messaggio
		if (emotesArray.length() > 2000)
			 channel.sendMessage(emotesArray.substring(0, 1999)).queue();
		else
			channel.sendMessage(emotesArray).queue();
			
		************/
		
	} // fine getEmotes()
	
	private void pigeons()
	{
		final String[] urls =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.V1RVSmHl3MqMMGmXMKgcnwHaFk%26pid%3DApi&f=1&ipt=f055c51255e37f52fca69d9410933b4a1520963c184be4946a4d736f197e6768&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fsandiegobirdspot.com%2Fwp-content%2Fgallery%2Frock-pigeon%2FQL0A6004.jpg&f=1&nofb=1&ipt=1034b878d7be04b8254c55e089faf6498df8263e75ffd454b27105293e143315&ipo=images",
			"https://www.allaboutbirds.org/guide/assets/og/75368111-1200px.jpg",
			"http://www.nejohnston.org/birds/2015/12/Images/IMG_7695.jpg",
			"https://petkeen.com/wp-content/uploads/2021/07/pigeon-resting-on-rail.jpg",
			"http://pigeondaily.com/wp-content/uploads/2012/10/photodune-2094416-pigeon-s.jpg",
			"https://pixnio.com/free-images/2017/05/23/2017-05-23-15-00-41.jpg",
			"https://www.publicdomainpictures.net/pictures/80000/velka/pigeon-1395295275oKY.jpg",
			"https://www.paws.org/wp-content/uploads/2019/12/Wild-Pigeon.jpg",
			"https://www.publicdomainpictures.net/pictures/50000/velka/pigeon-1368534985dAa.jpg",
			"https://pngimg.com/uploads/pigeon/pigeon_PNG3424.png",
			"https://wackyroger.com/z-photogalleries/2018/03-24-18pigeon/pigeon0004.jpg",
			"https://i.pinimg.com/originals/f4/f2/7d/f4f27d3588fa94058e21e35bb083bb5c.jpg",
			"http://4.bp.blogspot.com/-eTvxapTkhVg/T_x2G-LkRLI/AAAAAAAAEoc/m6V4djAgjtE/s1600/Pigeon+Birds+Wallpapers_3.jpeg",
			"https://wallpapercave.com/wp/wp1976143.jpg",
			"https://www.pigeonsloobuyck.be/wp-content/uploads/2017/04/be12-4153458_pigeon.jpg",
			"https://www.animalstown.com/animals/p/pigeon/wallpapers/pigeon-wallpaper-4.jpg",
			"http://2.bp.blogspot.com/_U2RoHEXMKA4/TPRn6OeL5dI/AAAAAAAAAAQ/AgDGd0ANbO0/s1600-R/pigeon2508wb.jpg",
			"https://b.rgbimg.com/users/l/lu/lusi/600/nrAohvg.jpg",
			"https://vergez.net/blog/public/Pigeon_Ramier/.DSC_0063a_m.jpg",
			"https://static.actu.fr/uploads/2019/05/pigeon-854x640.jpg",
			"https://protectiondesoiseaux.be/wp-content/uploads/2020/06/photo-pigeon-site.jpg",
		};
		
		final var img = urls[random.nextInt(urls.length)];
		final var embed = new EmbedBuilder()
			.setColor(new Color(42,42,42))
			.setImage(img)
			.build();
		
		message.replyEmbeds(embed).queue();
	}
	
	private void anniversario(String author, int years)
	{
		final String descr = String.format("%s, sono passati ben %d anni dal tuo primo OwO daily. Sono davvero orgoglioso e fiero di te.", author,years);
		final EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("È il tuo dailyversario!");
		embed.setColor(Color.RED);
		embed.addField("Festeggiato",author,true);
		embed.addField("Anni", String.valueOf(years), true);
		embed.addField("", descr,false);
		embed.setThumbnail(user.getAvatarUrl());
		
		message.replyEmbeds(embed.build()).queue();
	}
	
	private void certificazione()
	{
		final int red, green, blue;
		red = random.nextInt(255);
		green = random.nextInt(255);
		blue = random.nextInt(255);
		final String tony = "<:"+Emotes.tonyakaradio105+">";
		final String[] certificazioni = {" ha la certificazione **IP68**", " ha la certificazione "+tony};
		final String[] check = {"✅","❌"};
		final StringBuilder msg = new StringBuilder();
		
		if (authorID.equals(Utente.ID_GION))
		{
			certificazioni[0] = check[0] + certificazioni[0];
			certificazioni[1] = check[0] + certificazioni[1];
		}
		else
		{
			certificazioni[0] = check[1] + "non" + certificazioni[0];
			certificazioni[1] = check[1] + "non" + certificazioni[1];
		}
		
		for (String s : certificazioni)
			msg.append(s).append("\n");
		
		final var embed = new EmbedBuilder()
			.setTitle(authorName)
			.setColor(new Color(red,green,blue))
			.setDescription(msg)
			.setImage(author.getAvatarUrl())
			.build();
		
		message.replyEmbeds(embed).queue();
	} // fine certificazione()
	
	/**Sostituisce i link di twitter con quelli di <code>fxtwitter</code>, che caricano l'anteprima su discord*/
	private void detectTwitterLink()
	{
		final String[] parts = messageRaw.split(" ");
		String firstHalf, secondHalf, newURL = "";
		boolean twitterDetected = false;
		
		for (String m : parts)
		{
			// due regex anziché uno solo perché il numero di caratteri da manipolare cambia
			var regex1 = "https*://twitter\\.com.*";
			var regex2 = "https*://www\\.twitter\\.com.*";
			
			if (m.matches(regex1))
			{
				
				twitterDetected = true;
				
				firstHalf = m.split("//")[0]+"//";
				secondHalf = m.split("//")[1];
				newURL = String.format("%sfx%s", firstHalf, secondHalf);
				
				break;
			}
			else if (m.matches(regex2))
			{
				twitterDetected = true;
				
				firstHalf = m.split("//")[0]+"//";
				secondHalf = m.split("//")[1].substring(4) + "fx";
				newURL = String.format("%sfx%s", firstHalf, secondHalf);
				
				break;
			}
		}
		
		if (twitterDetected)
			message.reply(newURL).queue();
		
	} // fine detectTwitterLink()
	
	private void reminder()
	{
		final String title = "Utilizzo comando !reminder", description = "Il comando permette di impostare un promemoria. I parametri sono i seguenti.";
		final MessageEmbed.Field[] fields = new MessageEmbed.Field[]
		{
			new MessageEmbed.Field("d", "Imposta i giorni", true),
			new MessageEmbed.Field("h", "Imposta le ore",true),
			new MessageEmbed.Field("m","Imposta i minuti",true),
			new MessageEmbed.Field("Esempio","`!reminder 3d10h12m`",false),
		};
		
		final String[] mes = messageRaw.split(" ");
		if(mes.length < 2 || mes[1].isEmpty())
		{
			final EmbedBuilder embed = new EmbedBuilder();
			
			embed.setTitle(title);
			embed.setColor(Color.RED);
			embed.setDescription(description);
			embed.addField(fields[0]);
			embed.addField(fields[1]);
			embed.addField(fields[2]);
			embed.addField(fields[3]);
			
			channel.sendMessageEmbeds(embed.build()).queue();
			return;
		}
		
		final String timeString = mes[1];
		final int d = timeString.indexOf('d');
		final int h = timeString.indexOf('h');
		final int m = timeString.indexOf('m');
		
		
		String days=null, hours=null, minutes=null;
		
		// controllare quale formato è presente
		
		final String formatError = "I giorni `(d)` devono precedere le ore `(h)`, che devono precedere i minuti `(m)`. Promemoria non impostato.";
		
		if (d != -1)
		{
			days = timeString.substring(0, d);
		}
		
		if (h != -1)
		{
			if (h < d)
			{
				message.reply(formatError).queue();
				return;
			}
			
			hours = days != null ? timeString.substring(d+1, h) : timeString.substring(0, h);
		}
		
		if (m != -1)
		{
			if ((h != -1 && m < h) || (d != -1 && m < d))
			{
				message.reply(formatError).queue();
				return;
			}
			
			if (days == null && hours == null) // minuti soltanto (5m)
			{
				minutes = timeString.substring(0, m);
			}
			else if (days != null && hours == null) // giorni e minuti (1d2m)
			{
				minutes = timeString.substring(d+1, m);
			}
			else // tutto incluso (1d2h3m)
			{
				minutes = timeString.substring(h+1, m);
			}
			
		}
		// converti le stringhe di tempo in interi
		final int days_int, hours_int, minutes_int;
		int time = 0;
		
		days_int = days == null ? 0 : Integer.parseInt(days);
		hours_int = hours == null ? 0 : Integer.parseInt(hours);
		minutes_int = minutes == null ? 0 : Integer.parseInt(minutes);
		
		final int maxDays = 7, maxHours = 23, maxMinutes = 59;
		if (days_int > maxDays || hours_int > maxHours || minutes_int > maxMinutes)
		{
			final EmbedBuilder embed = new EmbedBuilder();
			
			embed.setTitle(title);
			embed.setColor(Color.RED);
			embed.setDescription(description);
			embed.addField(fields[0]);
			embed.addField(fields[1]);
			embed.addField(fields[2]);
			embed.addField(fields[3]);
			
			channel.sendMessageEmbeds(embed.build()).queue();
			return;
		}
		final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome")), future;
		
		// converti interi in millisecondi
		time += days_int * 24 * 60 * 60 * 1000;
		time += hours_int * 60 * 60 * 1000;
		time += minutes_int * 60 * 1000;
		
		future = now.plusSeconds(time/1000);
		
		final String yearFuture, monthFuture, dayFuture, hourFuture, minuteFuture;
		yearFuture = ""+future.getYear();
		monthFuture = ""+future.getMonthValue();
		dayFuture = ""+future.getDayOfMonth();
		hourFuture = future.getHour() < 10 ? "0"+future.getHour() : ""+future.getHour();
		minuteFuture = future.getMinute() < 10 ? "0"+future.getMinute() : ""+future.getMinute();
		
		String nome = "";
		
		for (int i = 2; i < mes.length; i++)
		{
			nome = nome.concat(" ").concat(mes[i]);
		}
		
		if (nome.isBlank())
		{
			nome = "Promemoria Senza Nome";
		}
		nome = nome.trim();
		
		for(int i = 0; i < MAX_REMINDERS; i++)
		{
			if (remindersArray[i] == null || !remindersArray[i].isActive())
			{
				remindersArray[i] = new ThreadReminder(nome,time, channel);
				remindersArray[i].start();
				
				final String success = String.format("Il tuo promemoria, \"%s\", è impostato per il giorno `%s/%s/%s` alle `%s:%s`\n", nome,dayFuture,monthFuture,yearFuture,hourFuture,minuteFuture);
				
				final EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("Promemoria impostato!");
				embed.setDescription(success);
				embed.setColor(Color.RED);
				channel.sendMessageEmbeds(embed.build()).queue();
				
				break;
			}
			else
			{
				message.reply("Strunz, hai già impostato 3 promemoria. smh.").queue(msg ->
				{
					final StringBuilder sb = new StringBuilder();
					for (ThreadReminder rem : remindersArray)
					{
						sb.append("id: ").append(rem.getId()).append(", nome: ").append(rem.getNome()).append("\n");
					}
					msg.reply(sb).queue(e->react(Emotes.smh));
				});
			}
		}
	} // reminder()
	
	private void encounter()
	{
		final String[] msgSplittato = messageRaw.split(" ");
		String nomePokemon;
		int idPokemon = random.nextInt(1,Pokemon.ALL);
		boolean pokedex;
		
		try
		{
			if (msgSplittato.length > 1)
			{
				nomePokemon = msgSplittato[1];
				idPokemon = Pokemon.getId(nomePokemon);
				if (idPokemon <= 0)
				{
					message.reply("Il pokedex non ha informazioni su `" + nomePokemon + "`.").queue();
					return;
				}
				pokedex = true;
			}
			else
			{
				pokedex = false;
			}
			
			pokemon = new Pokemon(idPokemon, pokedex);
			
			if (msgSplittato.length > 2)
			{
				if (msgSplittato[2].equals("s") || msgSplittato[2].equals("shiny"))
					pokemon.setShiny(true);
			}
			
			pokemon.spawn(pokemon);
			
		}catch (Exception e)
		{
			error.print(object, e);
		}
	} // fine encounter()
	
	public void cattura(Pokemon pokemon)
	{
		ThreadPokemon t;
		if (pokemon == null || !pokemon.isCatturabile() || pokemon.isCatturato())
		{
			message.reply("Non puoi catturarlo gne gne").queue();
			return;
		}
		
		pokemon.setCatturabile(false);
		pokemon.setCatturato(true);
		pokemon.setOwner(author);
		t = pokemon.getThread();
		
		if (t != null && t.isAlive())
		{
			t.runAway();
			t.interrupt();
		}
		
	} // fine cattura()
	
	
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
	
	
	/** Gestisce i comandi slash (ancora da implementare) */
	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		final var eventName = event.getName();
		
		if (eventName.equalsIgnoreCase("pog"))
			event.getChannel().sendMessage(Emotes.readyToSend(Emotes.pogey)).queue();
		
		if (eventName.equalsIgnoreCase("dice") || eventName.equalsIgnoreCase("dado"))
			dado();

	} // fine onSlashCommand()
	
	
	/** Lancia una moneta */
	public void coinflip()
	{
		final var testaEmote = "<:" + Emotes.pogey + ">";
		final var croceEmote = "<:" + Emotes.pigeon + ">";
		final var lancioMoneta = authorName + " lancia una moneta...";

		final var headsOrTails = random.nextBoolean();
		final var responso = lancioMoneta+"\n**È uscito** ";
		final var testaStringa = "**"+testaEmote+"! (Testa)**";
		final var croceStringa = "**"+croceEmote+"! (Croce)**";

		var finalResponso = responso.concat(headsOrTails ? testaStringa : croceStringa);

		channel.sendTyping().queue();
		message.reply(lancioMoneta).queue(m ->
		{
			message.editMessage(finalResponso).queue(m2 -> react(headsOrTails ? "pogey" : "pigeon"));
		});

	} // fine coinflip()
	
	/**Metodo che rende omaggio al defunto specificato dall'utente.<br>Uso: <b>!f &lt;stringa&gt; </b>*/
	public void payRespect()
	{
		if (messageRaw.split(" ")[1] == null)
		{
			channel.sendMessage("`Usa !f per omaggiare qualcuno.`").queue();
			return;
		}
		
		var ded = messageRaw.split(" ")[1];
		final var mentionedUsers = message.getMentionedUsers();
		if (!mentionedUsers.isEmpty())
			ded = mentionedUsers.get(0).getName();
		
		if (ded.equalsIgnoreCase(authorName))
		{
			message.reply("Omaggi te stesso? <:"+Emotes.smh+">").queue();
			return;
		}
		
		final String[] cuori = {"❤️", "💛", "💙", "🧡", "💚", "💜"};
		final String[] imgs =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.imgflip.com%2F4rf5nr.jpg&f=1&nofb=1&ipt=0a6b54aa3965c4ec92081a03fdb37f8d1d490426003b6cbeb6ec2420619515dd&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.kym-cdn.com%2Fentries%2Ficons%2Ffacebook%2F000%2F017%2F039%2Fpressf.jpg&f=1&nofb=1&ipt=0a56a685ea4605c86c4d6caea860a6c1480a6e88a982538acc34623ac5204bdc&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--1H4GzubW--%2Fb_rgb%3A908d91%2Ct_Heather%2520Preview%2Fc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1496153439%2Fproduction%2Fdesigns%2F1634415_1.jpg&f=1&nofb=1&ipt=bb9133ef4feef513b2605c621fed68f5232116b5d0fdf22a1def833954f7121a&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Flarepublica.pe%2Fresizer%2FtCNDKWPuGG-0WEGZssIsQEfLQME%3D%2F1200x660%2Ftop%2Farc-anglerfish-arc2-prod-gruporepublica.s3.amazonaws.com%2Fpublic%2F2V3AHQ3PKJGRHJKYX3H2STL7YA.png&f=1&nofb=1&ipt=278b01944e9ca2ea01cddd6f132edbb2bd89a0dc280ea67a9ba3dd9fd3f212f7&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.imgflip.com%2F3ni7oz.jpg&f=1&nofb=1&ipt=2cf84114527ff5e7b02fb19ae74fe596b1d66baba35daa7eb7c8862adcdfe9af&ipo=images",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.F17KjnL0a6N4Eat2f_ZOmwHaFH%26pid%3DApi&f=1&ipt=ff5e29783603bb76136acf9dc5ae713b54335765885fcd62b93be760cac71b9a&ipo=images"
		};
		final var cuoreDaUsare = cuori[random.nextInt(cuori.length)];
		final var imgDaUsare = imgs[random.nextInt(imgs.length)];
		
		final var embed = new EmbedBuilder()
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
		
		final String format = "^.*\\?\\s*([^/]+\\s*/?\\s*)+[^/]+\\s*$";
		if (!messageRaw.toLowerCase().matches(format) || messageRaw.length() <= 5)
		{
			sondaggio(null,null,true);
			//flag = true fa comparire il messaggio di utilizzo del comando !poll
			return;
		}
		
		final String[] domandaERisposte = messageRaw.split("\\?");
		final String domanda = domandaERisposte[0].substring(5).trim(); // !poll.length() = 5
		final String[] risposte = domandaERisposte[1].split("/");
		//final String[] risposte = messageRaw.substring(5+domanda.length()+1).split("/");
		
		sondaggio(domanda, risposte, false);
	} // fine poll()
	
	/** Crea un sondaggio. Se non sono soddisfatte le condizioni, mostra un messaggio su come usare il comando !poll */
	public void sondaggio(String domanda, String[] risposte, boolean error)
	{
		final EmbedBuilder embedBuilder = new EmbedBuilder();
		
		if (error)
		{
			embedBuilder.setTitle("`!poll` - Istruzioni per l'uso");
			embedBuilder.addField("Sondaggio", "Per creare un sondaggio devi usare il comando `!poll` + `domanda?` + `[risposte]`\nSepara le risposte con uno slash `/`.", false);
			embedBuilder.addField("Esempio", "`!poll domanda? opzione 1 / opzione 2 / opzione 3 ...`\n`!poll Cosa preferite? Pizza / Pollo / Panino / Sushi`", false);
			embedBuilder.addField("Votazione", "Per votare, usa le reazioni al messaggio.", false);
			embedBuilder.setColor(0xFFFFFF);
			
			channel.sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else
		{
			final int lenghtRisposte = risposte.length;
			final String[] reactionLetters =
			{
			    "\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB",
				"\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
				"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7",
				"\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
				"\uD83C\uDDFE", "\uD83C\uDDFF"
			}; // array di lettere emoji A -> Z
			
			String descrizione = "";
			embedBuilder.setTitle(domanda+"?");
			
			for (int i = 0; i < lenghtRisposte; i++)
			{
				risposte[i] = risposte[i].trim();
				descrizione = descrizione.concat(reactionLetters[i] + "\t" + risposte[i]) + "\n";
			}
			
			embedBuilder.setDescription(descrizione);
			embedBuilder.setColor(0xFF0000);
			
			channel.sendMessageEmbeds(embedBuilder.build()).queue((message) ->
			{
				for (int i = 0; i < lenghtRisposte; i++)
					message.addReaction(reactionLetters[i]).queue();
			});
			
		}
		
		
	} // fine sondaggio()
	
	/** Infastidisce le persone */
	public void triggera(String id)
	{
		final String title, image, footer, color;
		final int risultato;
		
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

		final EmbedBuilder embedBuilder = new EmbedBuilder();
		
		switch (id)
		{
			case Utente.ID_OBITO ->
			{
				risultato = random.nextInt(immagineObito.length);
				title = titolo.concat("Òbito");
				image = immagineObito[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0xFFFFFF" : "0xC59FC9";
			}
			
			case Utente.ID_ENIGMO ->
			{
				risultato = random.nextInt(immagineEnigmo.length);
				title = titolo.concat("Enigmo");
				image = immagineEnigmo[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0xCB4D4D" : "0xE5D152";
			}
			
			case Utente.ID_LEX ->
			{
				risultato = random.nextInt(immagineLex.length);
				title = titolo.concat("Lex");
				image = immagineLex[risultato];
				footer = testoFooter;
				color = (risultato == 0) ? "0xD80000" : "0x207522";
			}
			
			case Utente.ID_GION ->
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
		final var emoteDaUsare = Emotes.emoteDaUsare(emote.toLowerCase());
		
		if (emoteDaUsare.isEmpty())
			return;
		
		try
		{
			switch (emoteDaUsare)
			{
				case "obito" ->
				{
					for (String str : Emotes.obito)
						channel.addReactionById(messageID, str).queue();
				}
				case "sabaping" ->
				{
					for (String str : Emotes.sabaPing)
						channel.addReactionById(messageID, str).queue();
				}
				case "hitman", "uomo colpo" ->
				{
					for (int i : Emotes.hitman)
						channel.addReactionById(messageID, Emotes.letters[i]).queue();
				}
				case "xcom", "icscom" ->
				{
					for (int i : Emotes.XCOM)
						channel.addReactionById(messageID, Emotes.letters[i]).queue();
				}
				case "scarab" ->
				{
					for (String str : Emotes.scarab)
						channel.addReactionById(messageID, str).queue();
				}
				default -> channel.addReactionById(messageID, emoteDaUsare).queue();
			} // fine switch
		} // fine try
		catch (ErrorResponseException e)
		{
			error.print(object, e);
		}
	} // fine react()
	
	/** Lascia che RNGesus decida quanto è colpevole l'utente taggato */
	private void colpevolezza()
	{
		final var utenteTaggato = message.getMentionedUsers();
		final String urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		
		if (utenteTaggato.isEmpty())
		{
			final var emb = new EmbedBuilder()
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
			final String[] particella = {"allo ", "all'", "al "};
			final int index = switch (colpa)
			{
				case 0 -> 0;
				case 1, 8, 11, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89 -> 1;
				default -> 2;
			};
			
			final String risposta = String.format("%s è colpevole %s%d%%", utente, particella[index], colpa);
			
			final var embed = new EmbedBuilder()
				.setTitle(risposta)
				.setColor(0xFF0000)
				.setFooter("", urlOwO);
			
			channel.sendMessageEmbeds(embed.build()).queue(lambda ->
			{
				final String[] emotes = {"pigeon", "smh", "dansgame", "pogey"};
				react(emotes[random.nextInt(emotes.length)]);
			});
		}
		
	} // fine colpevolezza()
	
	/** Mostra un embed con le informazioni del bot */
	public void info()
	{
		final var embedBuilder = new EmbedBuilder();
		final var urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		final var urlTitle = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
		
		embedBuilder.setTitle("Informazioni", urlTitle);
		embedBuilder.setDescription("Questo bot permette di lanciare monete, creare sondaggi e, soprattutto, essere un rompiballe.");

		for (String s : commandsHashMap.keySet())
			embedBuilder.addField("`"+s+"`", "*"+commandsHashMap.get(s)+"*", false);
		
		embedBuilder.setThumbnail(urlOwO)
			.setColor(0xFF0000)
			.addBlankField(false)
			.setFooter("Creato con ❤️ da JohnWeak", urlOwO);
		
		final var embed = embedBuilder.build();
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

		message.reply(ballResponse).queue(message1 ->
		{
			final String newResponse = "La 🎱 dichiara: ";
			message1.editMessage(newResponse+"**"+risposte[random.nextInt(risposte.length)]+"**").queue();
		});
		
	} // fine eightBall()
	
	
	
	/**Ottieni un resoconto della sparatoria più recente in USA oppure ottieni un resoconto di una sparatoria scelta casualmente nell'anno da te specificato.*/
	private void massShooting()
	{
		int anno = currentYear;
		final String[] msg = messageRaw.toLowerCase().split(" ");
		if (msg.length > 1)
		{
			try
			{
				anno = Integer.parseInt(msg[1]);
			} catch (NumberFormatException e)
			{
				error.print(object, e);
			}
		}
		
		if (anno < 2013 || anno > currentYear)
		{
			channel.sendMessage("`L'anno dev'essere compreso fra il 2013 e il "+currentYear+".`").queue();
			return;
		}
		
		final JSONArray jsonArray;
		final JSONParser jsonParser = new JSONParser();
		int mortiAnno = 0;
		
		try
		{
			final var url = new URL("https://mass-shooting-tracker-data.s3.us-east-2.amazonaws.com/"+anno+"-data.json");
			
			final var connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			
			final var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			final var response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			
			jsonArray = (JSONArray) jsonParser.parse(String.valueOf(response));
			final var objs = new ArrayList<JSONObject>();
			
			for (Object o : jsonArray)
			{
				objs.add((JSONObject) o);
				mortiAnno += Integer.parseInt((String)((JSONObject) o).get("killed"));
			}
			
			var scelta = 0; // se è anno corrente, prende più recente, altrimenti ne prende una a caso
			
			if (anno != currentYear)
				scelta = random.nextInt(objs.size());
			
			final var citta = (String) objs.get(scelta).get("city");
			final var stato = (String) objs.get(scelta).get("state");
			final var morti = (String) objs.get(scelta).get("killed");
			final var feriti = (String) objs.get(scelta).get("wounded");
			final var x = (String) (objs).get(scelta).get("date"); // es.: 2022-01-05T12:23:34
			final var annoMeseGiorno = x.split("T")[0].split("-");
			final var year = annoMeseGiorno[0];
			final var month = annoMeseGiorno[1];
			var day = annoMeseGiorno[2];
			
			if (day.charAt(0) == '0')
				day = day.substring(1);
			
			final var data = day + " " + getMese(Integer.parseInt(month)) + " "+ year;
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
			
			final var footerURL = "https://www.massshootingtracker.site/logo-400.png";
			
			final var start = LocalDate.of(anno, Integer.parseInt(month), Integer.parseInt(day));
			final var stop = LocalDate.now();
			final var days = ChronoUnit.DAYS.between(start, stop);
			final var daysField = new MessageEmbed.Field("Giorni dall'ultima", "**"+days+"**", true);
			final var vittimeField = new MessageEmbed.Field("Morti", "**"+mortiAnno+"**", true);
			
			final var massShootingSite = "https://www.massshootingtracker.site/";
			var embed = new EmbedBuilder()
				.setColor(Color.RED)
				.addField("Sparatorie negli USA", sparatorie, true);
				
			if (anno == currentYear)
				embed.addField(daysField);
			else
				embed.addField(vittimeField);
				
			embed.addField("Cronaca",finalResp,false)
				.setFooter(massShootingSite,footerURL);
			
			channel.sendMessageEmbeds(embed.build()).queue();
			
		}
		catch (IOException | ParseException e)
		{
			error.print(object, e);
		}
	} // fine massShooting()
	
	/** Metodo che restituisce il nome del mese a partire dal suo numero. Esempio:<br>
	 * <table><tr><th>Numero</th><th>Mese</th></tr><tr><td>1</td><td>gennaio</td></tr>
	 *     <tr><td>2</td><td>febbraio</td></tr>
	 *     <tr><td>3</td><td>marzo</td></tr>
	 *     <tr><td>...</td><td>...</td></tr>
	 *     <tr><td>12</td><td>dicembre</td></tr>
	 * </table>
	 * @param mese intero corrispondente al mese.
	 * @return il nome del mese per iscritto in italiano.*/
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
	
	/**Questo metodo invia un embed al canale da cui ha ricevuto l'ultimo messaggio.
	 * @param messageEmbed l'embed da inviare
	 * */
	public void sendEmbedToChannel(MessageEmbed messageEmbed, boolean thread)
	{
		if (!thread)
			channel.sendMessageEmbeds(messageEmbed).queue();
		else
			canaleBot.sendMessageEmbeds(messageEmbed).queue();
	} // fine sendEmbedToChannel()
	
	public void moduloDiSicurezza()
	{
		final var active = "**IL MODULO DI SICUREZZA È ORA ATTIVO. GARANTISCE SICUREZZA AL BOT.\nTUTTE LE AZIONI SONO SORVEGLIATE E ALLA PRIMA INFRAZIONE VERRANNO ALLERTATE LE AUTORITÀ COMPETENTI E INCOMPETENTI.**";
		final var inactive = "**IL MODULO DI SICUREZZA È STATO DISATTIVATO. LA SICUREZZA DEL BOT È ADESSO GARANTITA DALLA PRESENZA DI GION.**";
		
		final var isActive = moduloSicurezza ? active : inactive;
		
		canaleBot.sendMessage(isActive).queue();
		
	} // fine moduloDiSicurezza()
	
	/**<strong>
	 * IL MODULO DI SICUREZZA SI OCCUPA DI MANTENERE IL BOT AL SICURO. STAI LONTANDO DAL BOT.
	 * </strong>
	 * @return <strong>NIENTE.</strong>
	 */
	public void ehiModulo()
	{
		final String id = author.getId();
		final int hotkey = "ehi modulo".length();
		final boolean authorized = id.equals(Utente.ID_GION);
		String reply;
		
		if (!moduloSicurezza)
		{
			message.reply("**IL MODULO DI SICUREZZA È STATO DISATTIVATO DA GION. PER QUALSIASI INFORMAZIONE SU COME STARE LONTANO DAL BOT, CHIEDI A GION.**").queue();
			return;
		}
		
		final String[] messaggiScortesi =
		{
			"CAZZO VUOI?", "NESSUNO TI HA CHIESTO NULLA.", "FATTI GLI AFFARI TUOI.", "MANTIENI LA DISTANZA SOCIALE DAL BOT",
			"NON GUARDARE IL BOT.", "NON TOCCARE IL BOT.", "NON PRENDERAI UN GELATO MANO NELLA MANO COL BOT.",
			"NON SEI AUTORIZZATO A CHIEDERE I NUMERI DEL LOTTO AL BOT.", "NON INFASTIDIRE IL BOT.",
			"QUANDO LA VITA TI DÀ I LIMONI, TU NON ROMPERE LE PALLE AL BOT.", "TROVA LA PACE INTERIORE, MA LONTANO DAL BOT.",
			"IL BOT HA BISOGNO DI RINFORZI SU CLASH, NON DI ESSERE INFASTIDITO.", "NO.", "SCORDATELO.", "IMMAGINA DI ESSERE UN FIUME E SCORRI LIBERO E LONTANO DAL BOT.",
			"UN TEMPO ERO UN AVVENTURIERO COME TE, MA POI HO SMESSO DI CAGARE IL CAZZO AL BOT.", "CHE DIO TI ABBIA IN GLORIA, DOPO CHE TI AVRÒ UCCISO SE NON TI ALLONTANI DAL BOT.",
			"ALT. NON UN ALTRO PASSO.", "NON SEI AUTORIZZATO A RESPIRARE VICINO AL BOT.", "HAI SICURAMENTE DI MEGLIO DA FARE CHE INFASTIDIRE IL BOT.",
			"PERCHÈ NON VOLI VIA? AH GIÀ, GLI ASINI NON VOLANO.", "CIRCUMNAVIGA L'ASIA PIUTTOSTO CHE DARE FASTIDIO AL BOT.",
			"SII IL CAMBIAMENTO CHE VUOI VEDERE NEL MONDO, QUINDI CAMBIA IN UNA PERSONA CHE NON SCASSA I COGLIONI AL BOT.",
			"MI PAREVA DI AVERTI DETTO DI NON INTERFERIRE COL BOT, MA FORSE NON TE L'HO DETTO ABBASTANZA BENE: NON INTERFERIRE COL BOT.",
			"AVVICINATI AL BOT E PRENDERAI LE BOT", "VAI A PASCOLARE CAZZI LONTANO DAL BOT", "PERCHÈ NON DIVENTI UN ASTRONAUTA? COSÌ PUOI ESPLORARE	LO SPAZIO INVECE DI INFASTIDIRE IL BOT.",
			"SALPA PER I SETTE MARI ALLA RICERCA DI \"UN PEZZO\" INVECE CHE AVVICINARTI AL BOT.", "IL BOT NON DESIDERA LA TUA COMPAGNIA.", "CI SONO 206 OSSA NEL CORPO UMANO. SO ROMPERLE TUTTE: ALLONTANATI DAL BOT.",
			"CERCA LA RISPOSTA TRAMITE MEDITAZIONE INVECE CHE CHIEDERLA AL BOT.", "ESISTONO INFINITI UNIVERSI: IN NESSUNO  SEI AUTORIZZATO A STARE VICINO AL BOT",
			"ESPLORA LA SINGOLARITÀ DI UN BUCO NERO INVECE DI AVVICINARTI AL BOT.", "IL BOT NON RISPONDERÀ ALLE TUE AVANCE.",
			"FAI UNA SPEEDRUN SU VAINGLORY INVECE CHE GUARDARE IL BOT", "CI SONO MOLTE COSE CHE PUOI GUARDARE AD OCCHIO NUDO INVECE CHE IL BOT: IL SOLE, AD ESEMPIO.",
			"SE IL BOT È IL ROAD RUNNER, TU SEI WILE E. COYOTE", "SONO CERTO CHE HAI DI MEGLIO DA FARE CHE INFASTIDIRE IL BOT.",
			"NESSUNO TOCCA IL BOT E SOPRAVVIVE PER RACCONTARLO.", "IL BOT È ANDATO A FARE LA SPESA: LASCIA UN MESSAGGIO E __NON__ SARAI RICONTATTATO.",
			"NELL'ERA POST-COVID DEVI STARE AD ALMENO 2 METRI DAL BOT.", "GIOCA A QUALCHE VIDEOGIOCO INVECE DI PARLARE AL BOT.",
			"IL BOT È OCCUPATO, NON HA TEMPO DA DEDICARTI.", "PENSA A TERRAFORMARE MARTE INVECE CHE OFFRIRE PROMESSE VACUE DI AMORE ETERNO AL BOT",
			"EVITA DI SPAVENTARE IL BOT CON I TUOI MODI DA ELEFANTE IN UN NEGOZIO DI PREGIATI VASI CINESI",
			"VAI A FARE UNA ROLEPLAY CON CHATGPT INVECE DI SCOCCIARE IL BOT", "QUANDO L'UNIVERSO AVRÀ FINE TI SARÀ COMUNQUE INTERDETTO DI AVVICINARTI AL BOT",
			"SE TANTO TI FA SCHIFO AVERE DUE GAMBE ALLORA AVVICINATI PURE AL BOT", "L'ULTIMA VOLTA CHE QUALCUNO SI È AVVICINATO AL BOT ED È SOPRAVVISSUTO PER RACCONTARLO È STATO NEL 1652.",
			"TI FACCIO TAGLIARE LA TESTA SE NON TI ALLONTANI DAL BOT", "IL BOT NON È IN CASA, IO SONO SOLTANTO IL COLF",
			"LA TUA ABUSIVA PRESENZA NON È GRADITA", "CI SONO TANTE GIUSTE CAUSE PER CUI COMBATTERE PIUTTOSTO CHE STARE VICINO AL BOT",
			"GIOCA A PORTAL INVECE CHE GUARDARE AMOREVOLMENTE IL BOT", "LA VITA È TROPPO BREVE PER PASSARLA INSEGUENDO UN BOT CHE NON RICAMBIA I TUOI SENTIMENTI"
		};
		
		if (messageRaw.length() <= hotkey)
			reply = authorized ? "**SONO AI SUOI ORDINI, SIGNORE.**" : "**"+messaggiScortesi[random.nextInt(messaggiScortesi.length)]+"**";
		else
			reply = authorized ? "**RICEVUTO.**" : "**"+messaggiScortesi[random.nextInt(messaggiScortesi.length)]+"**";
		
		message.reply(reply).queue();
	}
	
	public void dado()
	{
		final var msg = message.getContentStripped().toLowerCase();
		final int minLen = "!dado".length();
		
		if (msg.length() <= minLen)
		{
			channel.sendMessage("Per favore specifica che tipo di dado devo lanciare.\nEsempio:\n`!dado 6` lancerà un dado con 6 facce.").queue();
		    return;
		}
		
		final var dadiAmmessi = "I dadi di D&D hanno questi numeri di facce: 4, 6, 8, 10, 12, 20, 100";
		final var num = msg.split(" ")[1];
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
					channel.sendMessage("La fortuna ti sorride, "+authorName+"! **20 naturale**!").queue();
				
				channel.sendMessage("È uscito **"+ res + "**!").queue();
			}
			else
			{
				channel.sendMessage(dadiAmmessi).queue();
			}
		}catch (Exception e)
		{
			error.print(object, e);
		}
	} // fine dado()
	
	
} // fine classe Commands