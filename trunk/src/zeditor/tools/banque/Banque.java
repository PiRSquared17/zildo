package zeditor.tools.banque;

import java.awt.Point;
import java.util.List;
import java.util.Map;

//Regexps � appliquer:
///////////////////////
//1) commentaires
//find:    \{(.*)\}
//replace: /*$1*/
//2) les points
//find:     \(([0-9]*),([0-9]*)\)
//replace: new Point($1, $2)

public abstract class Banque {

	// Donn�es d'entr�e
	Point[] coords;
	List<Point> pkmChanges;
	// Donn�es construites par {@link GenereCorrespondanceDec#doTheJob()}
	Map<Point, Integer> mapCorrespondance;

	// Ensemble des points correspondant � la position haute-gauche de chaque
	// tile
	public Point[] getCoords() {
		return coords;
	}

	// List des num�ros de tile o� on change de PKM
	// Dans point on a: x=num�ro de tile / y=offset Y pour la page suivante
	public List<Point> getPkmChanges() {
		return pkmChanges;
	}

	public void setMapCorrespondance(Map<Point, Integer> map) {
		mapCorrespondance = map;
	}

	/**
	 * Renvoie le num�ro de la tile � la position donn�e. Renvoie -1 si il n'y a
	 * pas de tile � cet endroit.
	 * 
	 * @param x
	 * @param y
	 * @return int
	 */
	public int getNumTile(int x, int y) {
		Point p = new Point(x, y);
		Integer i = mapCorrespondance.get(p);
		return i == null ? -1 : i.intValue();
	}
}
