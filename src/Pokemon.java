import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Pokemon
{
	private static final Object object = Pokemon.class;
	
	private final int max = 898; // fino a gen 8
	private static final File nomiPokemon = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	
	// private static int pokemon_id = 261; -> Poochyena
	// https://pokeapi.co/api/v2/pokemon/261/
	
	private String nome;
	private String img;
	private boolean shiny = false;
	private String descrizione;
	private String[] tipo;
	private String generazione;
	private String dexNumber;
	private String[] lineaEvolutiva;
	private int[] individualValues = new int[6];
	private boolean catturato = false;
	private boolean active;
	
	public Pokemon()
	{
		shiny();
		
		try
		{
			String[] result = generatePokemon(random.nextInt(max)+1);
			nome = result[0];
			img = result[1];
		}
		catch (Exception e) { Error.print(object, e); }
		
	}

	public Pokemon(String nome, String descrizione, boolean shiny)
	{
		int id = 1;
		this.shiny = shiny;
		this.nome = nome;
		
		try
		{
			Scanner scanner = new Scanner(nomiPokemon);
			while (scanner.hasNextLine())
				if (nome.equalsIgnoreCase(scanner.nextLine()))
					break;
				else
					id++;


			img = generatePokemon(id)[1];
		}catch (Exception e) { Error.print(object,e); }

		this.descrizione = descrizione;
	}
	
	
	private String[] generatePokemon(int id)
	{
		if (id <= 0)
			id = random.nextInt(max)+1;
		
		Scanner scanner;
		String[] risultato = new String[2];
		
		generateIVs();
		
		try
		{
			scanner = new Scanner(nomiPokemon);
			for (int i = 0; i < id; i++)
				nome = scanner.nextLine();
			
		} catch (FileNotFoundException e) { Error.print(object,e); }
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+id+".png";
		final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+id+".png";
		
		risultato[0] = nome;

		img = (shiny ? urlShinyImg : urlImg);
		
		risultato[1] = img;
		
		return risultato;
		
	} // fine generatePokemon()
	
	private void shiny()
	{
		if (random.nextInt(8192) == 42)
			shiny = true;
	}
	
	private void generateIVs()
	{
		// [HP, ATK, DEF, SPA, SPD, SPE]
		for (int index : individualValues)
			individualValues[index] = random.nextInt(32); // IVs: 0-31
	}
	
	
	
	/** Cerca un Pokemon nell'API. Se non lo trova mostra un messaggio di errore. */
	public static void pokemon()
	{
		String msgLowercase = Commands.messageRaw.toLowerCase(Locale.ITALIAN);
		String[] msg = msgLowercase.split(" ");
		
		if (msgLowercase.contains("!pokemon"))
		{
			String[] tipo = {" ", " "};
			String generazione, numeroPokedex;
			String[] lineaEvolutiva = {"1", "2", "3"};
			
			if (msg.length > 1 && !msg[1].isEmpty())
			{
				String nome = msg[1];
				JSONArray jsonArray = Pokemon.search(nome);
				
				if (jsonArray.isEmpty())
				{
					Commands.canaleBotPokemon.sendMessage("jsonArray è vuoto").queue();
					return;
				}
				
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
					
					Commands.channel.sendTyping().queue();
					Commands.pause(1000, 500);
					Commands.channel.sendMessageEmbeds(Pokemon.buildEmbed(pokemon, true).build()).queue();
				}
				catch (IndexOutOfBoundsException e)
				{
					Error.print(object, e);
				}
			}
			else
				Commands.channel.sendMessage("Usa `!pokemon <nome> [shiny / s]` per cercare un Pokemon").queue();
			
		}
		else
		{
			var pokemon = new Pokemon();
			Pokemon.singleEncounter(pokemon);
		}
		
	} // fine pokemon()
	
	/** Genera un embed con il Pokemon */
	public static EmbedBuilder buildEmbed(Pokemon pokemon, boolean pokedex)
	{
		var embedBuilder = new EmbedBuilder();
		var descrizione = "";
		var tipi = pokemon.getTipo();
		var stringBuilder = new StringBuilder();
		var types = "";
		var lineaEvo = pokemon.getLineaEvolutiva();
		var lineaEvolutiva = "";
		
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
		
		try
		{
			embedBuilder.setTitle(pokemon.getNome().toUpperCase());
		}catch (Exception ignored) { }
		
		if ((descrizione = pokemon.getDescrizione()) != null)
		{
			String type = "Type";
			if (!tipi[1].equals(" "))
				type += "s";
			
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
	
	
	public static void spawnPokemon()
	{
		var rand = random.nextInt(100);
		if (rand == 42 || Commands.messageRaw.equals("pkmnpls"))
			singleEncounter(new Pokemon());
		
		// Commands.author.openPrivateChannel().flatMap(channel -> channel.sendMessage(""+rand)).queue();
		
	} // fine spawnPokemon()
	
	
	/** Effettua la ricerca del pokemon nell'API.
	 * @param pokemon il nome del pokemon da cercare.
	 * @return un array JSON, con tutte le informazioni del pokemon trovate nell'API.
	 * */
	public static JSONArray search(String pokemon)
	{
		URL url;
		JSONArray jsonArray = new JSONArray();
		JSONParser jsonParser = new JSONParser();
		
		try
		{
			url = new URL("https://pokeapi.glitch.me/v1/pokemon/" + pokemon);
			Scanner scanner = new Scanner(nomiPokemon);
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
		} catch (IOException | ParseException e) { Error.print(object,e); }
		
		return jsonArray;
	} // fine search()
	
	
	/** Genera un incontro con un pokemon selvatico */
	public static void singleEncounter(Pokemon pokemon)
	{
		EmbedBuilder embedBuilder;
		final var titolo = "A wild " + pokemon.getNome() + " appears!";
		
		embedBuilder = Pokemon.buildEmbed(pokemon, false).setTitle(titolo);
		embedBuilder.setFooter("Catturalo con !catch","https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Images.png");
		
		// a questo punto il pokemon è attivo nel canale
		
		try
		{
			var t = new ThreadPokemon(pokemon, Commands.canaleBot);
			t.setEmbedBuilder(embedBuilder);
			t.timeoutTime(t.MINUTES, random.nextInt(5, 60));
			t.start();
		}
		catch (Exception e)
		{
			Error.print(object, e);
		}
		
		
	} // fine singleEncounter
	
	
	/** Genera un doppio incontro con Pokemon selvatici */
	private void doubleEncounter(Pokemon uno, Pokemon due)
	{
		EmbedBuilder embedBuilder;
		String[] titolo = {"Primo Pokemon!", "Secondo Pokemon!"};
		Pokemon[] pokemons = {uno, due};
		var nomi = new String[] { uno.getNome(), due.getNome() };
		Commands.canaleBotPokemon.sendMessage("Doppio Incontro!").queue();
		
		for (int i = 0; i < 2; i++)
		{
			embedBuilder = Pokemon.buildEmbed(pokemons[i], false);
			embedBuilder.setDescription(titolo[i]);
			embedBuilder.setFooter("Catturalo con !cattura","https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Images.png");
		}
	} // fine doubleEncounter()
	
	/***/
	public static void catturaPokemon()
	{
		/*
		final var trainersFile = "trainers.txt";
		var gson = new Gson();
		var map = new HashMap<String, String>();
		*/
	
		
		/* *************************************************
		try
		{
			var file = new File(trainersFile);
			if (file.createNewFile())
				System.out.println("Il file è stato creato!");
			else
				System.out.println("Il file esisteva già.");
			
			var fileReader = new FileReader(trainersFile);
			var buffReader = new BufferedReader(fileReader);
			
			//TODO: leggere dal file
			
			buffReader.close();
		}catch (IOException ignored) {}
		
		map.put("Enigmo", "1");
		var json = gson.toJson(map);
		
		
		// TODO: ottenere una lista di trainer e controllare gli ID, se non è presente creare un nuovo trainer
		//  e fargli catturare il pokemon; altrimenti prendere il trainer esistente e assegnare a lui il pokemon
		*******************************************/
		
	} // fine catturaPokemon()
	
	
	
	
	//GETTER
	public String getNome() { return nome; }
	public String getImg() { return img; }
	public boolean isShiny() { return shiny; }
	public String getDescrizione() { return descrizione; }
	public String[] getTipo() { return tipo; }
	public String getGenerazione() { return generazione; }
	public String getDexNumber() { return dexNumber; }
	public String[] getLineaEvolutiva() { return lineaEvolutiva; }
	public int[] getIndividualValues() { return individualValues; }
	public boolean isCatturato() { return catturato; }
	public boolean isActive() { return active; }
	
	//SETTER
	public void setNome(String nome) { this.nome = nome;}
	public void setPokemonId(String img) { this.img = img; }
	public void setShiny(boolean shiny) { this.shiny = shiny; }
	public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
	public void setTipo(String[] tipi) { this.tipo = tipi; }
	public void setGenerazione(String generazione) { this.generazione = generazione; }
	public void setDexNumber(String dexNumber) { this.dexNumber = dexNumber; }
	public void setLineaEvolutiva(String[] lineaEvolutiva) { this.lineaEvolutiva = lineaEvolutiva; }
	public void setIndividualValues(int[] individualValues) { this.individualValues = individualValues; }
	public void setCatturato(boolean catturato) { this.catturato = catturato; }
	public void setActive(boolean active) { this.active = active; }
	
} // fine classe Pokemon
