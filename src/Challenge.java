import net.dv8tion.jda.api.entities.User;

public class Challenge
{
	private User sfidante, sfidato;
	private String tipoSfida;
	
	public Challenge()
	{
	
	}
	
	public Challenge(User sfidante, User sfidato, String tipoSfida)
	{
		this.sfidante = sfidante;
		this.sfidato = sfidato;
		this.tipoSfida = tipoSfida;
	}
	
	
	// GETTER
	public User getSfidante()
	{
		return sfidante;
	}
	public User getSfidato()
	{
		return sfidato;
	}
	public String getTipoSfida()
	{
		return tipoSfida;
	}
	
	// SETTER
	public void setSfidante(User sfidante)
	{
		this.sfidante = sfidante;
	}
	public void setSfidato(User sfidato)
	{
		this.sfidato = sfidato;
	}
	public void setTipoSfida(String tipoSfida)
	{
		this.tipoSfida = tipoSfida;
	}
	
} // fine classe Challenge
