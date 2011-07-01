package zeditor.windows;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import zeditor.core.Options;

/**
 * Classe de gesstion des param�tres et options de Zeditor
 * @author Drakulo
 */
public class OptionHelper {
	/**
	 * M�thode statique de chargement des param�tres.
	 * @return Map <String, String> : une Map contenant les param�tres
	 * @author Drakulo
	 */
	public static Map<String, String> load() {
		// On cr�e une instance de SAXBuilder
		Document document;
		Element racine;
		Map<String, String> map = new HashMap<String, String>();
		try {
			DocumentBuilder sxb = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// On charge le fichier de configuration
			File config = new File("config.xml");
			if (!config.exists()) {
				save(new HashMap<String, String>());
				return load();
			}
			// On cr�e un nouveau document JDOM avec en argument le fichier XML
			document = sxb.parse(config);

			// On initialise un nouvel �l�ment racine avec l'�l�ment racine du
			// document.
			racine = document.getDocumentElement();

			// Mantenant qu'on a la racine, on r�cup�re les infos
			if(racine.getChildNodes() != null || racine.getChildNodes().getLength()!=0){
				for(Options item : Options.values()){
					Node node=racine.getElementsByTagName(item.getValue()).item(0);
					if (node != null) {
						node = node.getFirstChild();
					
						String value = "";
						if (node != null) {
							value = node.getTextContent();
						}
						map.put(item.getValue(), value);
					}
				}
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * M�thode statique de sauvegarde des param�trages (appel�e dans la fen�tre
	 * d'options)
	 * @param p_params
	 * @author Drakulo
	 */
	public static void save(Map<String, String> p_params) {
		try {
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur;
			constructeur = fabrique.newDocumentBuilder();
			Document document = constructeur.newDocument();
			// Cr�ation du noeud racine
			Element racine = document.createElement("config");
			// Affectation du noeud racine au document
			document.appendChild(racine);

			// On cr�e maintenant chacun des �l�ments de param�trage
			for(Options name : Options.values()){
				Element modele = document.createElement(name.getValue());
				String item = (p_params.get(name.getValue()) != null) ? p_params.get(name.getValue()) : "";
				modele.appendChild(document.createTextNode(item));
				racine.appendChild(modele);
			}

			// On sauvegarde le fichier de config
			saveXml(document, "./config.xml");

		} catch (ParserConfigurationException e) {
			// Erreur possible lors de la construction du DocumentBuilder ?!
			e.printStackTrace();
		}
	}

	/**
	 * M�thode priv�e de sauvegarde du fichier de configuration
	 * @param document Document : le document � sauvegarder
	 * @param fichier String : le chemin du fichier dans lequel on va sauvegarder les informations
	 * @author Drakulo
	 */
	private static void saveXml(Document document, String fichier) {
		try {
			// On cr�e le source DOM
			Source source = new DOMSource(document);

			// On cr�e le fichier XML
			Result resultat = new StreamResult(new File(fichier));

			// On configure le transformeur
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			// On envoie la sauce
			transformer.transform(source, resultat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�thode de chargement du param�tre pass� en param�tres
	 * @param p_option : Entr�e de l'�num�tation Options � charger
	 * @return String : Valeur du param�tre
	 * @author Drakulo
	 */
	public static String loadOption(String p_option) {
		Map<String, String> map = load();
		if(map != null && !map.isEmpty()){
			return map.get(p_option);
		}else{
			return null;
		}
	}
	
	/**
	 * M�thode de sauvegarde unitaire d'une option
	 * @param p_option : Entr�e de l'�num�tation Options � modifier
	 * @param p_value : Valeur � sauvegarder
	 * @author Drakulo
	 */
	public static void saveOption(Options p_option, String p_value){
		Map<String, String> map = load();
		map.put(p_option.getValue(), p_value);
		save(map);
	}
}
