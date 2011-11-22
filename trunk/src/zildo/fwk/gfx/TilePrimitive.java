/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk.gfx;

import org.lwjgl.opengl.GL11;

import zildo.fwk.opengl.OpenGLStuff;
import zildo.fwk.opengl.compatibility.VBOBuffers;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;

/**
 * Class describing the TileEngine main element :<br/>
 * <ul>
 * <li>set of vertices</li>
 * <li>set of indices</li>
 * <li>set of normals (all the same)</li>
 * <li>set of textures coordinates</li>
 * </ul>
 * @author tchegito
 */

public class TilePrimitive extends OpenGLStuff {

    // Class variables
    protected int nPoints;
    protected int nIndices;
    private boolean isLock;

    protected VBOBuffers bufs;
    
    private int textureSizeX = 256;
    private int textureSizeY = 256;

    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////

    public TilePrimitive() { // Should never been called
        nPoints = 0;
        nIndices = 0;
    }

    public TilePrimitive(int numPoints) {
        initialize(numPoints);
    }

    private void initialize(int numPoints) {
        // Initialize VBO IDs
        bufs=vbo.create(numPoints);
        
        nPoints = 0;
        nIndices = 0;

        // Generate all indices at primitve instanciation (it never change)
        generateAllIndices();
    }

    public TilePrimitive(int numPoints, int numIndices, int texSizeX, int texSizeY) {
        textureSizeX = texSizeX;
        textureSizeY = texSizeY;
        initialize(numPoints);
    }

    public void cleanUp() {
       	vbo.cleanUp(bufs);
        nPoints = 0;
        nIndices = 0;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // startInitialization
    // /////////////////////////////////////////////////////////////////////////////////////
    // Lock VertexBuffer to gain access to data
    // /////////////////////////////////////////////////////////////////////////////////////
    public void startInitialization() {
    	nPoints = 0;
    	nIndices = 0;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // endInitialization
    // /////////////////////////////////////////////////////////////////////////////////////
    public void endInitialization() {
        vbo.endInitialization(bufs);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // renderPartial
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN : startingQuad, number of quads to render
    // /////////////////////////////////////////////////////////////////////////////////////
    // Ask OpenGL to render quad from this mesh, from a position to another
    // /////////////////////////////////////////////////////////////////////////////////////
    void renderPartial(int startingQuad, int nbQuadsToRender) {
        int position = bufs.indices.position();
        int saveNIndices = nIndices;
        int limit = bufs.indices.limit();

        bufs.indices.position(startingQuad * 6);
        nIndices = nbQuadsToRender * 6 + startingQuad * 6;
        render();
        bufs.indices.position(position);
        nIndices = saveNIndices;
        bufs.indices.limit(limit);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // render
    // /////////////////////////////////////////////////////////////////////////////////////
    // Ask OpenGL to render every quad from this mesh
    // /////////////////////////////////////////////////////////////////////////////////////
    public void render() {

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        vbo.draw(bufs);

        // Le buffer d'indices contient les indices pour 4096 tiles. On doit le limiter au nombre de tiles
        // r�ellement utilis�.
        bufs.indices.limit(nIndices);
        GL11.glDrawElements(GL11.GL_TRIANGLES, bufs.indices);

    }

    /**
     * Add standard tile : 16x16
     * @return position in bufs.vertices buffer for the added tile's first vertex
     */
    public int addTile(int x, int y, float u, float v) {
        int nTileToReturn = nPoints;
        addTileSized(x, y, u, v, 16, 16);
        return nTileToReturn;
    }

    private void putTileSized(float x, float y, float sizeX, float sizeY, float xTex, float yTex) {
        // 4 bufs.vertices
        if (bufs.vertices.position() == bufs.vertices.limit()) {
            // On rajoute une place
            bufs.vertices.limit(bufs.vertices.position() + 3 * 4);
            bufs.textures.limit(bufs.textures.position() + 2 * 4);
        }
        float pixSizeX=Math.abs(sizeX);
        float pixSizeY=Math.abs(sizeY);
        float texStartX=xTex;
        float texStartY=yTex;
        if (sizeX < 0) {
        	texStartX-=sizeX;
        }
        if (sizeY < 0) {
        	texStartY-=sizeY;
        }
        for (int i = 0; i < 4; i++) {
            bufs.vertices.put(x + pixSizeX * (i % 2)); // x
            bufs.vertices.put(y + pixSizeY * (i / 2)); // y
            bufs.vertices.put(0.0f); // z

            // Get right tile-texture
            float texPosX = texStartX + sizeX * (i % 2);
            float texPosY = texStartY + sizeY * (i / 2);

            bufs.textures.put(texPosX / textureSizeX);
            bufs.textures.put(texPosY / textureSizeY);
        }
    }

    // Return the quad position in Vertex Buffer
    protected int addTileSized(int x, int y, float xTex, float yTex, int sizeX, int sizeY) {
        putTileSized(x, y, sizeX, sizeY, xTex, yTex);

        nPoints += 4;
        nIndices += 6;

        return nPoints - 4;
    }

    public boolean isLock() {
        return isLock;
    }

    /**
     *  Move a tile and reset its texture (don't change size)<br/>
     * {@link #startInitialization()} should be called first.
     * @param x
     * @param y
     * @param u
     * @param v
     * @param reverse TODO
     */
    public void updateTile(float x, float y, float u, float v, Reverse reverse) {
        // Get size
        int vBufferPos = bufs.vertices.position(); // - 3*4;
		int tBufferPos = bufs.textures.position(); // - 2*4;

        if (bufs.vertices.limit() <= vBufferPos) {
            // On rajoute une place
            bufs.vertices.limit(vBufferPos + 3 * 4);
        }
		if (bufs.textures.limit() <= tBufferPos) {
			bufs.textures.limit(tBufferPos + 2 * 4);
		}
        
        float sizeX = bufs.vertices.get(vBufferPos + 3) - bufs.vertices.get(vBufferPos);
        float sizeY = bufs.vertices.get(vBufferPos + 3 * 2 + 1) - bufs.vertices.get(vBufferPos + 1);

        if (sizeX == 0) {
        	sizeX=16;
        	sizeY=16;
        }
        nPoints += 4;
        nIndices += 6;
		
		int revX = reverse.isHorizontal() ? -1 : 1;
		int revY = reverse.isVertical() ? -1 : 1;
		
        // Move tile
        putTileSized(x, y, sizeX * revX, sizeY * revY, u, v);

    }

    void removeTile(int numTile) {
    }

    // Generate indices for maximum tile authorized.
    // We can do this once for all, because every tiles is a quad made by 2 triangles
    // where indices are like this :
    // (v1,v2,v3) - (v2,v4,v3)
    void generateAllIndices() {
    	int numIndices=bufs.indices.limit() / 3;
        // 3 Indices
        for (int i = 0; i < (numIndices / 6); i++) {
            // Tile's first triangle
            bufs.indices.put(i * 4).put(i * 4 + 1).put(i * 4 + 2);
            // Tile's second triangle
            bufs.indices.put(i * 4 + 1).put(i * 4 + 3).put(i * 4 + 2);

            // Two bufs.normals oriented accross the screen (0,0,-1)
            bufs.normals.put(0).put(i).put(1);
            bufs.normals.put(0).put(0).put(1);
        }
    }
    
    public int getNPoints() {
        return nPoints;
    }

    public void setNPoints(int points) {
        nPoints = points;
    }

    public int getNIndices() {
        return nIndices;
    }

    public void setNIndices(int indices) {
        nIndices = indices;
    }
}