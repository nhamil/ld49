package xyz.thinic.ld49.entity;

import java.util.List;

import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.SpriteSheet;

public class Item extends Entity {

    public int item = (int) (Math.random() * 14); 
    
    public Item(List<Entity> otherEntities) {
        while (itemIsUsed(otherEntities)) item = (int) (Math.random() * 14); 
    }
    
    private boolean itemIsUsed(List<Entity> entities) {
        for (Entity e : entities) {
            if (e != this && e != null && (e instanceof Item) && ((Item) e).item == item) return true; 
        }
        return false; 
    }
    
    public void renderObject(Renderer r) {
        renderImage(r, SpriteSheet.TILESET.get(6, item), 0, -6);
    }
    
}
