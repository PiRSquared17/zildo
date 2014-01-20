package zeditor.core.selection;

import java.util.ArrayList;
import java.util.List;

import zeditor.core.tiles.TileSelection;
import zildo.monde.map.Case;
/**
 * Cette classe repr�sente une s�lection dans Zeditor. Elle est abstraite et ne
 * peut pas �tre utilis�e comme telle. Il faut utiliser l'une des classes filles
 * suivantes :
 * <p>
 * <ul>
 * <li>{@link TileSelection}</li>
 * </ul>
 * </p>
 * 
 * @author Drakulo
 * 
 */
public abstract class CaseSelection extends Selection {
	/**
	 * Liste des �l�ments de la s�lection
	 */
	protected List<Case> items;

	/**
	 * Constructeur vide
	 */
	public CaseSelection() {
		items=new ArrayList<Case>();
	}

	/**
	 * Constructeur � partir d'une liste
	 * 
	 * @param l
	 *            est la liste des �l�ments de la s�lection
	 */
	public CaseSelection(List<Case> l) {
		items = l;
	}

	/**
	 * Setter de la liste d'�l�ments
	 * 
	 * @param l
	 *            est la liste des �l�ments
	 */
	public void setItems(List<Case> l) {
		items = l;
	}


}
