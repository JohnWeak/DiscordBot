import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Pokemon
{
	private static final Object object = Pokemon.class;
	
	private static final File nomiPokemon = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	private final boolean pokedex;
	
	private String nome;
	private String img;
	private boolean shiny = false;
	private String descrizione;
	private String[] tipo = new String[]{" "," "};
	private String[] lineaEvolutiva = new String[]{"1","2","3"};
	private String generazione;
	private String dexNumber;
	private int[] individualValues = new int[6];
	private boolean catturato = false;
	private JSONArray types;
	private JSONArray jsonArray = new JSONArray();
	private static JSONParser jsonParser = new JSONParser();
	private EmbedBuilder embedBuilder = null;
	
	// private static int pokemon_id = 261; -> Poochyena
	// https://pokeapi.co/api/v2/pokemon/261/
	
	public Pokemon(int id, boolean pokedex)
	{
		var pm = new PrivateMessage(Utente.getGion());
		this.pokedex = pokedex;
		final var dir = new File("DiscordBot/json_pokemon");
		final var pokemons = dir.listFiles();
		
		if (pokemons == null)
		{
			pm.send("`pokemons: null`");
			return;
		}
		
		final int max = pokemons.length;
		
		
		// determina se il pokemon sarà shiny
		if (random.nextInt(8142) == 42)
			shiny = true;
		
		// Genera i valori individuali
		// [HP, ATK, DEF, SPA, SPD, SPE]
		
		for (int index : individualValues)
			individualValues[index] = random.nextInt(32); // IVs: 0-31
		
		if (id <= 0 || id > max)
			id = random.nextInt(1, max+1);
		
		
		// prendere i dati dal .json
		JSONObject data = getJsonObject(pokemons[id]);
		
		dexNumber = (String) data.get("id");
		nome = (String) data.get("name");
		types = (JSONArray) data.get("types");
		descrizione = (String) data.get("flavor_text");
		generazione = (String) data.get("generation");
		
		tipo[0] = (String) types.get(0);
		if (types.size() > 1)
			tipo[1] = (String) types.get(1);
		
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
		final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/" + id + ".png";
		
		img = (shiny ? urlShinyImg : urlImg);
		
		
		pm.send("nome: " + nome);
		pm.send("dexNumber: " + dexNumber);
		pm.send("descrizione: " + descrizione);
		pm.send("generazione: " + generazione);
		pm.send("tipo/i: " + Arrays.toString(tipo));
	} // fine costruttore
	
	
	public void spawn(Pokemon pokemon)
	{
		if (embedBuilder == null)
		{
			new Error<String>().print(object, "`Can't execute because embedBuilder is null`");
			return;
		}
		
		if (pokedex)
		{
			Commands.canaleBotPokemon.sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else
		{
			var t = new ThreadPokemon(pokemon, Commands.canaleBotPokemon, embedBuilder);
			var tout = random.nextInt(2, 30);
			t.setTimeoutTime(t.MINUTES, tout);
			t.start();
			//new PrivateMessage(Utente.getGion()).send("\nThread alive:" + t.isAlive() + "\ntout: " + tout + "\n");
		}
	} // fine startEncounter
	
	
	private static JSONObject getJsonObject(File f)
	{
		String line;
		StringBuilder sb = new StringBuilder();
		Scanner scanner;
		
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
			new Error<Exception>().print(object, e);
		}
		
		JSONObject rtrn = null;
		try
		{
			rtrn = (JSONObject) jsonParser.parse(String.valueOf(sb));
		}catch (Exception e)
		{
			System.out.println("Errore con "+f.getName());
		}
		return rtrn;
	}
	
	/** Genera un embed con il Pokemon */
	private EmbedBuilder buildEmbed(boolean pokedex)
	{
		var embedBuilder = new EmbedBuilder();
		var stringBuilder = new StringBuilder();
		var types = "";
		
		if (pokedex) // se è una entry del pokedex, mostra le informazioni varie
		{
			stringBuilder.append(tipo[0]);
			if (!(tipo[1].equals(" ")))
			{
				stringBuilder.append(" / ").append(tipo[1]);
			}
			types = String.valueOf(stringBuilder);
			
			// final String iconURL = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F53%2FPok%25C3%25A9_Ball_icon.svg%2F1026px-Pok%25C3%25A9_Ball_icon.svg.png&f=1&nofb=1";
			
			try
			{
				embedBuilder.setTitle(nome.toUpperCase());
			}
			catch (Exception e) { new Error<Exception>().print(object, e); }
			
			if (descrizione != null)
			{
				String type = "Type";
				if (!tipo[1].equals(" "))
					type += "s";
				
				embedBuilder.addField("**"+type+"**", ""+types, true);
				embedBuilder.addField("Generation", ""+generazione, true);
				embedBuilder.addField("National Dex", ""+dexNumber, true);
				
				embedBuilder.addField("Pokedex Entry", "*"+descrizione+"*", false);
				embedBuilder.setThumbnail(img);
			}
			else
			{
				embedBuilder.setImage(img);
			}
			
			var color = shiny ? 0xFFD020 : 0xFF0000;
			embedBuilder.setColor(color);
			
			if (shiny)
				embedBuilder.setFooter("✨ Shiny! ✨");
			
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
		try
		{
			Scanner scanner = new Scanner(nomiPokemon);
			while (scanner.hasNext())
			{
				x++;
				if (scanner.nextLine().equalsIgnoreCase(nome))
					break;
			}
		}catch (Exception e) { new Error<Exception>().print(object, e); }
		return x;
	}
	
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
	
	
} // fine classe
