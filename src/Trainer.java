import java.util.ArrayList;

/**Questa classe serve a memorizzare gli utenti che catturano pokemon*/
public class Trainer
{
	private String nome;
	private String ID;
	private ArrayList<Pokemon> pokemonCatturati;
	
	public Trainer(String nome, String ID)
	{
		this.nome = nome;
		this.ID = ID;
		// TODO: pokemonCatturati = getArrayListFromDB()
	}
	
	public void catturaPokemon(Pokemon pokemon)
	{
		if (pokemonCatturati.indexOf(pokemon) < 1)
		{
			pokemonCatturati.add(pokemon);
			pokemon.setCatturato(true);
		}
	}
	
	
	
	
	// GETTER E SETTER
	public String getNome()
	{
		return nome;
	}
	public void setNome(String nome)
	{
		this.nome = nome;
	}
	public String getID()
	{
		return ID;
	}
	public void setID(String ID)
	{
		this.ID = ID;
	}
	public ArrayList<Pokemon> getPokemonCatturati()
	{
		return pokemonCatturati;
	}
	public void setPokemonCatturati(ArrayList<Pokemon> pokemonCatturati)
	{
		this.pokemonCatturati = pokemonCatturati;
	}

} // fine classe Trainer
