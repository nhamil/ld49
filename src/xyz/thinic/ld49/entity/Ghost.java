package xyz.thinic.ld49.entity;

import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.SpriteSheet;

public class Ghost extends Entity {

    public Ghost() {
        possessed = true; 
        killOnLeave = true; 
    }
    
    public void renderObject(Renderer r) {
        renderImage(r, SpriteSheet.TILESET.get(2, 0), 0, -6);
    }
    
}
