import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.CloseCode;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class Commands extends ListenerAdapter
{
	private static final Object object = Commands.class;
	private static final Errore<Exception> error = new Errore<>();
	private static final Errore<String> errorString = new Errore<>();
	public static final String botChannel = "\uD83E\uDD16bot-owo";
	private static final Random random = new Random();
	public static GuildMessageChannel channel;
	private static long messageID;
	public static Message message;
	public static User author;
	public static TextChannel canaleBotPokemon;
	public static TextChannel canaleBot;
	private static PrivateMessage gion;
	
	private final int currentYear = new GregorianCalendar().get(Calendar.YEAR);
	private final boolean moduloSicurezza = false;
	private final ArrayList<ThreadReminder> remindersList = new ArrayList<>();
	private final MessageTask disconnectMessageTask;
	
	private User user;
	public String authorName;
	private String authorID;
	public String messageRaw;
	private JDA jda;
	
	public Commands()
	{
		final Timer timer = new Timer(true);
		final long period = 24 * 60 * 60 * 1000; // 24 ore in millisecondi
		final DailyTask dailyTask = new DailyTask();
		final ChangeActivityTask activityTask = new ChangeActivityTask();
		disconnectMessageTask = new MessageTask();
		
		timer.schedule(disconnectMessageTask, calcDelay(22,0,0), period);
		timer.schedule(dailyTask, calcDelay(21, 30, 0), period);
		timer.schedule(activityTask, calcDelay(10, 0, 0), period*2); // ogni 48 ore
	}
	
	/**Calcola il ritardo iniziale fino al prossimo orario desiderato
	 * @return la quantità, in millisecondi, di tempo che deve trascorrere prima di eseguire il task. */
	private long calcDelay(int hour, int minute, int second)
	{
		final int targetHour = (hour < 0 || hour > 24 ? 17 : hour);
		final int targetMinute = (minute < 0 || minute > 59 ? 0 : minute);
		final int targetSecond = (second < 0 || second > 59 ? 0 : second);
		
		final ZonedDateTime now, nextRun;
		final String romeString = "Europe/Rome";
		final ZoneId aMaggica = ZoneId.of(romeString);
		
		now = ZonedDateTime.now(aMaggica);
		nextRun = now.getHour() < targetHour ?
			now.withHour(targetHour).withMinute(targetMinute).withSecond(targetSecond) :
			now.withHour(targetHour).withMinute(targetMinute).withSecond(targetSecond).plusDays(1);
		
		return ChronoUnit.MILLIS.between(now, nextRun);
	}
	
	
	/** onReady() viene eseguita soltanto all'avvio del bot */
	public void onReady(@NotNull ReadyEvent event)
	{
		jda = Main.getJda();
		
		try
		{
			canaleBot = jda.getTextChannelsByName(botChannel, true).getFirst();
			canaleBotPokemon = jda.getTextChannelsByName("pokémowon", true).getFirst();
			gion = new PrivateMessage(Utente.getGion());
			
			gion.send("Riavvio completato.");
		}
		catch (Exception e)
		{
			error.print(object, e);
		}
		
		// moduloDiSicurezza();
		
	} // fine onReady()
	
	@Override
	public void onSessionDisconnect(@NotNull SessionDisconnectEvent event)
	{
		final CloseCode closeCode;
		final String closingMessage;
		final RegisteredEvent registeredEvent;
		
		if ((closeCode = event.getCloseCode()) != null)
		{
			closingMessage = String.format("%s\n",closeCode.getMeaning());
			
			registeredEvent = new RegisteredEvent(closingMessage, LocalDateTime.now());
			disconnectMessageTask.addEvent(registeredEvent);
		}
		
	}
	
	/** Questo metodo decide cosa fare quando un messaggio viene modificato */
	public void onMessageUpdate(@NotNull MessageUpdateEvent event)
	{
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
		
		if (authorID.equals(Utente.ID_BOWOT))
			return; // ignora i tuoi stessi messaggi
		
		identifyLatestMessage(event, null);
		
		if (event.isFromGuild())
		{
			guildMessage(event, author.isBot());
		}
		else
		{
			try
			{
				privateMessage(author.isBot());
			} catch (IOException | InterruptedException e)
			{
				error.print(object,e);
			}
		}
	
		if (random.nextInt(100) == 42)
		{
			jda.getPresence().setActivity(Main.selectActivity());
		}
		
	} // fine onMessageReceived()
	
	private void guildMessage(MessageReceivedEvent event, boolean isBot)
	{
		aggiungiReazioni();
		checkForKeywords(message.getContentStripped().toLowerCase());
	} // fine guildEvent()
	
	
	/**Gestisce i messaggi privati che il bot riceve. Se è un altro bot a inviarli, li ignora.
	 * @param isBot <code>true</code> se l'autore del messaggio è un bot a sua volta, <code>false</code> altrimenti. */
	public void privateMessage(boolean isBot) throws IOException, InterruptedException
	{
		if (isBot || authorID.equals(Utente.ID_GION))
			return;
		
		final List<Message.Attachment> attachments = message.getAttachments();
		final String toSend = String.format("%s ha scritto: \"%s\"", authorName, messageRaw);
		
		if (attachments.isEmpty())
			gion.send(toSend);
		else
			gion.send(toSend, attachments.getFirst());
		
		if (moduloSicurezza && author.getId().equals(Utente.ID_ENIGMO))
		{
			final PrivateMessage enigmo = new PrivateMessage(Utente.getEnigmo());
			enigmo.send(Emotes.readyToSend(Emotes.ragey));
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
			channel = received.getGuildChannel();
		}
		else // updated
		{
			messageID = updated.getMessageIdLong();
			author = updated.getAuthor();
			authorName = author.getName();
			message = updated.getMessage();
			messageRaw = message.getContentRaw();
			channel = updated.getGuildChannel();
		}
		
	} // fine identifyLatestMessage()
	
	/**Questo metodo aggiunge a un messaggio la stessa reazione che piazza l'utente*/
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
	{
		defineAuthor(event.getUser());
		
		// ignora le tue stesse reazioni
		if (author.getId().equals(Utente.ID_BOWOT)) { return; }
		
		try
		{
			final Emoji emote = event.getReaction().getEmoji();
			channel = event.getGuildChannel();
			messageID = event.getMessageIdLong();
			message = channel.getHistoryAround(messageID,1).complete().getMessageById(messageID);
			
			if (emote.getType().equals(Emoji.Type.UNICODE))
			{
				message.addReaction(Emoji.fromUnicode(emote.getAsReactionCode())).queue();
			}
			else
			{
				react(emote.getFormatted());
			}
		} catch (Exception e) { error.print(object, e); }
	} // fine onMessageReactionAdd
	
	/**Inserisce come reazioni tutte le emote che trova nel messaggio*/
	private void aggiungiReazioni()
	{
		if (message != null)
		{
			final List<CustomEmoji> emoteList = message.getMentions().getCustomEmojis();
			
			for (CustomEmoji e : emoteList)
			{
				try
				{
					message.addReaction(jda.getEmojis().stream().filter(
					em -> em.getName().contains(e.getName())
					).findFirst().orElseThrow()).queue();
				} catch (Exception ignored) {}
			}
		}
		
	} // fine aggiungiReazioni()
	
	private void defineAuthor(User user)
	{
		if (this.user == null) return;
		
		author = user;
		authorName = author.getName();
		authorID = author.getId();
	}
	
	public void checkForKeywords(String msgStrippedLowerCase)
	{
		boolean reply = false;
		final StringBuilder msgReply = new StringBuilder();
		
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
					final String[] msgSplittato = msgStrippedLowerCase.split(" ");
					final int size = msgSplittato.length;
					String auth = "";
					int numGiorni = 0;
					final List<Message> channelHistory = Utilities.channelHistory(channel,false,3);
					
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
							errorString.print(object, "<@" + Utente.ID_GION + ">\n`auth è una stringa vuota.`");
							return;
						}
						
						final int years = (numGiorni / 365);
						anniversario(auth, years);
					}
				} // fine if daily streak
			} // fine if equals bot
			
			return;
		} // fine if isBot
		
		//if (!msgStrippedLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
		//	encounter();
		
		if (random.nextInt(50) == 42) // chance di reagire con emote personali
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
				case Utente.ID_LEX -> channel.addReactionById(authorID, Emoji.fromUnicode("🇷🇴")).queue();
				case Utente.ID_GION -> react("boo2");
				
			} // fine switch
			
			final String[] reazione = {"dansgame", "pigeon", "smh"};
			final int scelta = random.nextInt(reazione.length);
			
			message.reply(Utilities.camelCase(messageRaw)).queue(lambda -> react(reazione[scelta]));
			
			
		} // fine if reazioni
		
		if (random.nextInt(100) == 42)
		{
			canaleBot.sendMessage(Emotes.readyToSend(Emotes.pogey)).queue();
		}
		
		// arraylist per contenere le reazioni da aggiungere al messaggio
		final ArrayList<String> reazioni = new ArrayList<>();
		
		if (msgStrippedLowerCase.contains("ehi modulo"))
			ehiModulo();
		
		final String pigeonRegex = ".*piccion[ei].*|.*pigeon.*|.*igm[oa].*";
		final String enigmoRegex = ".*igm[oa].*";
		final String gionRegex = ".*gion.*|.*john.*";
		
		
		if (msgStrippedLowerCase.matches("abcde"))
		{
			final ArrayList<CustomEmoji> y = new ArrayList<>(jda.getEmojis());
			final StringBuilder sb = new StringBuilder();
			for (CustomEmoji e : y)
				sb.append(e).append("\n");
			
			for (String s : Utilities.divideString(sb.toString()))
				message.reply(s).queue();
		}
		
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
		
		if (contains(msgStrippedLowerCase, new String[]{"ape", "api", "apecar"}))
			reazioni.add("🐝");
		
		if (msgStrippedLowerCase.contains("cl__z"))
		{
			reply = true;
			msgReply.append("Sempre sia lodato\n");
		}
		
		if (msgStrippedLowerCase.contains("scarab"))
			reazioni.add("scarab");
		
		if (contains(msgStrippedLowerCase, new String[]{"ingredibile", "andonio gonde"}))
			reazioni.add(Emotes.ingredibile);
		
		if (contains(msgStrippedLowerCase, new String[]{"wtf", "what the fuck", "ma che cazzo", "ma cosa"}))
			reazioni.add(Emotes.wtf);
		
		if (msgStrippedLowerCase.matches(".*(?:guid|sto.*guidando|monkasteer)"))
			reazioni.add(Emotes.monkaSTEER);
		
		if (msgStrippedLowerCase.contains("boris"))
			reazioni.add(Emotes.borisK);

		if (msgStrippedLowerCase.contains("òbito") && msgStrippedLowerCase.contains("india"))
		{
			reazioni.add("òbito");
			reazioni.add(DefaultEmoji.INDIA_BANDIERA.toString());
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
		
		if (msgStrippedLowerCase.contains("random") || msgStrippedLowerCase.contains("numero casuale") || msgStrippedLowerCase.contains("a caso"))
		{
			reply = true;
			final int n = random.nextInt(0, 100);
			msgReply.append(String.format("Numero casuale: **%d**.", n));
		}
		
		if (msgStrippedLowerCase.equalsIgnoreCase("cancella questo messaggio"))
		{
			if (random.nextInt(50) == 42)
			{
				message.reply("No.").queue(l -> react("getrekt"));
			}
			else
			{
				final int max = 6;
				final String[] msgs = new String[max];
				msgs[max-1] = "💣 **BOOM** 💥";
				for (int i = 0; i < max-1; i++)
				{
					final int n = Math.abs(i-(max-1));
					final char c = (n == 1 ? 'o' : 'i');
					msgs[i] = String.format("Ricevuto. Le cariche di C4 sono state piantate su questo messaggio.\nDetonazione fra **%d** second%c.", n, c);
				}
				
				final Message toDelete = channel.retrieveMessageById(message.getId()).complete();
				if (toDelete == null){gion.send(":("); return;}
				toDelete.reply(msgs[0]).queue(l ->
				{
					for (int i = 1; i < msgs.length; i++)
					{
						try { Thread.sleep(1000); } catch (InterruptedException e) { error.print(object, e); }
						l.editMessage(msgs[i]).queue();
					}
					try { Thread.sleep(1500); } catch (InterruptedException e) { error.print(object, e); }
					l.delete().queue();
					toDelete.delete().queue();
				});
			}
		}
			
		if (msgStrippedLowerCase.contains("non vedo l'ora") || msgStrippedLowerCase.contains("che ore sono") || msgStrippedLowerCase.contains("che ora è"))
		{
			reply = true;
			final GregorianCalendar date = Utilities.getLocalizedCalendar();
			final int hour = date.get(Calendar.HOUR_OF_DAY);
			final int minutes = date.get(Calendar.MINUTE);
			
			msgReply.append(switch (hour)
			{
				case 0 -> "È ";
				case 1 -> "È l' ";
				default -> "Sono le ";
			});
			
			if (random.nextInt(2) == 0)
			{
				msgReply.append(hour).append(":");
				if (minutes < 10)
					msgReply.append("0").append(minutes).append("\n");
				else
					msgReply.append(minutes).append("\n");
			}
			else
			{
				final Ore orario = new Ore(hour, minutes);
				
				msgReply
					.append(orario.getOra())
					.append(switch (minutes)
					{
						case 0 -> "";
						case 1 -> " e uno";
						default -> " e " + orario.getMinuti();
					}
				);
			}
		}
		
		final String[] saluti = {"ciao", "buondì", "saluti", "buongiorno", "buon pomeriggio", "buonasera", "salve"};
		boolean flag = true;
		for (final String s : saluti)
		{
			if (flag && msgStrippedLowerCase.contains(s))
			{
				flag = false;
				
				final String toReply = String.format("%s %s", Utilities.getSaluto(), random.nextInt(1000) == 1 ? " anche a te" : " un cazzo. ".concat(Emotes.readyToSend(Emotes.ragey)));
				message.reply(toReply).queue();
			}
		}
		
		if (msgStrippedLowerCase.matches("dammi il (?:cinque|5)") || msgStrippedLowerCase.matches("high five"))
		{
			reply = true;
			msgReply.append("🤚🏻\n");
		}
		
		if (msgStrippedLowerCase.contains("grazie") && random.nextInt(50) == 42)
		{
			reply = true;
			msgReply.append("Prego.\n");
		}
		
		if (msgStrippedLowerCase.matches("egg *dog"))
			message.reply(GIF.eggdog.getUrl()).queue();
		
		if (msgStrippedLowerCase.contains("spy") && random.nextInt(3) == 0)
			message.reply(GIF.spyHang.getUrl()).queue();
		
		if (msgStrippedLowerCase.matches("you(?:'re| are) ugly"))
			message.reply(GIF.engineer.getUrl()).queue();
		
		if (msgStrippedLowerCase.contains("deez nuts") && authorID.equals(Utente.ID_ENIGMO))
		{
			reply = true;
			msgReply.append("🥜\n");
			reazioni.add(DefaultEmoji.ARACHIDI.getEmoji());
		}
		
		if (msgStrippedLowerCase.contains("serve aiuto"))
		{
			reply = true;
			msgReply.append("Nemico assente!\n");
		}
		
//		if (msgStrippedLowerCase.contains("") && random.nextInt(42) == 0){}
		
		if (reply)
			message.reply(msgReply).queue();
		else if (random.nextInt(1000000) == 42)
			message.reply("***ULTIMO AVVISO: __NON FARLO MAI PIÙ.__***").queue();
		
	} // fine checkForKeywords()
	
	private EmbedBuilder pigeons()
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
		
		final String img = urls[random.nextInt(urls.length)];
		final EmbedBuilder embed = new EmbedBuilder()
			.setColor(new Color(42,42,42))
			.setImage(img)
		;
		
		return embed;
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
	
	/**Sostituisce i link di twitter con quelli di <code>fxtwitter</code>, che caricano l'anteprima su discord*/
	private void detectTwitterLink()
	{
		final String[] parts = messageRaw.split(" ");
		final String firstHalf, secondHalf;
		String newURL = "";
		boolean twitterDetected = false;
		
		for (String m : parts)
		{
			// due regex anziché uno solo perché il numero di caratteri da manipolare cambia
			final String regex1 = "https*://twitter\\.com.*";
			final String regex2 = "https*://www\\.twitter\\.com.*";
			
			if (m.matches(regex1))
			{
				
				twitterDetected = true;
				
				firstHalf = m.split("//")[0].concat("//");
				secondHalf = m.split("//")[1];
				newURL = String.format("%sfx%s", firstHalf, secondHalf);
				
				break;
			}
			else if (m.matches(regex2))
			{
				twitterDetected = true;
				
				firstHalf = m.split("//")[0].concat("//");
				secondHalf = m.split("//")[1].substring(4).concat("fx");
				newURL = String.format("%sfx%s", firstHalf, secondHalf);
				
				break;
			}
		}
		
		if (twitterDetected && !newURL.isBlank())
			message.reply(newURL).queue();
		
	} // fine detectTwitterLink()
	
	private boolean contains(String source, String[] subItem)
	{
		for (String s : subItem)
		{
			final String pattern = "\\b" + s + "\\b";;
			final Pattern p = Pattern.compile(pattern);
			final Matcher m = p.matcher(source);
			
			if (m.find())
				return true;
		}
		return false;
	} // fine metodo contains()
	
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
	{
		final String eventName = event.getName().toLowerCase();
		final OptionMapping option;
		
		switch (eventName)
		{
			case "dado" ->
			{
				int facce = 6;
				option = event.getOption("facce");
				if (option != null)
				{
					try
					{
						facce = Integer.parseInt(option.getAsString());
					}catch (Exception e)
					{
						error.print(object, e);
					}
				}
				final int res = random.nextInt(1,facce+1);
				final StringBuilder sb = new StringBuilder();
				sb.append(String.format("🎲 È uscito **%d**!", res));
				if (res == facce)
					sb.append(String.format("\nWow, %d naturale! 🎉", res));
				else if (res == 1)
					sb.append("\nSventura, 1 critico... ⚠️");
				
				final String message = String.format("Sto lanciando il dado con %d facce... 🎲", facce);
				
				event.reply(message).queue(m -> {
					try {
						Thread.sleep(random.nextInt(1000, 2000));
					} catch (InterruptedException e) {
						error.print(object,e);
					}
					m.editOriginal(sb.toString()).queue();
				});
			}
			case "cena" ->
			{
				final User author = event.getUser();
				final String replyMessage;
				final boolean isEphemeral;
				
				try
				{
					option = event.getOption("utente");
					if (option != null)
					{
						try
						{
							final User user = option.getAsUser();
							final String userName = user.getEffectiveName();
							final String authorName = author.getEffectiveName();
							
							if (user.isBot())
							{
								isEphemeral = true;
								replyMessage = user.getId().equals(Utente.ID_BOWOT) ?
									String.format("No, non uscirò a cena con te, %s.", authorName) :
									String.format("Spiacente, %s non può uscire a cena con te.", userName)
								;
							}
							else
							{
								isEphemeral = false;
								replyMessage = String.format("%s ti ha ufficialmente invitato a cena, %s! Accetti?", authorName, user.getAsTag());
							}
							event.reply(replyMessage).setEphemeral(isEphemeral).queue();
						}
						catch (Exception e)
						{
							error.print(object, e);
						}
					}
					else
					{
						event.reply("Devi menzionare qualcuno da invitare a cena!").setEphemeral(true).queue();
					}
				} catch (Exception e)
				{
					event.reply("Impossibile creare un invito a cena. Tutti i ristoranti e i pub sono chiusi.").setEphemeral(true).queue();
				}
			}
			case "pokemon" ->
			{
				String pkmnName = null;
				Pokemon p = null;
				final boolean shiny;
				if (!event.getOptions().isEmpty())
				{
					shiny = event.getOption("shiny") != null && Objects.requireNonNull(event.getOption("shiny")).getAsBoolean();
					option = event.getOption("nome");
					if (option != null)
					{
						pkmnName = option.getAsString();
						if (Pokemon.getId(pkmnName) != -1)
						{
							p = new Pokemon(Pokemon.getId(pkmnName), true);
							p.setShiny(shiny);
						}
					}
				}
				if (p != null)
				{
					event.replyEmbeds(p.spawn().build()).queue();
				}
				else
					event.reply("Il pokedex non ha informazioni riguardo " + pkmnName).setEphemeral(true).queue();
				
			}
			case "poll" ->
			{
				final List<OptionMapping> options = event.getOptions();
				final String domanda;
				final String[] rs = new String[options.size()-1];
				Arrays.fill(rs,null);
				
				domanda = Objects.requireNonNull(event.getOption("domanda")).getAsString();
				rs[0] = Objects.requireNonNull(event.getOption("opzione1")).getAsString();
				rs[1] = Objects.requireNonNull(event.getOption("opzione2")).getAsString();
				
				if (options.size() > 3)
				{
					for (int i = 2; i < options.size(); i++)
					{
						final OptionMapping x = event.getOption("opzione"+(i+1));
						
						if (x != null)
						{
							rs[i] = x.getAsString();
						}
					}
				}
				
				final EmbedBuilder embedBuilder = creaSondaggio(domanda,rs);
				final String[] reactionLetters =
				{
					"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB",
					"\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
					"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7",
					"\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
					"\uD83C\uDDFE", "\uD83C\uDDFF"
				}; // array di lettere emoji A -> Z
				event.replyEmbeds(embedBuilder.build()).queue(l ->
				{
					l.retrieveOriginal().queue(originalMessage ->
					{
						for (int i = 0; i < rs.length; i++)
						{
							originalMessage.addReaction(Emoji.fromUnicode(reactionLetters[i])).queue();
						}
					});
				});
			}
			case "f" ->
			{
				option = event.getOption("utente");
				final String avatar = (option != null ? option.getAsUser().getAvatarUrl() : null);
				final User u = event.getUser();
				
				if (option != null && option.getAsUser().getName().equals(u.getName()))
				{
					final String reply = String.format("Omaggi te stesso? %s", Emotes.readyToSend(Emotes.smh));
					event.reply(reply).setEphemeral(true).queue();
				}
				
				final EmbedBuilder embedBuilder = new EmbedBuilder();
				final String[] cuori = {"❤️", "💛", "💙", "🧡", "💚", "💜"};
				final String title = option == null ? "F" : String.format("In loving memory of %s %s",option.getAsUser().getName(), cuori[random.nextInt(cuori.length)]);
				final String[] imgs =
				{
					"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.imgflip.com%2F4rf5nr.jpg&f=1&nofb=1&ipt=0a6b54aa3965c4ec92081a03fdb37f8d1d490426003b6cbeb6ec2420619515dd&ipo=images",
					"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.kym-cdn.com%2Fentries%2Ficons%2Ffacebook%2F000%2F017%2F039%2Fpressf.jpg&f=1&nofb=1&ipt=0a56a685ea4605c86c4d6caea860a6c1480a6e88a982538acc34623ac5204bdc&ipo=images",
					"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--1H4GzubW--%2Fb_rgb%3A908d91%2Ct_Heather%2520Preview%2Fc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1496153439%2Fproduction%2Fdesigns%2F1634415_1.jpg&f=1&nofb=1&ipt=bb9133ef4feef513b2605c621fed68f5232116b5d0fdf22a1def833954f7121a&ipo=images",
					"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.imgflip.com%2F3ni7oz.jpg&f=1&nofb=1&ipt=2cf84114527ff5e7b02fb19ae74fe596b1d66baba35daa7eb7c8862adcdfe9af&ipo=images",
				};
				final String imgDaUsare = imgs[random.nextInt(imgs.length)];
				final String footer = String.format("%s pays his respects.", u.getName());
				
				embedBuilder
					.setTitle(title)
					.setColor(Color.black)
					.setImage(imgDaUsare)
					.setThumbnail(avatar)
					.setFooter(footer, u.getAvatarUrl())
				;
				event.replyEmbeds(embedBuilder.build()).queue(l -> {
					l.retrieveOriginal().queue(original -> {
						try {
							Thread.sleep(300);
							original.addReaction(Emoji.fromFormatted(Emotes.readyToSend(Emotes.o7))).queue();
						} catch (Exception ignored) {}
					});
				});
			}
//			case "history" ->
//			{
//				final int MAX = 10;
//				final List<Message> history, enigmosHistory, gionsHistory;
//
//				history = canaleBot.getHistory()
//					.retrievePast(MAX)
//				.complete();
//
//				final String romeString = "Europe/Rome";
//				final ZoneId romeZone = ZoneId.of(romeString);
//				final LocalDate oggi = LocalDate.now(romeZone);
//
//				enigmosHistory = history
//					.stream().filter(message ->
//						message.getAuthor().getId().equals(Utente.ID_ENIGMO) &&
//						message.getTimeCreated().atZoneSameInstant(romeZone).toLocalDate().equals(oggi) &&
//						message.getContentRaw().strip().toLowerCase().contains("owo daily")
//					)
//				.toList();
//
//
//				gionsHistory = history
//					.stream()
//					.filter(message ->
//						message.getAuthor().getId().equals(Utente.ID_GION) &&
//						message.getTimeCreated().atZoneSameInstant(romeZone).toLocalDate().equals(oggi) &&
//						message.getContentRaw().strip().toLowerCase().contains("owo daily")
//					)
//				.toList();
//
//
//				final StringBuilder sb = new StringBuilder();
//				final String pattern = "{\n%s: \"%s\"\n%s\n}\n\n";
//				sb.append("TUTTA LA STORIA:\n");
//				for (Message m : history)
//				{
//					sb.append(String.format(pattern, m.getAuthor().getName(), m.getContentRaw(), m.getTimeCreated()));
//				}
//
//				sb.append("\nI MESSAGGI DEL SIGNOR ENIGMO:\n");
//				for (Message m : enigmosHistory)
//				{
//					sb.append(String.format(pattern, m.getAuthor().getName(), m.getContentRaw(), m.getTimeCreated()));
//				}
//
//				sb.append("\nI MESSAGGI DI GION:\n");
//				for (Message m : gionsHistory)
//				{
//					sb.append(String.format(pattern, m.getAuthor().getName(), m.getContentRaw(), m.getTimeCreated()));
//				}
//				sb.append("\nFINE DELLA STORIA.\n\n");
//
//				event.reply("Check your dms my friend").setEphemeral(true).queue();
//
//				List<String> chunks = new ArrayList<>();
//				StringBuilder sbCopy = new StringBuilder(sb); // Copia per sicurezza, si sa mai
//				final int maxChunkSize = 2000;
//
//				while (!sbCopy.isEmpty())
//				{
//					if (sbCopy.length() > maxChunkSize)
//					{
//						chunks.add(sbCopy.substring(0, maxChunkSize));
//						sbCopy.delete(0, maxChunkSize);
//					} else
//					{
//						chunks.add(sbCopy.toString());
//						sbCopy.setLength(0);
//					}
//				}
//
//				for (String s : chunks)
//					gion.send(s);
//
//			}
			case "coinflip" ->
			{
				final int minDelay = 1000, maxDelay = 2500;
				
				event.deferReply().queue();
				final Timer timer = new Timer();
				final TimerTask task = new TimerTask()
				{
					@Override
					public void run()
					{
						event.getHook().editOriginal(String.format("È uscito %s!", coinflip())).queue();
					}
				};
				
				timer.schedule(task, random.nextInt(minDelay, maxDelay));
			}
			case "promemoria" ->
			{
				final int MAX_REMINDERS = 5;
				final int[] times = new int[MAX_REMINDERS];
				final String nome;
				
				remindersList.removeIf(r -> !r.isActive());
				
				nome = Objects.requireNonNull(event.getOption("nome")).getAsString();
				
				for (int i = 1; i < 4; i++)
					times[i-1] = event.getOptions().get(i).getAsInt();
				
				final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
				if (remindersList.size() < MAX_REMINDERS)
				{
					int time = 0;
					final ZonedDateTime future;
					final EmbedBuilder impostato, scaduto;
					final String createdSuccess, endedSuccess, rickroll = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
					final String img = "https://thumbs.dreamstime.com/b/reminder-icon-vector-illustration-simple-vector-icon-reminder-icon-vector-illustration-175544158.jpg";
					final User author = event.getUser();
					
					time += times[0] * 24 * 60 * 60 * 1000; // giorni -> ms
					time += times[1] * 60 * 60 * 1000; // ore -> ms
					time += times[2] * 60 * 1000; // minuti -> ms
					
					future = ZonedDateTime.now(ZoneId.of("Europe/Rome")).plusSeconds(time/1000);
					
					createdSuccess = String.format("Il tuo promemoria, \"%s\", è impostato per il giorno `%s`\n", nome.trim(), future.format(formatter));
					endedSuccess = String.format("Il promemoria \"%s\" è scaduto!", nome);
					
					try
					{
						impostato = new EmbedBuilder();
						impostato.setTitle("Promemoria impostato!");
						impostato.setDescription(createdSuccess);
						impostato.setColor(Color.RED);
						impostato.setThumbnail(img);
						impostato.setAuthor(String.format("Impostato da %s", author.getName()), rickroll, author.getAvatarUrl());
						
						scaduto = new EmbedBuilder();
						scaduto.setTitle("Promemoria scaduto!");
						scaduto.setDescription(endedSuccess);
						scaduto.setColor(Color.GRAY);
						scaduto.setThumbnail(img);
						scaduto.setAuthor(String.format("Impostato da %s", author.getName()), rickroll, author.getAvatarUrl());
						
						GuildMessageChannelUnion canale = event.getGuildChannel();
						if (canale.getType().equals(ChannelType.PRIVATE))
						{
							canale = (GuildMessageChannelUnion) event.getMessageChannel();
						}
						
						final ThreadReminder reminder = new ThreadReminder( time, canale, scaduto);
						remindersList.add(reminder);
						reminder.start();
						
						event.replyEmbeds(impostato.build()).queue();
					}
					catch (Exception e)
					{
						new Errore<String>().print(this,e.getMessage());
					}
				}
				else
				{
					final EmbedBuilder impostato = new EmbedBuilder();
					impostato.setTitle("Promemoria attuali");
					for (ThreadReminder r : remindersList)
					{
						final String name = r.getName();
						final LocalDateTime scad = r.getEnd();
						final String desc = scad.format(formatter);
						final MessageEmbed.Field rem = new MessageEmbed.Field(name,desc,true);
						
						impostato.addField(rem);
					}
					impostato.setColor(Color.RED);
					
					final String strunz = String.format("Strunz, hai già impostato %d promemoria.", MAX_REMINDERS);
					
					event.reply(strunz).queue(l->
					{
						l.retrieveOriginal().complete().replyEmbeds(impostato.build()).queue();
					});
				}
				
				
			}
			case "mass_shooting" ->
			{
				option = event.getOption("anno");
				int anno = new GregorianCalendar().get(GregorianCalendar.YEAR);
				if (option != null)
				{
					anno = option.getAsInt();
				}
				
				final String avatar = "https://www.massshootingtracker.site/logo-400.png";
				final String massShootingSite = "https://www.massshootingtracker.site/";
				
				final EmbedBuilder massShootingReport = massShooting(anno);
				if (massShootingReport == null)
				{
					final EmbedBuilder emptyReport = new EmbedBuilder();
					final String msg = String.format("Nessuna sparatoria di massa trovata per l'anno %d.", anno);
					
					emptyReport.setTitle(msg);
					emptyReport.setColor(Color.RED);
					emptyReport.setFooter(massShootingSite, avatar);
					emptyReport.setAuthor("Mass Shooting Tracker", massShootingSite);
					
					event.replyEmbeds(emptyReport.build()).queue();
				}
				else
				{
					event.replyEmbeds(massShootingReport.build()).queue();
				}
				
			}
			case "8ball" ->
			{
				final OptionMapping optionDomanda = event.getOption("domanda");
				final OptionMapping optionHidden = event.getOption("segreto");
				final boolean secret = optionHidden != null && optionHidden.getAsBoolean();
				if (optionDomanda == null) { return; }
				final String domandaString = optionDomanda.getAsString().strip();
				
				final String domanda = secret ? "•".repeat(domandaString.length()) : domandaString;
				final String response = eightBall();
				
				final EmbedBuilder embed = new EmbedBuilder()
					.setTitle(domanda)
					.setColor(Color.RED)
					.setDescription("La magica palla 8 🎱 sta valutando la tua domanda...")
					.setFooter("")
				;
				
				event.replyEmbeds(embed.build()).queue(l ->
				{
					try {
						Thread.sleep(random.nextInt(1000,2000));
					} catch (InterruptedException e) {error.print(object, e);}
					
					embed.setColor(Color.GRAY);
					embed.setDescription(String.format("La magica palla 8 🎱 risponde:\n`%s`.", response));
					final String refund = "Insoddisfatto? Usa `/refund` per un rimborso!";
					l.editOriginalEmbeds(embed.build()).queue(ll -> ll.editMessage(refund).queue());
				});
			}
			case "carta" ->
			{
				final Card c = new Card();
				event.replyEmbeds(c.getEmbed().build()).queue();
			}
			case "pigeons" ->
			{
				event.replyEmbeds(pigeons().build()).queue();
			}
			case "test" ->
			{
				 event.reply(handsOnHips()).queue();
			}
			case "calcolatrice" ->
			{
				final int uno, due;
				final double result;
				final StringBuilder reply = new StringBuilder();
				final String operazione, res;
				final boolean error;
				final DecimalFormat df = new DecimalFormat("#.##");
				final boolean negativo;
				final char[] c = new char[]{'(',')'};
				
				uno = event.getOption("primo").getAsInt();
				due = event.getOption("secondo").getAsInt();
				operazione = event.getOption("operatore").getAsString();
				negativo = due < 0;
				error = (operazione.equals("/") || operazione.equals("%")) && due == 0;
				
				result = switch (operazione)
				{
					case "+" -> uno + (long) due;
					case "-" -> uno - (long) due;
					case "*" -> uno * (long) due;
					case "/" -> due != 0 ? uno / (double) due : random.nextDouble() * 100;
					case "%" -> due != 0 ? uno % (double) due : random.nextDouble() * 100;
					case "^" -> Math.pow(uno, due);
					default -> 0;
				};
				res = String.format("**%d %s %s%d%s = %s**", uno, operazione, negativo ? c[0] : "", due, negativo ? c[1] : "", df.format(result));
				reply.append(res.concat(error ? String.format("\nNo, aspetta. **%d %s %d** non fa **%s**... Hai messo il divisore uguale a zero! Così facendo hai distrutto la struttura fondamentale dello spazio-tempo, grazie tante. %s", uno, operazione, due, df.format(result), Emotes.readyToSend(Emotes.ragey)) : ""));
				
				if (!error && (random.nextInt(4) == 0))
				{
					final String stringURL = String.format("http://www.numbersapi.com/%d?json", (int)result);
					final JsonObject j = Utilities.httpRequest(stringURL).getAsJsonObject();
					
					final boolean found = j.get("found").getAsBoolean();
					if (!found) break;
					final String text = j.get("text").getAsString();
					reply.append(String.format("\n-# %s", text));
				}

				event.reply(reply.toString()).queue();
			}
			case "barzelletta" ->
			{
				final EmbedBuilder embed = new EmbedBuilder();
				final String url = "https://official-joke-api.appspot.com/random_joke";
				final String setup, punchline;
				final JsonObject j = Utilities.httpRequest(url).getAsJsonObject();
				final String refund = "-# If you didn't like the joke, use the command `/refund` to get your money back!";
				
				setup = j.get("setup").getAsString();
				punchline = j.get("punchline").getAsString();
				
				embed.setColor(Color.RED);
				embed.setTitle(setup);
				embed.setDescription("");
				
				event.replyEmbeds(embed.build()).queue(l ->
				{
					try
					{
						Thread.sleep(random.nextInt(2000,3000));
					}catch (InterruptedException e) { error.print(object,e); }
					
					embed.setDescription(punchline);
					
					l.editOriginalEmbeds(embed.build()).queue( ll ->
					{
						try
						{
							Thread.sleep(random.nextInt(2000,4000));
						}catch (InterruptedException e) { error.print(object,e); }
						embed.setColor(Color.GRAY);
						ll.editMessageEmbeds(embed.build()).queue();
						
						ll.addReaction(Emoji.fromUnicode("👍🏻")).queue();
						ll.addReaction(Emoji.fromUnicode("👎🏻")).queue();
						ll.editMessage(refund).queue();
					});
				});
			}
			case "quiz" ->
			{
				final ThreadQuiz quiz = new ThreadQuiz(event);
				quiz.start();
			}
			case "trivia" ->
			{
				final String fileName = "./src/main/java/nations.json";
				final File f = new File(fileName);
				final EmbedBuilder embed = new EmbedBuilder();
				option = event.getOption("nazione");
				final String opt = option == null ? "" : option.getAsString();
				try
				{
					final String nations = new String(Files.readAllBytes(f.toPath()));
					final JsonArray allNationsArray = JsonParser.parseString(nations).getAsJsonArray();
					final JsonObject country;
					final JsonObject currency;
					final String commonName, officialName, cca3, footer, landlocked, image, population;
					final JsonArray continents;
					final Set<String> keys;
					String nomeMoneta="", simboloMoneta="";
					
					country = opt.isEmpty() ?
						allNationsArray.get(random.nextInt(allNationsArray.size())).getAsJsonObject()
						:
						StreamSupport.stream(allNationsArray.spliterator(), false)
							.map(JsonElement::getAsJsonObject)
							.filter(tempCountry -> tempCountry.get("name").getAsJsonObject().get("common").getAsString().equalsIgnoreCase(opt))
							.findFirst()
							.orElse(allNationsArray.get(random.nextInt(allNationsArray.size())).getAsJsonObject())
					;
					
					commonName = country.get("name").getAsJsonObject().get("common").getAsString();
					officialName = country.get("name").getAsJsonObject().get("official").getAsString();
					cca3 = country.get("cca3").getAsString();
					continents = country.get("continents").getAsJsonArray();
					footer = String.format("%s %s (%s)", country.get("flag").getAsString(), commonName, cca3);
					landlocked = country.get("landlocked").getAsString().equals("true") ? "✓" : "✕";
					image = country.get("coatOfArms") != null ? country.get("coatOfArms").getAsJsonObject().get("svg").getAsString() : "";
					currency = country.get("currencies").getAsJsonObject();
					population = String.format("%,d", country.get("population").getAsInt()).replace(",",".");
					keys = currency.keySet();
					
					for (String key : keys)
					{
						final JsonObject obj = currency.get(key).getAsJsonObject();
						nomeMoneta = obj.get("name").getAsString();
						simboloMoneta = obj.get("symbol").getAsString();
						break;
					}
					
					final String descr = commonName.equals(officialName) ?
						String.format("La moneta usata in %s si chiama %s (%s)", commonName, nomeMoneta, simboloMoneta) :
						String.format("Il nome completo di %s è \"%s\"", commonName, officialName);
					
					embed.setTitle(commonName);
					embed.setDescription(descr);
					embed.setColor(Color.RED);
					embed.setThumbnail(country.get("flags").getAsJsonObject().get("png").getAsString());
					embed.setImage(image);
					embed.addField("Capitale",country.get("capital").getAsJsonArray().get(0).getAsString(),true);
					embed.addField("Popolazione",population,true);
					embed.addField("Landlocked",landlocked,true);
					embed.setFooter(footer);
					
					event.replyEmbeds(embed.build()).queue();
					
				} catch (Exception e)
				{
					error.print(object,e);
					final String errormsg = "Si è verificato un errore con il comando. Per favore attendi un minuto prima di riprovare.";
					event.reply(errormsg).queue();
				}
				
			}
			
		}
	} // fine onSlashCommand()
	
	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
	{
		final String eventName = event.getName();
		final AutoCompleteQuery focused = event.getFocusedOption();
		final String NAMES_FILE;
		List<Command.Choice> options = null;
		
		if (eventName.equalsIgnoreCase("pokemon") && focused.getName().equalsIgnoreCase("nome"))
		{
			NAMES_FILE = "./src/main/java/nomiPokemon.txt";
			options = getChoichesFromFile(NAMES_FILE,focused);
		}
		else if (eventName.equals("trivia") && focused.getName().equalsIgnoreCase("nazione"))
		{
			NAMES_FILE = "./src/main/java/nationsNames.txt";
			options = getChoichesFromFile(NAMES_FILE, focused);
		}
		
		if (options != null)
			event.replyChoices(options).queue();
	}
	
	private List<Command.Choice> getChoichesFromFile(String fileName, AutoCompleteQuery query)
	{
		final File f = new File(fileName);
		try (Stream<String> lines = Files.lines(f.toPath()))
		{
			return lines.filter(word -> word.toLowerCase().contains(query.getValue().toLowerCase()))
				.limit(25)
				.map(word -> new Command.Choice(word, word))
				.toList();
		} catch (Exception e) { new Errore<Exception>().print(object,e); }
		return null;
	}
	
	/** Lancia una moneta */
	public String coinflip()
	{
		final boolean headsOrTails = random.nextBoolean();
		final String heads = Emotes.readyToSend(Emotes.pigeon);
		final String tails = Emotes.readyToSend(Emotes.boo2);
		
		return headsOrTails ? heads : tails;
	} // fine coinflip()
	
	public EmbedBuilder creaSondaggio(String domanda, String[] risposte)
	{
		final EmbedBuilder embedBuilder = new EmbedBuilder();
		
		final int lenghtRisposte = risposte.length;
		final String[] reactionLetters =
		{
			"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB",
			"\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
			"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7",
			"\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
			"\uD83C\uDDFE", "\uD83C\uDDFF"
		}; // array di lettere emoji [A] -> [Z]
		
		final StringBuilder descrizione = new StringBuilder();
		embedBuilder.setTitle(domanda.concat(domanda.contains("?") ? "" : "?"));
		
		for (int i = 0; i < lenghtRisposte; i++)
		{
			descrizione.append(String.format("%s\t%s\n", reactionLetters[i], risposte[i].trim()));
		}
		
		embedBuilder.setDescription(descrizione);
		embedBuilder.setColor(0xFF0000);
		
		return embedBuilder;
	} // fine sondaggio()
	
	
	/** Aggiunge una reazione all'ultimo messaggio inviato */
	public static void react(String emote)
	{
		if (emote == null || emote.isBlank())
			return;
		
		final String emoteDaUsare = Emotes.emoteDaUsare(emote.toLowerCase());
		
		if (emoteDaUsare == null || emoteDaUsare.isBlank())
			return;
		
		try
		{
			switch (emoteDaUsare)
			{
				case "obito" ->
				{
					for (String str : Emotes.obito)
						channel.addReactionById(messageID, Emoji.fromFormatted(str)).queue();
				}
				case "sabaping" ->
				{
					for (String str : Emotes.sabaPing)
						channel.addReactionById(messageID, Emoji.fromFormatted(str)).queue();
				}
				case "hitman", "uomo colpo" ->
				{
					for (int i : Emotes.hitman)
						channel.addReactionById(messageID, Emoji.fromFormatted(Emotes.letters[i])).queue();
				}
				case "xcom", "icscom" ->
				{
					for (int i : Emotes.XCOM)
						channel.addReactionById(messageID, Emoji.fromFormatted(Emotes.letters[i])).queue();
				}
				case "scarab" ->
				{
					for (String str : Emotes.scarab)
						channel.addReactionById(messageID, Emoji.fromFormatted(str)).queue();
				}
				default -> channel.addReactionById(messageID, Emoji.fromFormatted(emoteDaUsare)).queue();
			} // fine switch
		} // fine try
		catch (ErrorResponseException e)
		{
			error.print(object, e);
		}
	} // fine react()
	
	
	private String handsOnHips()
	{
		return "https://i.imgflip.com/4zap4m.jpg";
	}
	
	/** Genera un responso usando la magica palla 8 */
	public String eightBall()
	{
		final String[] risposte =
		{
			"Sì",
			"Certamente",
			"Assolutamente sì",
			"Senza dubbio",
			"Puoi contarci",
			"Probabile",
			"Molto probabile",
			"Il responso è positivo",
			"I segni presagiscono di sì",
			"Il presagio non è né positivo né negativo",
			"Non ci contare",
			"Improbabile",
			"Molto improbabile",
			"La mia risposta è no",
			"Le mie fonti dicono di no",
			"Il responso non è favorevole",
			"Ci sono molti dubbi al riguardo",
			"Gli astri non ti sorridono",
			"No"
		};
		return risposte[random.nextInt(risposte.length)];
	} // fine eightBall()
	
	
	/**Ottieni un resoconto della sparatoria più recente in USA oppure ottieni un resoconto di una sparatoria scelta casualmente nell'anno da te specificato.*/
	private EmbedBuilder massShooting(int anno)
	{
		final JsonArray jsonArray;
		int mortiAnno = 0;
		final String avatar = "https://www.massshootingtracker.site/logo-400.png";
		final String massShootingSite = "https://www.massshootingtracker.site/";
		final EmbedBuilder embed = new EmbedBuilder();
		
		try
		{
			final String link = String.format("https://mass-shooting-tracker-data.s3.us-east-2.amazonaws.com/%d-data.json", anno);
			jsonArray = Utilities.httpRequest(link).getAsJsonArray();
			
			final ArrayList<JsonObject> objs = new ArrayList<>();
			
			for (JsonElement o : jsonArray)
			{
				objs.add(o.getAsJsonObject());
				mortiAnno += Integer.parseInt((o.getAsJsonObject()).get("killed").getAsString());
			}
			
			final int scelta = (anno != currentYear ? random.nextInt(objs.size()) : 0);
			
			if (objs.isEmpty())
				return null;
			
			final String citta = objs.get(scelta).get("city").getAsString();
			final String stato = objs.get(scelta).get("state").getAsString();
			final String morti = objs.get(scelta).get("killed").getAsString();
			final String feriti = objs.get(scelta).get("wounded").getAsString();
			final String x = (objs).get(scelta).get("date").getAsString(); // es.: 2022-01-05T12:23:34
			final String[] annoMeseGiorno = x.split("T")[0].split("-");
			final String year = annoMeseGiorno[0];
			final String month = annoMeseGiorno[1];
			final String day = (annoMeseGiorno[2].charAt(0) == '0' ? annoMeseGiorno[2].substring(1) : annoMeseGiorno[2]);
			
			final String data = String.format("%s %s %s", day, Utilities.getMese(Integer.parseInt(month)), year);
			final String sparatorie = String.format("Nel %s ammontano a **%d**.", anno, jsonArray.size());
			final String recente = String.format("La più recente è avvenuta il %s in **%s, %s.**\n",data, citta, stato);
			final String caso = String.format("Una si è verificata il %s in **%s, %s.**\n", data, citta, stato);
			final String personeMorte = String.format("Sono morte **%s** persone.\n", morti);
			final String personaMorta = "È morta **1** persona.\n";
			final String noVittime = "Per fortuna non ci sono state vittime.\n";
			final String personeFerite = String.format("I feriti ammontano a **%s**.\n", feriti);
			final String totaleMorti = String.format("In totale sono morte **%s** persone durante l'anno.\n", mortiAnno);
			
			final String finalResp = String.format("%s %s %s %s",
				anno == currentYear ? recente : caso,
				switch (Integer.parseInt(morti))
				{
					case 0 -> noVittime;
					case 1 -> personaMorta;
					default -> personeMorte;
				},
				personeFerite,
				anno == currentYear ? totaleMorti : ""
			);
			
			final LocalDate start = LocalDate.of(anno, Integer.parseInt(month), Integer.parseInt(day));
			final LocalDate stop = LocalDate.now();
			final long days = ChronoUnit.DAYS.between(start, stop);
			final MessageEmbed.Field daysField = new MessageEmbed.Field("Giorni dall'ultima", "**"+days+"**", true);
			final MessageEmbed.Field vittimeField = new MessageEmbed.Field("Morti", "**"+mortiAnno+"**", true);
			
			embed.addField("Sparatorie negli USA", sparatorie, true);
			
			if (anno == currentYear)
				embed.addField(daysField);
			else
				embed.addField(vittimeField);
				
			embed.addField("Cronaca",finalResp,false)
				.setFooter(massShootingSite,avatar)
				.setAuthor("Mass Shooting Tracker", massShootingSite)
				.setColor(Color.RED)
			;
		}
		catch (Exception e)
		{
			error.print(object, e);
		}
		return embed;
	} // fine massShooting()
	
	/**
	 * <strong>
	 * IL MODULO DI SICUREZZA SI OCCUPA DI MANTENERE IL BOT AL SICURO. STAI LONTANDO DAL BOT.
	 * </strong>
	 *
	 * @return <strong>NIENTE.</strong>
	 * @throws ENIGMO DALLA FINESTRA.
	 * @apiNote QUELLE CHE FANNO BZZZ E IMPOLLINANO I FIORI.
	 * @see <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ">QUELLO CHE È SUCCESSO A CHI HA MOLESTATO IL BOT PRIMA DI TE.</a>
	 * @since QUELLA VOLTA IN CUI ENIGMO HA MOSSO DELLE AVANCE AL BOT.
	 */
	public void moduloDiSicurezza() throws ENIGMO
	{
		final String active = "**IL MODULO DI SICUREZZA È ORA ATTIVO. GARANTISCE SICUREZZA AL BOT.\nTUTTE LE AZIONI SONO SORVEGLIATE E ALLA PRIMA INFRAZIONE VERRANNO ALLERTATE LE AUTORITÀ COMPETENTI E INCOMPETENTI.**";
		final String inactive = "**IL MODULO DI SICUREZZA È STATO DISATTIVATO. LA SICUREZZA DEL BOT È ADESSO GARANTITA DALLA PRESENZA DI GION.**";
		
		canaleBot.sendMessage(moduloSicurezza ? active : inactive).queue();
		
	} // fine moduloDiSicurezza()
	
	
	public void ehiModulo()
	{
		final String id = author.getId();
		final int hotkey = "ehi modulo".length();
		final boolean authorized = id.equals(Utente.ID_GION);
		final String reply;
		
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
		
		final String agliOrdini;
		final String ricevuto;
		final String scortese = messaggiScortesi[random.nextInt(messaggiScortesi.length)];
		
		if (messageRaw.length() <= hotkey)
		{
			agliOrdini = "SONO AI SUOI ORDINI, SIGNORE.";
			reply = String.format("**%s**", authorized ? agliOrdini : scortese);
		}
		else
		{
			ricevuto = "RICEVUTO.";
			reply = String.format("**%s**", authorized ? ricevuto : scortese);
		}
		
		message.reply(reply).queue();
	}
	
} // fine classe Commands