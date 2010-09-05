package zeditor.windows.managers;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import zeditor.core.Options;
import zeditor.core.Selection;
import zeditor.core.exceptions.ZeditorException;
import zeditor.core.prefetch.Prefetch;
import zeditor.core.tiles.TileSelection;
import zeditor.core.tiles.TileSet;
import zeditor.helpers.OptionHelper;
import zeditor.windows.ExplorerFrame;
import zeditor.windows.MasterFrame;
import zeditor.windows.OptionsFrame;
import zildo.fwk.awt.ZildoCanvas;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.server.EngineZildo;


/**
 * Classe de management de la fen�tre principale de Zeditor (MasterFrame.class)
 * 
 * @author Drakulo
 */
public class MasterFrameManager {
	public static JLabel systemDisplay;
	private static TileSet tileSet;
	private JPanel masterPanel;
	private static MasterFrame masterFrame;
	private JComboBox backgroundCombo;

	private static ZildoCanvas zildoCanvas;

	private static Selection currentSelection;
	
	private String currentMapFile;

	public final static int MESSAGE_ERROR = 1;
	public final static int MESSAGE_INFO = 2;

	/**
	 * Constructeur vide
	 * 
	 * @author Drakulo
	 */
	public MasterFrameManager(MasterFrame p_frame) {
		masterFrame = p_frame;
	}

	/**
	 * Constructeur avec une liste d'objets correspondant aux diff�rents objets
	 * de la MasterFrame
	 * 
	 * @param p_sys
	 *            Le JLabel Syst�me de la MasterFrame
	 * @author Drakulo
	 */
	public MasterFrameManager(JLabel p_sys, TileSet p_tile, JPanel p_master,
			MasterFrame p_frame, JComboBox p_backgroundCombo,
			ZildoCanvas p_zildoCanvas) {
		systemDisplay = p_sys;
		tileSet = p_tile;
		masterPanel = p_master;
		masterFrame = p_frame;
		backgroundCombo = p_backgroundCombo;
		zildoCanvas = p_zildoCanvas;
		
		//Make canvas get the focus whenever frame is activated.
		masterFrame.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	zildoCanvas.requestFocusInWindow();
		    }
		});

		updateTitle();
		
	}

	/**
	 * Ferme la fen�tre de Zeditor
	 * 
	 * @author Drakulo
	 */
	public void exit() {
		// TODO Ajouter un test de v�rification s'il y a eu une modification et
		// demander une sauvegarde le cas �ch�ant.
		System.exit(0);
	}

	/**
	 * Sauve la carte en cours dans la carte en cours. Si la carte en cours n'a
	 * pas encore �t� sauvegard�e, on appelle la m�thode
	 * {@link MasterPanelManager.saveAs saveAs()}
	 * 
	 * @author Drakulo
	 */
	public void save() {
		zildoCanvas.saveMapFile(currentMapFile);
		display("Sauvegarde effectu�e.", MESSAGE_INFO);
	}

	/**
	 * Ouvre l'explorateur afin de s�lectionner le nom du fichier � sauvegarder
	 * pui lance (ou annule) la sauvegarde
	 */
	public void saveAs() {
		display("[A FAIRE] Enregistrer sous...", MESSAGE_ERROR);
		openFileExplorer(ExplorerFrameManager.SAVE);
		// TODO
	}

	/**
	 * Charge une nouvelle carte
	 * 
	 * @author Drakulo
	 */
	public void load() {
		openFileExplorer(ExplorerFrameManager.OPEN);
		// TODO

	}

	public void loadMap(String p_mapName) {

		display("Ouverture du fichier : " + p_mapName, MESSAGE_INFO);
		zildoCanvas.loadMap(p_mapName);
		display("Chargement effectu�.", MESSAGE_INFO);
		currentMapFile = p_mapName;
		
		updateTitle();
	}


	public void updateTitle() {
		StringBuilder sb=new StringBuilder("ZEditor - ");
		Area map=EngineZildo.mapManagement.getCurrentMap();
		if (map != null) {
			sb.append(map.getName());
			sb.append(" - ");
			sb.append(map.getDim_x()+" x "+map.getDim_y());
		} else {
			sb.append("Nouvelle carte");
		}
		masterFrame.setTitle(sb.toString());		
	}
	
	/**
	 * Cr�e une nouvelle carte
	 * 
	 * @author Drakulo
	 */
	public void create() {
		// TODO
		changeTitle("Carte sans nom");
		display("[A FAIRE] Nouvelle carte", MESSAGE_ERROR);
	}

	/**
	 * Charge la liste des TileSets pour la combo de d�cors
	 * 
	 * @return Un tableau de String des titres des TileSets
	 * @author Drakulo
	 */
	public Object[] loadTileForCombo() {
		try {
			return masterFrame.getTileSetPanel().getTiles();
		} catch (ZeditorException e) {
			display(e.getMessage(), MESSAGE_ERROR);
			return new Object[]{""};
		}
	}

	/**
	 * Charge le tileSet dont le nom est pass� en param�tres
	 * 
	 * @param name
	 * @author Drakulo
	 */
	public void changeTileSet(String p_name) {
		try {
			masterFrame.getTileSetPanel().changeTile(p_name);
			display("TileSet '" + p_name + "' charg�.", MESSAGE_INFO);
		} catch (ZeditorException e) {
			display(e.getMessage(), MESSAGE_ERROR);
		}
	}

	public Object[] getPrefetchForCombo() {
		return Prefetch.getNames();
	}
	
	/**
	 * Affiche un message dans le label Syst�me
	 * 
	 * @param p_msg
	 *            est le message � afficher
	 * @param p_type
	 *            est le type de message
	 * @author Drakulo
	 */
	public static void display(String p_msg, int p_type) {
		if (systemDisplay != null) {
			systemDisplay.setText(" " + p_msg);
			switch (p_type) {
				case MESSAGE_ERROR :
					systemDisplay.setForeground(Color.red);
					break;
				case MESSAGE_INFO :
				default :
					systemDisplay.setForeground(Color.black);
					break;
			}
		}
	}

	/**
	 * Ouvre la fen�tre de param�trage des options
	 * 
	 * @author Drakulo
	 */
	public void openOptionsFrame() {
		OptionsFrame optFrame = new OptionsFrame();
		optFrame.setLocationRelativeTo(masterFrame);
		optFrame.setVisible(true);
		optFrame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent arg0) {
			}
			public void windowClosed(WindowEvent arg0) {
			}
			public void windowClosing(WindowEvent arg0) {
			}
			public void windowDeactivated(WindowEvent arg0) {
				updateTools();
				masterPanel.repaint();
			}
			public void windowDeiconified(WindowEvent arg0) {
			}
			public void windowIconified(WindowEvent arg0) {
			}
			public void windowOpened(WindowEvent arg0) {
			}

		});
	}

	/**
	 * Recharge les "petites" configurations
	 * 
	 * @author Drakulo
	 */
	public void reloadConfig() {
		updateTools();
		masterPanel.repaint();
		display("Petite configuration recharg�e.", MESSAGE_INFO);
	}

	/**
	 * Changement d'une option avec sauvegarde
	 * 
	 * @param p_option
	 *            : Entr�e de l'�num�ration Options
	 * @param p_value
	 *            : Valeur � attribuer
	 * @author Drakulo
	 */
	public void saveOption(String p_option, String p_value) {
		OptionHelper.saveOption(p_option, p_value);
		masterPanel.repaint();
	}

	/**
	 * Chargement d'une option
	 * 
	 * @param p_option
	 *            : Entr�e de l'�num�ration Options
	 * @return La valeur param�tr�e de l'option
	 * @author Drakulo
	 */
	public String loadOption(String p_option) {
		return OptionHelper.loadOption(p_option);
	}

	/**
	 * Met � jours les boutons de la ToolBar. Cette m�thode est d�pendante de la
	 * structure de la fen�tre. MasterPanel >> ToolbarContainer >> ToolBar
	 * 
	 * @author Drakulo
	 */
	public void updateTools() {
		JToolBar toolBar = (JToolBar) ((JPanel) masterPanel.getComponent(0))
				.getComponent(0);

		// Bouton des tuiles non mapp�es
		JToggleButton unmapped = (JToggleButton) toolBar.getComponent(0);
		if (Boolean.valueOf(OptionHelper.loadOption(Options.SHOW_TILES_UNMAPPED
				.getValue()))) {
			unmapped.setSelected(true);
		} else {
			unmapped.setSelected(false);
		}

		// Bouton d'affichage de la grille
		JToggleButton grid = (JToggleButton) toolBar.getComponent(1);
		if (Boolean.valueOf(OptionHelper.loadOption(Options.SHOW_TILES_GRID
				.getValue()))) {
			grid.setSelected(true);
		} else {
			grid.setSelected(false);
		}
	}

	/**
	 * Change le titre de la fen�tre Zildo avec le texte : "Zeditor - [TITLE]"
	 * 
	 * @param title
	 * @author Drakulo
	 */
	public void changeTitle(String title) {
		masterFrame.setTitle("Zeditor - " + title);
	}

	/**
	 * M�thode de test pour afficher le num�ro des tuiles s�lectionn�es
	 * 
	 * @author Drakulo
	 */
	public void displaySelectedTiles() {
		if (tileSet.getCurrentSelection() != null) {
			display(tileSet.getCurrentSelection().toString(), MESSAGE_INFO);
		}
	}

	/**
	 * Affiche ou masque la grille sur le TileSet suivant le param�tre
	 * 
	 * @param flag
	 *            true : afficher, false : masquer
	 * @author Drakulo
	 */
	public void showTileSetGrid(boolean flag) {
		saveOption(Options.SHOW_TILES_GRID.getValue(), String.valueOf(flag));
		if (flag) {
			display("Grille affich�e.", MESSAGE_INFO);
		} else {
			display("Grille masqu�e.", MESSAGE_INFO);
		}
	}

	/**
	 * Affiche ou masque les tuiles non mapp�es sur le TileSet suivant le
	 * param�tre
	 * 
	 * @param flag
	 *            true : afficher, false : masquer
	 * @author Drakulo
	 */
	public void showTileSetUnmapped(boolean flag) {
		saveOption(Options.SHOW_TILES_UNMAPPED.getValue(), String.valueOf(flag));
		if (flag) {
			display("Tuiles non mapp�es mises en �vidence.", MESSAGE_INFO);
		} else {
			display("Tuiles non mapp�es ignor�es.", MESSAGE_INFO);
		}
	}

	/**
	 * Ouvre l'explorateur de fichier avec les param�tres
	 * 
	 * @param mode
	 *            est les mode (ouverture / sauvegarde) :
	 *            <p>
	 *            {@link ExplorerFrameManager.OPEN} /
	 *            {@link ExplorerFrameManager.SAVE}
	 *            </p>
	 */
	public void openFileExplorer(int mode) {
		ExplorerFrame explorer = new ExplorerFrame(masterFrame, mode);
		explorer.setLocationRelativeTo(masterFrame);
		explorer.setVisible(true);
	}
	/**
	 * Initialisation de la fen�tre
	 */
	public void init() {
		updateTools();
		changeTileSet(backgroundCombo.getSelectedItem().toString());
	}

	public static Selection getSelection() {
		int sel=masterFrame.getTabsPane().getSelectedIndex();
		switch (sel) {
		case 0: // tiles
			return tileSet.getCurrentSelection();
		case 1:	// prefetch
			return currentSelection;
		}
		return null;
	}

	public static ZildoCanvas getZildoCanvas() {
		return zildoCanvas;
	}
	
	/**
	 * Stop copy mode and switch to *block* tileset.
	 */
	public static void switchCopyTile(int p_width, int p_height, List<Case> p_cases) {
		if (p_width > 0 && p_height >0) {
		    masterFrame.getCopyPasteTool().setSelected(false);
		    masterFrame.getBackgroundCombo().selectWithKeyChar('*');
		    masterFrame.getTileSetPanel().buildSelection(p_width, p_height, p_cases);
		}
	}

	public static void setCurrentSelection(Selection p_currentSelection) {
		currentSelection = p_currentSelection;
		if (currentSelection instanceof TileSelection) {
			TileSelection tileSel=(TileSelection) currentSelection;
			getZildoCanvas().setCursorSize(tileSel.width, tileSel.height);
		}
	}
}
