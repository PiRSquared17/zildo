package zeditor.core;

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
public abstract class Selection {
	/**
	 * Liste des �l�ments de la s�lection
	 */
	protected List<Case> items;

	/**
	 * Constructeur vide
	 */
	public Selection() {
	}

	/**
	 * Constructeur � partir d'une liste
	 * 
	 * @param l
	 *            est la liste des �l�ments de la s�lection
	 */
	public Selection(List<Case> l) {
		items = l;
	}

	/**
	 * Getter de la liste d'�l�ments
	 * 
	 * @return La liste des items
	 */
	public List<Case> getItems() {
		return items;
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

	/**
	 * R�cup�re un �l�ment de la s�lection
	 * 
	 * @param Index
	 *            est l'index de l'�l�ment � r�cup�rer dans la liste
	 * @return la valeur de l'�l�ment
	 */
	public Case getItem(Integer index) {
		if (index > items.size()) {
			return null;
		} else {
			return items.get(index);
		}
	}

}
