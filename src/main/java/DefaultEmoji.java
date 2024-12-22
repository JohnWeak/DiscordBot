public enum DefaultEmoji
{
	MELA_ROSSA("🍎"),
	MELA_VERDE("🍏"),
	ARACHIDI("🥜"),
	INDIA_BANDIERA("🇮🇳"),
	POLLICE_SU( "👍🏻"),
	POLLICE_GIU( "👎🏻"),
	MELANZANA("🍆"),
	CHECK("✅"),
	CROSS("❌");
	
	private final String emoji;
	DefaultEmoji(String emoji)
	{
		this.emoji = emoji;
	}
	
	public String getEmoji() { return emoji; }
	
} // fine classe Emoji
