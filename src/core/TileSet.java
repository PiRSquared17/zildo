package zeditor.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import zeditor.core.exceptions.TileSetException;
import zeditor.core.exceptions.ZeditorException;
import zeditor.helpers.OptionHelper;
import zeditor.tools.CorrespondanceGifDec;

/**
 * Classe de gestion des Tuiles
 * @author Drakulo
 */
public class TileSet extends JPanel {
	private static final long serialVersionUID = 8712246788178837311L;

	private String tileName;
	private CorrespondanceGifDec bridge;
	private Point startPoint;
	private Point stopPoint;
	private Image tile;
	private Integer tileWidth;
	private Integer tileHeight;
	private TileSelection currentSelection;

	/**
	 * Constructeur vide
	 */
	public TileSet(){}

	/**
	 * Constructeur avec param�tres
	 * @param p_tileName : Nom du set de tuiles en cours
	 * @author Drakulo
	 */
	public TileSet(String p_tileName) {
		// D�finition du layout afin d'afficher le Tile en haut du conteneur
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		tileName = p_tileName;

		// Construction du pont de correspondance
		bridge = new CorrespondanceGifDec();
		bridge.init();

		// On ajoute le mouseListene pour d�tecter les actions � la souris
		this.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				if(tile != null){
					if(MouseEvent.BUTTON1 == e.getButton()){
						// On r�initialise les points pour la nouvelle s�lection
						startPoint = null;
						stopPoint = null;
	
						int x = 16 * ((int) e.getX() / 16);
						int y = 16 * ((int) e.getY() / 16);
						if((x >= 0 && x <= tileWidth - 16) && (y >= 0 && y <= tileHeight - 16)){
							if(startPoint == null){
								startPoint = new Point(x,y);
							}else{
								startPoint.setLocation(x, y);
							}
							repaint();
						}
					}else if (MouseEvent.BUTTON3 == e.getButton()){
						// Click droit
					}
				}
			}
			public void mouseClicked(MouseEvent e) {
//				if(tile != null){
//					if(MouseEvent.BUTTON1 == e.getButton()){
//						// Click gauche
//						int x = 16 * ((int) e.getX() / 16);
//						int y = 16 * ((int) e.getY() / 16);
//						if((x > 0 && x <= tileWidth - 16) && (y > 0 && y <= tileHeight - 16)){
//							startPoint = new Point(x,y);
//							stopPoint = null;
//						}
//					}else if (MouseEvent.BUTTON3 == e.getButton()){
//						// Click droit
//						startPoint = null;
//						stopPoint = null;
//					}
//					repaint();
//				}
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent e) {
				if(tile != null){
					if(MouseEvent.BUTTON1 == e.getButton()){
						// Click gauche
						int x = 16 * ((int) e.getX() / 16);
						int y = 16 * ((int) e.getY() / 16);
						if(x < 0){x = 0;}else if (x >= tileWidth){x = tileWidth - 16;}
						if(y < 0){y = 0;}else if (y >= tileHeight){y = tileHeight - 16;}
						if(stopPoint == null){
							stopPoint = new Point(x,y);
						}else{
							stopPoint.setLocation(x, y);
						}				
						
						if(startPoint != null){
							// On trie les points si le startPoint est valide
							sortPoints();
							// On construit la nouvelle s�lection
							buildSelection();
						}
						repaint();
						
					}else if (MouseEvent.BUTTON3 == e.getButton()){
						// Click droit
	
					}
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent e) {
				if(tile != null){
					int x = 16 * ((int) e.getX() / 16);
					int y = 16 * ((int) e.getY() / 16);
					if(x < 0){x = 0;}else if (x >= tileWidth){x = tileWidth - 16;}
					if(y < 0){y = 0;}else if (y >= tileHeight){y = tileHeight - 16;}
					if(stopPoint == null){
						stopPoint = new Point(x,y);
					}else{
						stopPoint.setLocation(x, y);
					}
					// Repaint du Tile
					repaint();
				}
			}
			public void mouseMoved(MouseEvent arg0) {}
		});
	}

	/**
	 * M�thode de changement du tile
	 * @param p_url chemin de l'image du Tile � charger
	 * @author Drakulo
	 * @throws ZeditorException
	 */
	public void changeTile(String p_name) throws ZeditorException {
		// On renomme le tile
		tileName = p_name;

		// On supprime les points de s�lection pr�c�dents
		startPoint = null;
		stopPoint = null;
		
		// On r�cup�re le chemin param�tr� (si il est param�tr�)
		String path = OptionHelper.loadOption(Options.TILES_PATH.getValue());
		if ("".equals(path) || path == null) {
			path = "tiles/";
		}

		// On teste l'existance
		File f = new File(path);
		if (!f.exists()) {
			throw new TileSetException("Le chemin sp�cifi� pour la banque de tiles n'existe pas");
		}

		// On charge l'image
		ImageIcon icon = new ImageIcon(path + p_name + ".gif");
	
		// On v�rifie la largeur
		if(icon.getIconWidth() != 320){
			tile = null;
			throw new TileSetException("Le fichier de Tiles est trop large :" + icon.getIconWidth() + "px au lieu de 320px.");
		}
		
		tile = icon.getImage();
		// R�cup�ration de la taille de l'image
		tileWidth = icon.getIconWidth();
		tileHeight = icon.getIconHeight();
		// Si la hauteur n'est pas un multiple de 16, on tronque la taille au multiple inf�rieur
		if(icon.getIconHeight() % 16 != 0){
			tileHeight -= icon.getIconHeight() % 16;
		}

		
		// On repaint pour afficher le r�sultat
		repaint();
	}

	/**
	 * M�thode de chargement dynamique des tiles pr�sents dans le dossier d�fini
	 * des Tiles.
	 * @return Object[] : Un tableau de String contenant le nom des fichiers images (sans l'extension)
	 * @throws ZeditorException
	 * @author Drakulo
	 */
	public static Object[] getTiles() throws ZeditorException {
		// R�cup�ration des fichiers du dossier de tiles
		File[] files = null;
		List<String> list = new ArrayList<String>();
		String path = OptionHelper.loadOption(Options.TILES_PATH.getValue());
		if ("".equals(path) || path == null) {
			path = "tiles/";
		}
		File dir = new File(path);
		if (!dir.exists()) {
			throw new TileSetException("Le chemin sp�cifi� pour la banque de tiles n'existe pas");
		}
		files = dir.listFiles();
		if (files == null) {
			throw new TileSetException("Le dossier param�tr� est vide");
		}
		int i = 0;
		for (File file : files) {
			String fileName = file.getName();
			// On ajoute � la liste que si l'�l�ment est une image GIF
			if(fileName.endsWith(".gif") || fileName.endsWith(".GIF")){
				list.add(fileName.substring(0, (fileName.length() - 4)));
			}
			i++;
		}
		if (list.isEmpty()) {
			throw new TileSetException(
			"Le dossier param�tr� ne contient aucun tileSet");
		}
		return list.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public void paint(Graphics g){
		// Redimentionnement de la taille du TileSet pour le Scroll automatique
		if(tile != null){
			setPreferredSize(new Dimension(tile.getWidth(null), tile.getHeight(null)));
		}else{
			setPreferredSize(new Dimension(0, 0));
		}
		revalidate();
		// Repaint
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(tile, 0, 0, null);
		if(tile != null && bridge != null){
			// Selon le param�trage :
			if(Boolean.parseBoolean(OptionHelper.loadOption(Options.SHOW_TILES_UNMAPPED.getValue()))){
				showUnmappedTiles(g2d);
			}
			if(Boolean.parseBoolean(OptionHelper.loadOption(Options.SHOW_TILES_GRID.getValue()))){
				showGrid(g2d);
			}
		}
		// On dessine un cadre autour des tuiles s�lectionn�es en dernier pour qu'il soit au dessus
		if(stopPoint == null){
			// Le point stopDrag est null donc on met le cadre sur une simple case
			drawRectangle(g2d, Color.black, Color.white, startPoint, startPoint);
		}else{
			// Le point de stopDrag n'est pas null donc on doit tracer un rectangle sur plusieurs cases
			drawRectangle(g2d, Color.black, Color.white, startPoint, stopPoint);
		}
	}

	/**	 * M�thode de trac� du cadre autour des tuiles s�lectionn�es
	 * @param g : Graphics sur lequel on va dessiner
	 * @param outer : Couleur de l'ext�rieur de cadre
	 * @param inner : Couleur de l'int�rieur de cadre
	 * @param startPoint : Point de d�part du cadre
	 * @param stopPoint : Point de fin du cadre
	 * @author Drakulo
	 */
	private void drawRectangle(Graphics g, Color outer, Color inner, Point startPoint, Point stopPoint){
		if(startPoint != null && stopPoint != null){
			int xDep, yDep, xFin, yFin;
			if(startPoint.getX() < stopPoint.getX()){
				// On veut s�lectionner de gauche � droite
				xDep = (int) startPoint.getX();
				xFin = (int) stopPoint.getX();
			}else{
				// On veut s�lectionner de droite � gauche
				xDep = (int) stopPoint.getX();
				xFin = (int) startPoint.getX();
			}

			if(startPoint.getY() < stopPoint.getY()){
				// On veut s�lectionner de haut en bas
				yDep = (int) startPoint.getY();
				yFin = (int) stopPoint.getY();
			}else{
				// On veut s�lectionner de bas en haut
				yDep = (int) stopPoint.getY();
				yFin = (int) startPoint.getY();
			}

			g.setColor(outer);
			g.drawRect(xDep, yDep, xFin-xDep+16, yFin-yDep+16);
			g.setColor(inner);
			g.drawRect(xDep+1, yDep+1, xFin-xDep+14, yFin-yDep+14);
			g.setColor(outer);
			g.drawRect(xDep+2, yDep+2, xFin-xDep+12, yFin-yDep+12);
		}
	}

	/**
	 * M�thode priv�e d'affichage des tuiles non mapp�es
	 * @param g le Graphics concern�
	 * @author Drakulo
	 */
	private void showUnmappedTiles(Graphics g){
		for(int j = 0; j < tile.getHeight(null); j+=16){
			for(int i = 0; i < tile.getWidth(null); i+=16){
				if(bridge.getCorrespondance(tileName, i, j) < 0){
					g.setColor(Color.red);

					// Lignes obliques montantes de gauche � droite (/)
					g.drawLine(i+4, j, i, j+4);
					g.drawLine(i+8, j, i, j+8);
					g.drawLine(i+12, j, i, j+12);
					g.drawLine(i+16, j, i, j+16);
					g.drawLine(i+16, j+4, i+4, j+16);
					g.drawLine(i+16, j+8, i+8, j+16);
					g.drawLine(i+16, j+12, i+12, j+16);

					// Lignes obliques descendantes de gauche � droite (\)
					g.drawLine(i, j, i+16, j+16);
					g.drawLine(i+4, j, i+16, j+12);
					g.drawLine(i+8, j, i+16, j+8);
					g.drawLine(i+12, j, i+16, j+4);
					g.drawLine(i, j+4, i+12, j+16);
					g.drawLine(i, j+8, i+8, j+16);
					g.drawLine(i, j+12, i+4, j+16);

				}
			}
		}
	}

	/**
	 * M�thode priv�e d'affichage de la grille sur le TileSet
	 * @param g le Graphics concern�
	 * @author Drakulo
	 */
	private void showGrid(Graphics g){
		g.setColor(Color.gray);
		// Grille horitontale
		for(int i = 0; i < tile.getHeight(null); i+=16){
			g.drawLine(0, i, tile.getWidth(null), i);
		}

		// Grille verticale
		for(int j = 0; j < tile.getWidth(null); j+=16){
			g.drawLine(j, 0, j, tile.getHeight(null));
		}
	}
	
	/**
	 * M�thode priv�e de tri des Points de s�lection. Le point en haut � gauche devient 
	 * le point de d�part et le point en bas � droite devient le point de fin.
	 */
	private void sortPoints(){
		// A partir des points de d�but et de fin, on recr�e deux nouveaux 
		// points afin d'avoir le point de d�but en haut � gauche et le point 
		// de fin en bas � droite
		//System.out.println("AVANT --");
		//System.out.println("Start " + startPoint.x + "x : " + startPoint.y + "y");
		//System.out.println("Stop " + stopPoint.x + "x : " + stopPoint.y + "y");
		Point temp;
		if(startPoint.x > stopPoint.x){
			if(startPoint.y > stopPoint.y){
				temp = new Point(startPoint.x, startPoint.y);
				startPoint.setLocation(stopPoint.x, stopPoint.y);
				stopPoint.setLocation(temp);
			}else{
				temp = new Point(startPoint.x, stopPoint.y);
				startPoint.setLocation(stopPoint.x, startPoint.y);
				stopPoint.setLocation(temp);
			}
		}else{
			if(startPoint.y > stopPoint.y){
				temp = new Point(stopPoint.x, startPoint.y);
				startPoint.setLocation(startPoint.x, stopPoint.y);
				stopPoint.setLocation(temp);
			}
		}
		//System.out.println("APRES --");
		//System.out.println("Start " + startPoint.x + "x : " + startPoint.y + "y");
		//System.out.println("Stop " + stopPoint.x + "x : " + stopPoint.y + "y");
	}
	
	/**
	 * M�thode priv�e de construction de la s�lection
	 */
	private void buildSelection(){
		int startX, startY, stopX, stopY, width, height;
		width = 0;
		height = 0;
		List<Integer> list = new ArrayList<Integer>();
		
		startX = startPoint.x;
		startY = startPoint.y;
		stopX = stopPoint.x;
		stopY = stopPoint.y;
		//String buffer = "";
		
		for(int i = startY; i <= stopY; i+=16){
			//buffer = "|";
			for(int j = startX; j <= stopX; j+= 16){
				//buffer += bridge.getCorrespondance(tileName, j, i);
				//buffer += "|";
				list.add(bridge.getCorrespondance(tileName, j, i));
				// On ne compte la largeur que pour la premi�re ligne
				if(height == 0){
					width ++;
				}
			}
			//System.out.println(buffer);
			height ++;
		}
		currentSelection = new TileSelection(width, height, list);
	}
	
	/**
	 * Getter de la s�lection courante sur le TileSet
	 * @return la s�lection courante
	 */
	public TileSelection getCurrentSelection() {
		return currentSelection;
	}
}