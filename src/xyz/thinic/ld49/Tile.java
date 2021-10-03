package xyz.thinic.ld49;

import java.awt.Color;

import xyz.thinic.ld49.entity.Entity;

public class Tile {

    public static final int TYPE_FLOOR = 0; 
    public static final int TYPE_WALL = 1; 
    public static final int TYPE_PLATE_1 = 2; 
    public static final int TYPE_GATE_1 = 3; 
    public static final int TYPE_PLATE_2 = 4; 
    public static final int TYPE_GATE_2 = 5; 
    public static final int TYPE_PLATE_3 = 6; 
    public static final int TYPE_GATE_3 = 7; 
    
    public int type = TYPE_FLOOR; 
    public float offset = (float) (Math.random() * 11) - 5; 
    public float dOffset = Math.random() > 0.5 ? 1 : -1; 
    public boolean locked = false; 
    
    public final Level level; 
    public final int x, y; 
    
    private Entity entity = null; 
    
    public Tile(Level level, int x, int y) {
        this.level = level; 
        this.x = x; 
        this.y = y; 
    }
    
    public boolean isWall() {
        if (type == TYPE_WALL) return true; 
        if (type == TYPE_GATE_1 && locked) return true; 
        if (type == TYPE_GATE_2 && locked) return true; 
        if (type == TYPE_GATE_3 && locked) return true; 
        return false; 
    }
    
    protected void setEntity(Entity e) {
//        if (entity != null) {
//            entity.onTileSet(null); 
//        }
        entity = e; 
        if (entity != null) {
            entity.onTileSet(this); 
//            offset += 50; 
        }
    }
    
    public Entity getEntity() {
        return entity; 
    }
    
    public void update(Keyboard input) {
        if (offset > 5) dOffset = -1; 
        if (offset < -5) dOffset = 1; 
        if (offset < -20) dOffset = 100; 
        
        offset += 0.25f * dOffset; 
        
        if (offset > 20) {
            offset = (float) (Math.random() * 11) - 5; 
            dOffset = -1; 
        }
    }
    
    public void render(Renderer r, float x, float y) {
        r.setTile(this); 
                
        if (type == TYPE_FLOOR) {
            r.drawImage(SpriteSheet.TILESET.get(0, 0), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else if (type == TYPE_WALL) {
            r.drawImage(SpriteSheet.TILESET.get(1, 1), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(1, 0), (x) * Game.TILE_SIZE, (y - 1) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else if (type == TYPE_PLATE_1) {
            r.drawImage(SpriteSheet.TILESET.get(0, 7), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else if (type == TYPE_GATE_1) {
            if (!locked) {
                r.drawImage(SpriteSheet.TILESET.get(0, 6), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            }
            else {
                r.drawImage(SpriteSheet.TILESET.get(0, 4), (x) * Game.TILE_SIZE, (y - 1) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
                r.drawImage(SpriteSheet.TILESET.get(0, 5), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            }
        }
        else if (type == TYPE_PLATE_2) {
            r.drawImage(SpriteSheet.TILESET.get(1, 7), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else if (type == TYPE_GATE_2) {
            if (!locked) {
                r.drawImage(SpriteSheet.TILESET.get(1, 6), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            }
            else {
                r.drawImage(SpriteSheet.TILESET.get(1, 4), (x) * Game.TILE_SIZE, (y - 1) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
                r.drawImage(SpriteSheet.TILESET.get(1, 5), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            }
        }
        else if (type == TYPE_PLATE_3) {
            r.drawImage(SpriteSheet.TILESET.get(2, 7), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else if (type == TYPE_GATE_3) {
            if (!locked) {
                r.drawImage(SpriteSheet.TILESET.get(2, 6), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            }
            else {
                r.drawImage(SpriteSheet.TILESET.get(2, 4), (x) * Game.TILE_SIZE, (y - 1) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
                r.drawImage(SpriteSheet.TILESET.get(2, 5), (x) * Game.TILE_SIZE, (y) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            }
        }
    }
    
}
