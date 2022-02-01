import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
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
import java.net.URL;
import java.text.DateFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	private static final File valori = new File("valori.txt");
	private static final File nomiPkmn = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	private static MessageChannel messageChannel;
	private static final String[] listaComandi = {"!coinflip", "!poll", "!info", "!8ball", "!pokemon"};
	// private static final String[] listaParole = {"pigeon", "owo", "pog", "òbito", "vergogna", "no"};
	private static final String[] listaDescrizioni =
	{
		"Il bot lancerà una moneta",
		"Permette di creare sondaggi",
		"Visualizza le informazioni. Proprio quelle che stai leggendo!",
		"Chiedi un responso all'Entità Superiore: la magica palla 8.",
		"Acchiappali tutti!"
	};
	private static int messaggiInviati = 0;
	private static int limite;
	private static String authorName;
	private static final String[] nomeUtenti = {"Òbito#2804", "Enigmo#7166", "Alex#2241", "Gion#0935", "OwO#8456"};
	private static final ArrayList<String> utenti = new ArrayList<>();
	private static long id;
	private static final Locale locale = Locale.ITALIAN;
	private static Message message;
	private static String messageRaw;
	private static User author;


	public void onReady(@NotNull ReadyEvent event)
	{
		String nome = event.getJDA().getSelfUser().getName();
		Activity act = Objects.requireNonNull(event.getJDA().getPresence().getActivity());
		Collections.addAll(utenti, nomeUtenti);
		
		System.out.printf("%s si è connesso a Discord!\n\n", nome);
		System.out.print("public class MessageHistory\n{\n");
		
		TextChannel textChannel = event.getJDA().getTextChannelsByName("\uD83E\uDD16bot-owo", true).get(0);

		String activity = act.getType().toString();
		String nomeActivity = act.getName();

		textChannel.sendMessage(activity+"/"+nomeActivity).queue();
	}

	public void onMessageReceived(MessageReceivedEvent event)
	{
		id = event.getMessageIdLong();
		String guild = event.getGuild().toString().split("\\(")[0].split(":")[1];

		author = event.getAuthor();
		authorName = author.getName();
		message = event.getMessage();
		messageRaw = message.getContentRaw();
		messageChannel = event.getChannel();
		
		final String mockupCode = "\tString %s = \"%s\"; // in \"%s\" (%s) - %s";
		var date = new Date();
		var dFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		var dataFormattata = dFormat.format(date);

		String messageChannelString = "#"+messageChannel.toString().split(":")[1].split("\\(")[0];
		String[] args = messageRaw.split(" ");
		String comando = args[0];
		String msgLowerCase = messageRaw.toLowerCase(Locale.ROOT);

		List<Emote> emoteList = message.getEmotes();

		System.out.printf(mockupCode, authorName, messageRaw, messageChannelString, guild, dataFormattata);
		System.out.print("\n}\r");

		
		if (author.isBot())
		{
			if (author.getDiscriminator().equals("8456"))
			{
				react("owo");
				react("vergogna");
			}
			// return a priori, se però il messaggio lo manda l'altro bot OwO prima gli mette le reazioni e poi return
			
			if (author.getDiscriminator().equals("5269"))
				if (random.nextInt(1000) == 42) // 0,1%
					message.reply("BOwOt vergognati").queue();
			
			return;
		}

		for (Emote emote : emoteList)
			message.addReaction(emote).queue();

		if (!msgLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
			spawnPokemon();
		
		//	if (msgLowerCase.contains("!testenigmo"))
			//	triggeraEnigmo();

		if (random.nextInt(20) == 9) // 5% chance di reagire con emote personali
		{
			String discriminator = author.getDiscriminator();
			var trigger = random.nextInt(2); // 0 - 1
			
			if (trigger == 0)
				triggera(discriminator);
			else
			{
				switch(discriminator)
				{
					case "2804" -> // Òbito
					{
						react("obito");
						react("vergogna");
						message.reply("Òbito vergognati").queue();
					}
					case "7166" -> // Enigmo
						react("pigeon");
					
					case "2241" ->  // Alex
						messageChannel.addReactionById(id, "🇷🇴").queue();
					
					case "0935" -> // Gion
						react("smh");
					
				} // fine switch
				
			} // fine else
			
		} // fine if reazioni

		
		switch (comando)
		{
			case "!coinflip" -> coinflip(event);
			case "!poll" -> poll(event);
			case "!info" -> info();
			case "!8ball" -> eightBall(event);
			case "!pokemon" -> pokemon();
		}
		
		
		if (msgLowerCase.contains("pigeon"))
			react("pigeon");
		
		if (msgLowerCase.contains("owo"))
			react("owo");
		
		if (msgLowerCase.contains("pog"))
			react("pog");
		
		if (msgLowerCase.contains("òbito") || msgLowerCase.contains("obito"))
			if (random.nextInt(50) == 42) // 2%
			{
				react("obito");
				react("vergogna");
			}
		
		if (msgLowerCase.contains("vergogna"))
			react("vergogna");
		
		if (msgLowerCase.contains("no u") || msgLowerCase.contains("nou"))
			react("nou");
		
		if (msgLowerCase.contains("sabaping"))
			react("sabaping");
		
		if (msgLowerCase.contains("get"))
			if (msgLowerCase.contains("rekt"))
				react("getrekt");

		if (msgLowerCase.contains("smh"))
			react("smh");

		if (msgLowerCase.contains("giorno"))
			react("giorno");

		if (msgLowerCase.contains("uomo colpo") || msgLowerCase.contains("hitman") || msgLowerCase.contains("icscom") || msgLowerCase.contains("xcom"))
		{
			react("pog");
			
			if (msgLowerCase.contains("icscom") || msgLowerCase.contains("xcom"))
				react("xcom");
			
			if (msgLowerCase.contains("uomo colpo") || msgLowerCase.contains("hitman"))
				react("hitman");
		}
		
		
	} // fine onMessageReceived()



	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{


	} // fine onSlashCommand()

	
	public void coinflip(MessageReceivedEvent event)
	{
		final String testa = "<:pogey:733659301645910038>";
		final String croce = "<:pigeon:647556750962065418>";
		String autore = authorName+" lancia una moneta...";
		
		if (random.nextInt(2) == 1) // testa
			messageChannel.sendMessage(autore+"\n**È uscito** " + testa + "**! (Testa)**").queue(message -> message.addReaction("pogey:733659301645910038").queue());
		else
			messageChannel.sendMessage(autore+"\n**È uscito** " + croce + "**! (Croce)**").queue(message -> message.addReaction("pigeon:647556750962065418").queue());
		
	} // fine coinflip()
	
	public void poll(MessageReceivedEvent event)
	{
		// args[0] = "!poll"
		// args[1] = domanda
		// args[2, 3, ...] = risposte
		String msg = message.getContentRaw();
		
		if (msg.length() <= 5)
		{
			sondaggio("Pog?", new String[]{"Pog sì", "Pog no", "Porgo Tensing"}, true);
			//flag = true fa comparire il messaggio di utilizzo del comando !poll
			return;
		}
		
		String[] domandaERisposte = event.getMessage().getContentRaw().split("\\?");
		String domanda = domandaERisposte[0].substring("!poll".length());
		String[] risposte = msg.substring("!poll".length()+domanda.length()+1).split("/");
		
		//System.out.printf("DomandaERisposte length: %d\nDomandaERisposte: %s\nDomanda length: %d\nDomanda: %s\nRisposte.length: %d\nRisposte: %s\n", domandaERisposte.length, Arrays.toString(domandaERisposte), domanda.length(), domanda, risposte.length, Arrays.toString(risposte));
		
		sondaggio(domanda, risposte, false);
	} // fine poll()
	
	public void sondaggio(String domanda, String[] risposte, boolean flag)
	{
		risposte[0] = risposte[0].substring(0, risposte[0].length()-1).trim();
		int sleepInterval = random.nextInt(500) + 1000;
		final int size = risposte.length;
		EmbedBuilder embedBuilder = new EmbedBuilder();
		final String[] letters =
		{
			"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB",
			"\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
			"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7",
			"\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
			"\uD83C\uDDFE", "\uD83C\uDDFF"
		}; // array di lettere emoji A -> Z
		
		if (size < 2 || size > 20 || flag)
		{
			//embedBuilder.setFooter("");
			embedBuilder.setTitle("`!poll` - Istruzioni per l'uso");
			embedBuilder.addField("Sondaggio", "Per creare un sondaggio devi usare il comando `!poll` + `domanda?` + `[risposte]`\nSepara le risposte con uno slash `/`.", false);
			embedBuilder.addField("Esempio", "`!poll domanda? opzione 1 / opzione 2 / opzione 3 ...`\n`!poll Cosa preferite? Pizza / Pollo / Panino / Sushi`", false);
			embedBuilder.addField("Votazione", "Per votare, usa le reazioni!", false);
			embedBuilder.setColor(0xFFFFFF);
			
			messageChannel.sendTyping().queue();
			try { Thread.sleep(sleepInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else
		{
			String descrizione = "";
			//embedBuilder.setFooter(author + " ha posto la domanda");
			embedBuilder.setTitle(domanda+"?");
			for (int i = 0; i < risposte.length; i++)
				descrizione = descrizione.concat(letters[i] + "\t" + risposte[i]) + "\n";
			embedBuilder.setDescription(descrizione);
			embedBuilder.setColor(0xFF0000);
			
			messageChannel.sendTyping().queue();
			
			try { Thread.sleep(sleepInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue((message) ->
			{
				for (int i = 0; i < size; i++)
					message.addReaction(letters[i]).queue();
			});
			
		}
		
	} // fine sondaggio()

	public void triggera(String discriminator)
	{
		String title, image, footer, color;
		short risultato = (short) random.nextInt(2);

		final String titolo = "Get rekt ";

		final String[] immagineObito =
		{
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fstocks2.com%2Fassets%2Fstocks%2Fgm%2Fgme%2Fgme-daily-rsi.png&f=1&nofb=1",
			"https://scontent.fnap5-1.fna.fbcdn.net/v/t1.6435-9/36686816_812263365633695_5089614023322763264_n.jpg?_nc_cat=102&ccb=1-5&_nc_sid=174925&_nc_ohc=sFL5GEX7jv4AX_KyE9g&_nc_ht=scontent.fnap5-1.fna&oh=00_AT_oGm-J17HN6LGRMIH_NOSDf3Fya36yAs9gtKmHKu-qxg&oe=6219896F"
		};

		final String[] immagineEnigmo =
		{
			"https://ramenparados.com/wp-content/uploads/2021/01/21.png",
			"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.pinimg.com%2F736x%2F2b%2Fa6%2F4b%2F2ba64b426bbbb365252a54e2312118e9.jpg&f=1&nofb=1"
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
		
		messageChannel.sendTyping().queue();
		pause(1000, 0);

		var embedBuilder = new EmbedBuilder();
		
		switch (discriminator)
		{
			case "2804" -> // Òbito
			{
				title = titolo.concat("Òbito");
				image = immagineObito[risultato];
				footer = testoFooter;
				color = ( risultato == 0 ? "0xFFFFFF" : "0xC59FC9");
			}
			
			case "7166" -> // Enigmo
			{
				title = titolo.concat("Enigmo");
				image = immagineEnigmo[risultato];
				footer = testoFooter;
				color = ( risultato == 0 ? "0xCB4D4D" : "0xE5D152");
			}
			
			case "2241" -> // Lex
			{
				title = titolo.concat("Lex");
				image = immagineLex[risultato];
				footer = testoFooter;
				color = ( risultato == 0 ? "0xD80000" : "0x207522");
			}
			
			case "0935" -> // Gion
			{
				title = titolo.concat("Gion");
				image = immagineGion[risultato];
				footer = testoFooter;
				color = ( risultato == 0 ? "0XDDCD4f" : "0xEAE28A");
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

		messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();
	
	} // fine triggera()

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
		final int[] emoteHitman = {7, 8, 19, 12, 0, 13}; // posizioni nell'alfabeto
		final int[] emoteXCOM = {23, 2, 14, 12}; // posizioni nell'alfabeto
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
		
		String emoteDaUsare = switch (emote)
		{
			case "pigeon" -> emotePigeon;
			case "nou" -> emoteNou;
			case "owo" -> emoteOwO;
			case "pog" -> emotePog;
			case "vergogna" -> emoteVergognati[random.nextInt(2)];
			case "getrekt" -> emoteGetRekt;
			case "smh" -> emoteSmh;
			case "giorno" -> emoteGiorno;
			default -> "";
		};

		if (emote.equals("obito"))
			for (String s : emoteObito)
				messageChannel.addReactionById(id, s).queue();
		
		if (emote.equals("sabaping"))
			for (String s : emoteSabaPing)
				messageChannel.addReactionById(id, s).queue();
		
		if (emote.equals("hitman"))
			for (int i : emoteHitman)
				messageChannel.addReactionById(id, letters[i]).queue();
		
		if (emote.equals("xcom"))
			for (int i : emoteXCOM)
				messageChannel.addReactionById(id, letters[i]).queue();
		
		if (!emoteDaUsare.equals(""))
		{
			try
			{
				messageChannel.addReactionById(id, emoteDaUsare).queue();
			}
			catch (Exception e) { System.out.printf("Errore nell'aggiunta della reazione \"%s\"\n\t", emoteDaUsare); }
		}
		
	} // fine react()
	
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
		//embedBuilder.addField();
		embedBuilder.setColor(0xFF0000);
		embedBuilder.addBlankField(false);
		embedBuilder.setFooter("Creato con ❤ da JohnWeak", urlOwO);
		
		MessageEmbed embed = embedBuilder.build();
		messageChannel.sendMessageEmbeds(embed).queue();
		
	} // fine info()
	
	public void eightBall(MessageReceivedEvent event)
	{
		final Message message = event.getMessage();
		final String ball = "🎱 ";
		final String risposta = ball+"says... ";
		final String[] risposte =
		{
			"Yes",
			"It is certain.",
			"It is decidedly so.",
			"Without a doubt.",
			"Yes definitely.",
			"You may rely on it.",
			"As I see it, yes.",
			"Most likely.",
			"Outlook good.",
			"Yes.",
			"Signs point to yes.",
			"Reply hazy, try again.",
			"Ask again later.",
			"Better not tell you now.",
			"Cannot predict now.",
			"Concentrate and ask again.",
			"Don't count on it.",
			"My reply is no.",
			"My sources say no.",
			"Outlook not so good.",
			"Very doubtful."
		};

		messageChannel.sendTyping().queue();

		pause(-1, -1);

		message.reply(risposta).queue(message1 ->
		{

			pause(-1,-1);

			message1.editMessage(risposta+"**"+risposte[random.nextInt(risposte.length)]+"**").queue();
		});
		
		
	} // fine eightBall()

	private void pause(int millis, int bound)
	{
		if (millis < 1)
			millis = 1500;

		if (bound < 1)
			bound = 500;

		try { Thread.sleep(millis+random.nextInt(bound)); }
		catch (InterruptedException e) { e.printStackTrace(); }
	} // fine pause()
	
	public void pokemon()
	{
		String[] msg = messageRaw.split(" ");

		if (messageRaw.contains("!pokemon"))
		{
			String[] tipo = {" ", " "};
			String generazione;
			String numeroPokedex;
			String[] lineaEvolutiva = {"1","2","3"};

			if (msg.length > 1 && !msg[1].isEmpty())
			{
				String nome = msg[1];
				JSONArray jsonArray = search(nome);

				try
				{
					JSONObject jsonObject = (JSONObject) jsonArray.get(0);
					String description = (String) jsonObject.get("description");
					JSONArray types = (JSONArray) jsonObject.get("types");
					JSONObject family = (JSONObject) jsonObject.get("family");
					JSONArray evoLine = (JSONArray) family.get("evolutionLine");

					for (int i = 0; i < types.size(); i++)
					{
						tipo[i] = types.get(i).toString();
					}

					generazione = String.valueOf(jsonObject.get("gen"));
					numeroPokedex = (String) jsonObject.get("number");

					for (int i = 0; i < evoLine.size(); i++)
					{
						lineaEvolutiva[i] = evoLine.get(i).toString();
					}

					var pokemon = new Pokemon(nome, description, false);

					pokemon.setTipo(tipo);
					pokemon.setGenerazione(generazione);
					pokemon.setDexNumber(numeroPokedex);
					pokemon.setLineaEvolutiva(lineaEvolutiva);

					messageChannel.sendTyping().queue();
					pause(1000, 500);
					messageChannel.sendMessageEmbeds(buildEmbed(pokemon, true).build()).queue();
				}
				catch (IndexOutOfBoundsException e) { System.out.printf("Il pokemon cercato (%s) non è presente nell'API\n}", nome); }
			}
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

	private void singleEncounter(Pokemon pokemon)
	{
		EmbedBuilder embedBuilder;
		String[] nomi = {pokemon.getNome(), ""};
		var titolo = "A wild ".concat(pokemon.getNome().concat(" appears!"));
		embedBuilder = buildEmbed(pokemon, false);
		embedBuilder.setTitle(titolo);
		messageChannel.sendTyping().queue();
		pause(500, 500);

		sendMessage(nomi, embedBuilder);

	} // fine singleEncounter


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
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("Accept", "application/json");
					
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder response = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null)
							response.append(inputLine);

					jsonArray = (JSONArray) jsonParser.parse(String.valueOf(response));


				}
		}catch (IOException | ParseException e) { System.out.println("Errore nell'apertura del file: " + nomiPkmn); }

		return jsonArray;
	}
	
	private void doubleEncounter(Pokemon uno, Pokemon due)
	{
		EmbedBuilder embedBuilder;
		String[] titolo = {"Primo Pokemon!", "Secondo Pokemon!"};
		Pokemon[] pokemons = {uno, due};
		var nomi = new String[] { uno.getNome(), due.getNome() };
		messageChannel.sendMessage("Doppio Incontro!").queue();
		
		for (int i = 0; i < 2; i++)
		{
			embedBuilder = buildEmbed(pokemons[i], false);
			embedBuilder.setDescription(titolo[i]);
			//embedBuilder.setFooter("Catturalo con !catch","https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Images.png");

			sendMessage(nomi, embedBuilder);
		}
		
		System.out.printf("\nUno: %s, shiny: %s\nDue: %s, shiny: %s\n",uno.getNome(), uno.isShiny(), due.getNome(), due.isShiny());
	} // fine

	private void sendMessage(String[] pokemonNames, EmbedBuilder embedBuilder)
	{
		messageChannel.sendMessageEmbeds(embedBuilder.build()).queue((message ->
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


	private EmbedBuilder buildEmbed(Pokemon pokemon, boolean pokedex)
	{
		EmbedBuilder embedBuilder = new EmbedBuilder();
		String descrizione;
		String[] tipi = pokemon.getTipo();
		StringBuilder stringBuilder = new StringBuilder();
		String types = "";
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
			embedBuilder.setFooter(""+lineaEvolutiva, "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F53%2FPok%25C3%25A9_Ball_icon.svg%2F1026px-Pok%25C3%25A9_Ball_icon.svg.png&f=1&nofb=1");

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

		if (pokemon.isShiny())
		{
			embedBuilder.setColor(Color.YELLOW);
			embedBuilder.setFooter("✨ Shiny! ✨");
		}
		else
		{
			embedBuilder.setColor(Color.RED);
		}


		return embedBuilder;
	} // fine buildEmbed()
	
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
			limite = random.nextInt(10) + 5; // genera un nuovo max per i messaggi
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

		}catch (IOException e) { System.out.println("Errore nella scrittura del file!"); }

		//System.out.println("Valori nel file: " + Arrays.toString(valori));
		//System.out.printf("Valori nelle variabili: [limite: %d, messaggiInviati: %d]\n", limite, messaggiInviati);


	} // fine spawnPokemon
	
	
} // fine classe Commands