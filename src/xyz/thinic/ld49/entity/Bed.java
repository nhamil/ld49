package xyz.thinic.ld49.entity;

import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.SpriteSheet;

public class Bed extends Entity {

    public Bed() {
        winOnPossess = true; 
        turnsToDeath = Integer.MAX_VALUE; 
    }
    
    public void onPossessed() {
        
    }
    
    public void renderObject(Renderer r) {
        renderImage(r, SpriteSheet.TILESET.get(3, 0), 0, 0);
    }
    
}
