package zildo.fwk.opengl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import zildo.client.ClientEngineZildo;
import zildo.monde.map.Point;
import zildo.server.EngineZildo;

public class OpenGLZildo extends OpenGLGestion {

	String windowTitle="Zildo OpenGL";

    private ClientEngineZildo clientEngineZildo;

    private float z;
    private float xx;
    private Point zoomPosition;
    private boolean pressed=false;
    
    public static final byte[] icon = { 71, -110, 88, -1, 75, -113, 84, -1, 84, -106, 98, -1, 54, 89, 42, -1, 48, 30, 0, -1, 110, 51, 26,
        -1, -101, 61, 47, -1, 113, 22, 12, -1, 80, 29, 12, -1, 77, 78, 49, -1, 80, 125, 86, -1, 94, -122, 103, -1, 114, -117, 119, -1,
        -37, -1, -20, -1, -115, -56, -94, -1, 61, -119, 73, -1, 97, -113, 96, -1, 94, 122, 77, -1, 55, 71, 35, -1, 49, 23, 0, -1, -114,
        59, 29, -1, -67, 71, 51, -1, -54, 56, 38, -1, -80, 41, 18, -1, -26, -121, 101, -1, 93, 55, 19, -1, 14, 17, 0, -1, 115, 123, 87,
        -1, -18, -12, -25, -1, -40, -14, -32, -1, -119, -68, -102, -1, 67, -115, 79, -1, 79, 93, 60, -1, 40, 17, 0, -1, 40, 0, 0, -1,
        101, 9, 5, -1, -49, 72, 60, -1, -65, 45, 24, -1, -67, 41, 4, -1, -49, 84, 24, -1, -51, 120, 40, -1, 105, 56, 0, -1, -59, -87,
        73, -1, 95, 79, 17, -1, -75, -79, -109, -1, -62, -43, -79, -1, 68, 115, 65, -1, 78, -107, 82, -1, 91, 46, 29, -1, -87, 80, 67,
        -1, -122, 29, 16, -1, 121, 15, 9, -1, 114, 7, 0, -1, -84, 68, 34, -1, -35, -127, 70, -1, -120, 65, 11, -1, 105, 53, 0, -1, -65,
        -102, 46, -1, -1, -29, 108, -1, -26, -52, 124, -1, 26, 18, 0, -1, -49, -29, -62, -1, 71, 121, 78, -1, 75, -104, 77, -1, -127,
        50, 47, -1, -83, 53, 48, -1, -102, 33, 21, -1, 112, 25, 14, -1, 117, 59, 44, -1, 108, 55, 18, -1, 92, 48, 5, -1, -105, 116, 45,
        -1, -62, -94, 85, -1, -2, -28, -112, -1, 127, 103, 20, -1, 95, 73, 18, -1, -99, -106, 122, -1, 122, -109, -128, -1, 81, -117,
        108, -1, 66, -109, 74, -1, 112, 57, 54, -1, -121, 49, 54, -1, 104, 18, 19, -1, 50, 0, 0, -1, -86, 113, 105, -1, -79, 126, 99,
        -1, 66, 24, 0, -1, -52, -87, 118, -1, 54, 25, 0, -1, 57, 34, 0, -1, 94, 70, 26, -1, 28, 8, 0, -1, -28, -32, -57, -1, 86, 115,
        95, -1, 72, -128, 98, -1, 78, -101, 90, -1, 68, 53, 39, -1, 69, 33, 29, -1, 32, 2, 0, -1, 21, 0, 0, -1, 114, 66, 44, -1, -46,
        -90, -117, -1, 73, 34, 0, -1, -93, -125, 85, -1, 107, 77, 40, -1, 109, 82, 58, -1, -119, 112, 101, -1, 67, 51, 36, -1, 106,
        110, 89, -1, 62, 96, 75, -1, 103, -91, -124, -1, 72, -110, 89, -1, -115, -83, -116, -1, 34, 54, 24, -1, 73, 100, 66, -1, 7, 2,
        0, -1, 122, 83, 36, -1, -107, 113, 72, -1, 69, 31, 0, -1, -98, 122, 70, -1, -111, 112, 63, -1, -111, 115, 79, -1, -100, 125,
        112, -1, -109, -124, 120, -1, 0, 11, 0, -1, 99, -119, 111, -1, 96, -98, 121, -1, 74, -110, 93, -1, -116, -51, -101, -1, 80,
        -113, 85, -1, 57, 124, 62, -1, 94, 117, 56, -1, 57, 32, 0, -1, 74, 47, 20, -1, -113, 109, 85, -1, 96, 60, 22, -1, -63, -96,
        109, -1, -118, 107, 59, -1, 114, 83, 51, -1, 78, 69, 47, -1, 127, -105, 125, -1, -80, -33, -71, -1, 103, -86, 122, -1, 59,
        -127, 75, -1, 124, -56, -113, -1, 65, -115, 76, -1, 93, -88, 96, -1, -112, -74, -121, -1, 24, 23, 20, -1, 8, 0, 17, -1, 68, 44,
        75, -1, 79, 50, 58, -1, 68, 40, 17, -1, 66, 42, 2, -1, 54, 29, 6, -1, 119, 118, 87, -1, -85, -51, -85, -1, -89, -36, -86, -1,
        92, -94, 100, -1, 81, -105, 94, -1, 127, -58, -110, -1, 67, -119, 80, -1, 110, -83, 108, -1, 84, -127, 111, -1, 23, 47, 99, -1,
        66, 71, -98, -1, 31, 19, 118, -1, 25, 7, 70, -1, 34, 15, 30, -1, 108, 94, 76, -1, 112, 98, 75, -1, 1, 9, 0, -1, -83, -43, -87,
        -1, -96, -36, -100, -1, 97, -86, 97, -1, 66, -119, 73, -1, -123, -58, -111, -1, 81, -119, 89, -1, 114, -106, 110, -1, 71, 99,
        101, -1, 1, 19, 81, -1, 74, 80, -76, -1, 84, 75, -53, -1, 50, 37, -107, -1, 42, 31, 97, -1, 10, 1, 29, -1, 16, 13, 17, -1,
        -111, -97, -114, -1, -70, -31, -70, -1, -90, -34, -91, -1, 91, -93, 90, -1, 73, -110, 77, -1, 122, -69, -126, -1, 106, -107,
        107, -1, 56, 72, 51, -1, 24, 25, 35, -1, 30, 25, 75, -1, 44, 38, 126, -1, 75, 69, -64, -1, 73, 69, -61, -1, 41, 36, -120, -1,
        61, 56, 117, -1, 21, 10, 42, -1, 75, 85, 81, -1, 111, -107, 114, -1, -122, -71, -119, -1, 107, -83, 111, -1, 81, -100, 86, -1,
        -67, -7, -60, -1, -98, -56, -95, -1, 0, 0, 0, -1, 38, 26, 41, -1, 70, 45, 101, -1, 63, 50, -117, -1, 54, 51, -90, -1, 40, 46,
        -99, -1, 52, 54, -112, -1, 59, 51, 111, -1, 94, 68, 113, -1, 22, 20, 26, -1, -60, -26, -58, -1, -59, -15, -57, -1, -68, -7,
        -64, -1, 100, -85, 108, -1, -101, -46, -96, -1, -83, -47, -83, -1, 63, 80, 61, -1, 42, 28, 41, -1, 53, 21, 66, -1, 61, 37, 110,
        -1, 71, 54, -107, -1, 67, 55, -105, -1, 65, 54, -126, -1, 62, 45, 99, -1, 29, 6, 50, -1, 6, 6, 14, -1, 113, -114, 115, -1, -77,
        -36, -73, -1, -82, -27, -77, -1, 103, -88, 113, -1, -88, -36, -84, -1, -65, -26, -63, -1, -105, -81, -103, -1, 1, 5, 0, -1, 12,
        0, 13, -1, 31, 6, 44, -1, 43, 5, 61, -1, 44, 0, 57, -1, 48, 12, 55, -1, 9, 0, 17, -1, 1, 1, 17, -1, 0, 0, 0, -1, 16, 46, 20,
        -1, -69, -27, -63, -1, -88, -37, -80, -1, 110, -83, 124, -1 };
    
    public OpenGLZildo() {
    	
    }
    
	public OpenGLZildo(boolean fullscreen) {
		super(fullscreen);
    	
		z=0.0f;
	}
	
    public void setClientEngineZildo(ClientEngineZildo p_engineZildo) {
    	clientEngineZildo=p_engineZildo;
    }
    
    protected void mainloopExt() {

        // Pour test
        if(Keyboard.isKeyDown(Keyboard.KEY_ADD)) {       // '+'
            z+=0.1f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {       // '-'
            z-=0.1f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_MULTIPLY)) {
        	pressed=true;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_MULTIPLY) && pressed) {
        	pressed=false;
        }
    	EngineZildo.extraSpeed=1;
    	if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
        	EngineZildo.extraSpeed=2;
        }
        xx+=0.5f / 8.0f;
    }
    
    public void render(boolean p_clientReady) {

   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer

    	GL11.glLoadIdentity(); // Reset The Projection Matrix

    	// invert the y axis, down is positive
        float zz=(float) (z *5.0f);
        if (zz != 0.0f) {
        	GL11.glTranslatef(-zoomPosition.getX()*zz, zoomPosition.getY()*zz,0.0f);
        }
    	GL11.glScalef(1+zz , -1-zz, 1);
    	if (ClientEngineZildo.filterCommand != null) {
    		ClientEngineZildo.filterCommand.doPreFilter();
    	}
    	
		clientEngineZildo.renderFrame(awt);
    	if (!p_clientReady) {
    		//ClientEngineZildo.ortho.drawText(0,4,"Awaiting server...", new Vector3f(1,1,1));
    		clientEngineZildo.renderMenu();
    	}
    	
    	if (ClientEngineZildo.filterCommand != null) {
    		ClientEngineZildo.filterCommand.doFilter();
    		ClientEngineZildo.filterCommand.doPostFilter();
    	}
    	
       	if (framerate != 0) {
       		Display.sync(framerate);
       	}
       	
        if (!awt) {
        	Display.update();
        }
    }
    
    public void setZ(float p_z) {
    	z=p_z;
    }

	public void setZoomPosition(Point zoomPosition) {
		this.zoomPosition = zoomPosition;
	}
	
    protected void cleanUpExt() {
    	clientEngineZildo.cleanUp();
    }
}
