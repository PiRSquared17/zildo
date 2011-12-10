package zeditor.core;

/**
 * Enum�ration des options de Zeditor
 * @author Drakulo
 *
 */
public enum Options {
	SHOW_TILES_UNMAPPED("showTilesUnmapped"),
	SHOW_TILES_GRID("showTilesGrid"),
	SHOW_COLLISION("showCollision");
	
	/** L'attribut qui contient la valeur associ� � l'enum */
	private final String value;
	
	/** Le constructeur qui associe une valeur � l'enum */
	private Options(String value) {
		this.value = value;
	}
	
	/** La m�thode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}
}
