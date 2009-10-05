package zildo.fwk.filter;

import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.monde.map.Point;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;

public class ZoomFilter extends ScreenFilter {

	
	public boolean renderFilter()
	{
		focusOnZildo();
				
		GL11.glDisable(GL11.GL_BLEND);

		return true;
	}
	
	protected void focusOnZildo() {
		// Focus camera on Zildo, and zoom according to the 'fadeLevel'
		PersoZildo zildo=EngineZildo.persoManagement.getZildo();
		Point zildoPos=new Point(zildo.getScrX(), zildo.getScrY());
		SpriteModel spr=zildo.getSprModel();
		zildoPos.add(spr.getTaille_x() / 2, spr.getTaille_y() / 2);
		ClientEngineZildo.openGLGestion.setZoomPosition(zildoPos);
		float z=2.0f * (float) Math.sin(getFadeLevel() * (0.25f*Math.PI / 256.0f));
		ClientEngineZildo.openGLGestion.setZ(z);
		//EngineZildo.getOpenGLGestion().setZ((float) Math.sin(getFadeLevel() * (0.5f*Math.PI / 256.0f)));
	}
	
	/**
	 * Re-initialize z coordinate
	 */
	public void doOnInactive() {
		ClientEngineZildo.openGLGestion.setZ(0);
	}	
}
