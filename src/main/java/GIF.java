/**Questa classe contiene gli indirizzi URL delle GIF.*/
public enum GIF
{
	spyHang("https://c.tenor.com/pLhXMqtRw2gAAAAC/team-fortress-spy.gif"),
	eggdog("https://c.tenor.com/OWWuHRZ53-cAAAAd/tenor.gif"),
	engineer("https://i.warosu.org/data/diy/img/0014/73/1538282856261.gif");
	
	
	
	private final String url;
	GIF(String url)
	{
		this.url = url;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	
} // fine classe GIF
