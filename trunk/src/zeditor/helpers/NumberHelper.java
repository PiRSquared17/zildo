package zeditor.helpers;

/**
 * Classe de gestion des nombres
 * @author Drakulo
 *
 */
public class NumberHelper {
	
	/**
	 * Renvoie la plus petite valeur des deux param�tres (Integer)
	 * @param a Entier A
	 * @param b Entier B
	 * @return le plus petit Entier des deux
	 */
	public static Integer min(Integer a, Integer b){
		return (a < b)? a : b;
	}
	
	/**
	 * Renvoie la plus grande valeur des deux param�tres (Integer)
	 * @param a Entier A
	 * @param b Entier B
	 * @return le plus grand Entier des deux
	 */
	public static Integer max(Integer a, Integer b){
		return (a > b)? a : b;
	}
	
	/**
	 * Renvoie la plus petite valeur des deux param�tres (Long)
	 * @param a Long A
	 * @param b Long B
	 * @return le plus petit Long des deux
	 */
	public static Long min(Long a, Long b){
		return (a < b)? a : b;
	}
	
	/**
	 * Renvoie la plus grande valeur des deux param�tres (Long)
	 * @param a Long A
	 * @param b Long B
	 * @return le plus grand Long des deux
	 */
	public static Long max(Long a, Long b){
		return (a > b)? a : b;
	}
	
	/**
	 * Renvoie la plus petite valeur des deux param�tres (Double)
	 * @param a Double A
	 * @param b Double B
	 * @return le plus petit Double des deux
	 */
	public static Double min(Double a, Double b){
		return (a < b)? a : b;
	}
	
	/**
	 * Renvoie la plus grande valeur des deux param�tres (Double)
	 * @param a Double A
	 * @param b Double B
	 * @return le plus grand Double des deux
	 */
	public static Double max(Double a, Double b){
		return (a > b)? a : b;
	}
}
