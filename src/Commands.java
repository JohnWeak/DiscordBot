import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	private static int msgCount = 0;
	// public static final String prefix = "!";
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
	private static final String[] utenti = {"Òbito#2804", "Enigmo#7166", "Alex#2241", "Gion#0935", "OwO#8456"};
	private static long id;



	public void onReady(@NotNull ReadyEvent event)
	{
		String nome = event.getJDA().getSelfUser().getName();

		System.out.printf("%s si è connesso a Discord!\n\n", nome);
		System.out.print("public class MessageHistory\n{\n");
	}

	public void onMessageReceived(MessageReceivedEvent event)
	{
		id = event.getMessageIdLong();
		String guild = event.getGuild().toString().split("\\(")[0].split(":")[1];
		authorName = event.getAuthor().getName();
		var author = event.getAuthor();
		var message = event.getMessage();
		final String mockupCode = "\tString %s = \"%s\"; // in \"%s\" (%s)";

		messageChannel = event.getChannel();
		String messageChannelString = "#"+messageChannel.toString().split(":")[1].split("\\(")[0];
		String[] args = event.getMessage().getContentRaw().split(" ");
		String comando = args[0];
		String msg = event.getMessage().getContentRaw();
		String msgLowerCase = msg.toLowerCase(Locale.ROOT);

		List<Emote> emoteList = event.getMessage().getEmotes();

		System.out.printf(mockupCode, authorName, msg, messageChannelString, guild);
		System.out.print("\n}\r");


		// dire all'altro bot OwO di vergognarsi
		if (author.getDiscriminator().equals("8456"))
		{
			react("owo");
			react("vergogna");
			return;
		}
		
		if (author.isBot()) return; // Per evitare problemi con altri bot

		for (Emote emote : emoteList)
			message.addReaction(emote).queue();

		if (!msgLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
			spawnPokemon(event);
		


		if (random.nextInt(20) == 9) // 5% chance di reagire con emote personali
		{
			String discriminator = author.getDiscriminator();


			switch(discriminator)
			{
				case "2804" -> // Òbito
				{
					message.reply("Òbito vergognati").queue();
					react("obito");
					react("vergognati");
				}
				case "2241" -> react("romania"); // Alex
				case "0935" -> react("smh"); // Gion
				case "7166" -> react("pigeon"); // Enigmo
			}


		} // fine if reazioni

		
		switch (comando)
		{
			case "!coinflip" -> coinflip(event);
			case "!poll" -> poll(event);
			case "!info" -> info();
			case "!8ball" -> eightBall(event);
			case "!pokemon" -> pokemon(event);
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

		//if (msgLowerCase.contains(""))
		//	react("");
		
		
	} // fine onMessageReceived()

	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		String c = event.getCommandString();

		System.out.println("Comando: " + c);

		if (c.equals("test"))
			messageChannel.sendMessage("test eseguito con successo!").queue();
	
	//FIXME: come diavolo si fa?!
	} // fine onSlashCommand()

	
	public void coinflip(MessageReceivedEvent event)
	{
		final String testa = "<:pogey:733659301645910038>";
		final String croce = "<:pigeon:647556750962065418>";
		String autore = event.getAuthor().getName()+" lancia una moneta...";
		
		if (random.nextInt(2) == 1) // testa
			event.getChannel().sendMessage(autore+"\n**È uscito** " + testa + "**! (Testa)**").queue(message -> message.addReaction("pogey:733659301645910038").queue());
		else
			event.getChannel().sendMessage(autore+"\n**È uscito** " + croce + "**! (Croce)**").queue(message -> message.addReaction("pigeon:647556750962065418").queue());
		
	} // fine coinflip()
	
	public void poll(MessageReceivedEvent event)
	{
		// args[0] = "!poll"
		// args[1] = domanda
		// args[2, 3, ...] = risposte
		String msg = event.getMessage().getContentRaw();
		
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
		final String[] letters = new String[]
		{
			"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9",
			"\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED",
			"\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
			"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5",
			"\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9",
			"\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
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
		final String emoteBandieraRomania = "U+1F1F7 U+1F1F4";


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
			case "romania" -> emoteBandieraRomania;
			default -> "";
		};

		if (emote.equals("obito"))
			for (String s : emoteObito)
				messageChannel.addReactionById(id, s).queue();
		
		if (emote.equals("sabaping"))
			for (String s : emoteSabaPing)
				messageChannel.addReactionById(id, s).queue();
		
		if (!emoteDaUsare.equals(""))
		{
			try
			{
				messageChannel.addReactionById(id, emoteDaUsare).queue();
			}
			catch (Exception ignored) {}
		}
		
	} // fine react()
	
	public void info()
	{
		var embedBuilder = new EmbedBuilder();
		String urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		String urlTitle = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
		
		embedBuilder.setTitle("Informazioni", urlTitle);
		embedBuilder.setDescription("Questo bot permette di lanciare monete, creare sondaggi e, soprattutto, essere un rompiballe.");
		
		for (int i = 0; i < listaComandi.length; i++)
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
		if (millis < 0)
			millis = 1500;

		if (bound < 0)
			bound = 500;

		try { Thread.sleep(millis+random.nextInt(bound)); }
		catch (InterruptedException e) { e.printStackTrace(); }
	} // fine pause()
	
	public void pokemon(MessageReceivedEvent event)
	{
		String[] msg = event.getMessage().getContentRaw().split(" ");

		if (event.getMessage().getContentRaw().contains("!pokemon"))
		{
			String[] tipo = {" ", " "};
			String generazione;
			String numeroPokedex;
			String[] lineaEvolutiva = {"1","2","3"};

			if (msg.length > 1 && !msg[1].isEmpty())
			{
				String nome = msg[1];
				JSONArray jsonArray = search(msg[1]);

				try
				{
					JSONObject jsonObject = (JSONObject) jsonArray.get(0);
					String description = (String) jsonObject.get("description");
					JSONArray types = (JSONArray) jsonObject.get("types");
					JSONObject family = (JSONObject) jsonObject.get("family");
					JSONArray evoLine = (JSONArray) family.get("evolutionLine");

					System.out.println("Evoluzioni: " + evoLine);

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

					Pokemon pokemon = new Pokemon(nome, description, false);

					pokemon.setTipo(tipo);
					pokemon.setGenerazione(generazione);
					pokemon.setDexNumber(numeroPokedex);
					pokemon.setLineaEvolutiva(lineaEvolutiva);

					messageChannel.sendTyping().queue();
					pause(1000, 500);
					messageChannel.sendMessageEmbeds(buildEmbed(pokemon, true).build()).queue();
				}
				catch (IndexOutOfBoundsException e) { System.out.printf("Il pokemon cercato (%s) non è presente nell'API", nome); }
			}
			else if (msg.length == 1)
			{
				Pokemon pokemon = new Pokemon();
				EmbedBuilder embedBuilder;

				if (random.nextInt(10) == 9)
				{
					doubleEncounter(pokemon, new Pokemon());
				}
				else
				{
					embedBuilder = buildEmbed(pokemon, false);
					messageChannel.sendTyping().queue();
					pause(500, 500);
					messageChannel.sendMessageEmbeds(embedBuilder.build()).queue((message ->
					{
						message.addReaction("👍🏻").queue();
						message.addReaction("❤️").queue();
						message.addReaction("👎🏻").queue();
					}));
				}
			}
		}
	} // fine metodo definitivo pokemon()

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
		messageChannel.sendMessage("Doppio Incontro!").queue();
		
		for (int i = 0; i < 2; i++)
		{
			embedBuilder = buildEmbed(pokemons[i], false);
			embedBuilder.setDescription(titolo[i]);
			//embedBuilder.setFooter("Catturalo con !catch","https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Images.png");
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue(message ->
			{
				message.addReaction("👍🏻").queue();
				message.addReaction("❤️").queue();
				message.addReaction("👎🏻").queue();
			});
			
		}
		
		System.out.printf("\nUno: %s, shiny: %s\nDue: %s, shiny: %s\n",uno.getNome(), uno.isShiny(), due.getNome(), due.isShiny());
	} // fine
	
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
		embedBuilder.setTitle(pokemon.getNome());
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
	
	public void spawnPokemon(MessageReceivedEvent event)
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
			pokemon(event); // genera un incontro
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