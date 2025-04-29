import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Pokemon
{
	// UNRELATED TO POKEMON
	private static final Object object = Pokemon.class;
	private static final Errore<Exception> error = new Errore<>();
	private static final String NAMES_FILE = "./src/main/java/nomiPokemon.txt";
	private static final String JSON_FILES = "./src/main/java/json/";
	private final boolean debug = false;
	private final PrivateMessage pm = new PrivateMessage(Utente.getGion());
	
	// FILE
	private static final File nomiPokemon = new File(NAMES_FILE);
	private static final Random random = new Random();
	private final boolean pokedex;
	public static final int ALL = 898;
	private final JsonArray jsonArray = new JsonArray();
	private EmbedBuilder embedBuilder = null;
	private ThreadPokemon t = null;
	
	// POKEMON INFO
	@Getter @Setter private String nome;
	@Getter @Setter private String nomeFile;
	@Getter @Setter private String img;
	@Getter private boolean shiny = false;
	@Getter @Setter private String descrizione;
	@Getter @Setter private String[] tipo = new String[]{" "," "};
	@Getter @Setter private String generazione;
	@Getter @Setter private String dexNumber;
	@Getter @Setter private int[] individualValues = new int[6];
	@Getter @Setter private boolean catturato = false;
	@Getter @Setter private boolean catturabile = false;
	@Getter @Setter private User owner;
	@Getter @Setter private int id;
	private JsonArray types;
	
	// private static int pokemon_id = 261; -> Poochyena
	// https://pokeapi.co/api/v2/pokemon/261/
	
	public Pokemon(int id, boolean pokedex)
	{
		this.pokedex = pokedex;
		this.id = id;
		final File jsonFile;
		owner = null;
		
		String line="";
		BufferedReader reader = null;
		final Path path;
		
		if (id > ALL)
			return;
		
		if (id <= 0 && pokedex)
		{
			return;
		}
		
		if (!pokedex)
		{
			id = random.nextInt(1, ALL+1);
		}
		
		try
		{
			path = Paths.get(NAMES_FILE);
			reader = Files.newBufferedReader(path);
			for (int i = 0; i < id; i++)
				line = reader.readLine().toLowerCase();
			
			nomeFile = line;
			
			if (debug)
				pm.send("`\n\nnome="+nomeFile+"\n\n`");
			
			jsonFile = new File(JSON_FILES + nomeFile+ ".json");
			
		}
		catch (Exception e)
		{
			error.print(object,e);
			return;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}catch (IOException e) { error.print(object,e); }
			}
		}
		// determina se il pokemon sarà shiny
		if (random.nextInt(8142) == 42)
			shiny = true;
		
		// Genera i valori individuali
		// [HP, ATK, DEF, SPA, SPD, SPE]
		
		for (int index : individualValues)
			individualValues[index] = random.nextInt(32); // IVs: 0-31
		
		
		// prendere i dati dal .json
		final JsonObject data = getJsonObject(jsonFile);
		
		dexNumber = data.get("id").getAsString();
		nome = Utilities.capitalize(data.get("name").getAsString());
		types = data.get("types").getAsJsonArray();
		descrizione = data.get("flavor_text").getAsString();
		generazione = data.get("generation").getAsString();
		
		tipo[0] = types.get(0).getAsString();
		tipo[0] = tipo[0].toUpperCase();
		
		if (types.size() > 1)
		{
			tipo[1] = types.get(1).getAsString();
			tipo[1] = tipo[1].toUpperCase();
		}
		
		final String shinyString = shiny ? "shiny/" : "";
		img = String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%s%d.png", shinyString, id);
		
		//final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
		//final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/" + id + ".png";
		
		//img = (shiny ? urlShinyImg : urlImg);
		
		if (debug)
		{
			final String msg = String.format("nome: %s\ndexNumber: %s\ndescrizione: %s\ngenerazione: %s\ntipo/i: %s", nome, dexNumber, descrizione, generazione, Arrays.toString(tipo));
			pm.send(msg);
		}
		
	} // fine costruttore
	
	
	public EmbedBuilder spawn()
	{
		if (embedBuilder == null)
			embedBuilder = buildEmbed(pokedex);
		
		return embedBuilder;
	} // fine spawn()
	
	
	private static JsonObject getJsonObject(File f)
	{
		String line;
		final StringBuilder sb = new StringBuilder();
		final Scanner scanner;
		
		try
		{
			scanner = new Scanner(f);
			
			while (scanner.hasNext())
			{
				if ((line = scanner.nextLine()) != null)
					sb.append(line);
			}
		
		}
		catch (FileNotFoundException e)
		{
			error.print(object, e);
		}
		
		JsonObject rtrn = null;
		try
		{
			rtrn = JsonParser.parseString(sb.toString()).getAsJsonObject();
		}catch (Exception e)
		{
			error.print(object,e);
		}
		return rtrn;
	}
	
	/** Genera un embed con il Pokemon */
	private EmbedBuilder buildEmbed(boolean pokedex)
	{
		final EmbedBuilder embedBuilder = new EmbedBuilder();
		final StringBuilder stringBuilder = new StringBuilder();
		String types;
		
		if (pokedex) // se è una entry del pokedex, mostra le informazioni varie
		{
			stringBuilder.append(tipo[0]);
			if (!(tipo[1].isEmpty()))
			{
				stringBuilder.append(" / ").append(tipo[1]);
			}
			types = String.valueOf(stringBuilder);
			
			// final String iconURL = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F53%2FPok%25C3%25A9_Ball_icon.svg%2F1026px-Pok%25C3%25A9_Ball_icon.svg.png&f=1&nofb=1";
			
			try
			{
				embedBuilder.setTitle(nome.toUpperCase());
			}
			catch (Exception e) { error.print(object, e); }
			
			if (descrizione != null)
			{
				String type = "Type";
				if (!tipo[1].isEmpty())
					type += "s";
				
				embedBuilder.addField("**"+type+"**", types, true);
				embedBuilder.addField("Generation", generazione, true);
				embedBuilder.addField("National Dex", dexNumber, true);
				
				embedBuilder.addField("Pokedex Entry", "*"+descrizione+"*", false);
				embedBuilder.setThumbnail(img);
			}
			else
			{
				embedBuilder.setImage(img);
			}
			
			final int color = shiny ? 0xFFD020 : 0xFF0000;
			embedBuilder.setColor(color);
			
			if (shiny)
			{
				embedBuilder.setFooter("✨ Shiny! ✨");
			}
			
		}
		else // se non è una entry del pokedex mostra solo nome e immagine
		{
			embedBuilder
				.setTitle("A wild " + nome + " appears!")
				.setImage(img)
				.setColor(Color.red)
				.setFooter("Type !catch to capture it.")
			;
		}
		
		return embedBuilder;
	} // fine buildEmbed()
	
	public static int getId(String nome)
	{
		int x = 0;
		boolean trovato = false;
		try
		{
			final Scanner scanner = new Scanner(nomiPokemon);
			while (scanner.hasNext())
			{
				x += 1;
				if (scanner.nextLine().equalsIgnoreCase(nome))
				{
					trovato = true;
					break;
				}
			}
		} catch (Exception e) { error.print(object, e); }
		
		
		return trovato ? x : -1;
	}
	
	public ThreadPokemon getThread() { return t; }
	
	public void setShiny(boolean shiny)
	{
		this.shiny = shiny;
		final String shinyString = shiny ? "shiny/" : "";
		img = String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%s%d.png",shinyString, id);
	}
	
} // fine classe
