package xyz.thinic.ld49;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import xyz.thinic.ld49.entity.Bed;
import xyz.thinic.ld49.entity.Entity;
import xyz.thinic.ld49.entity.Ghost;
import xyz.thinic.ld49.entity.Item;

public class Level {

    public String filename; 
    private int width; 
    private int height; 
    private Tile[] tiles; 
    private List<Entity> entities = new ArrayList<>(); 
    
    public static final int STATUS_ONGOING = 0; 
    public static final int STATUS_WIN = 1; 
    public static final int STATUS_LOSE = 2; 
    
    public int status = STATUS_ONGOING; 
    
    public Level(int width, int height) {
        this.width = width; 
        this.height = height; 
        this.tiles = new Tile[width * height]; 
        for (int i = 0; i < width * height; i++) {
            this.tiles[i] = new Tile(this, i % width, i / width); 
        }
    }
    
    public Level(String filename) {
        this.filename = filename; 
        try {
            BufferedImage level = ImageIO.read(Level.class.getResourceAsStream("/levels/" + filename + ".png")); 
            this.width = level.getWidth(); 
            this.height = level.getHeight(); 
            this.tiles = new Tile[width * height]; 
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = new Tile(this, x, y); 
                    int rgb = level.getRGB(x, y) & 0xFFFFFF; 
                    
                    switch (rgb) {
                    case 0xffffff: // floor 
                        break; 
                    case 0x404040: // wall 
                        tile.type = Tile.TYPE_WALL; 
                        break; 
                    case 0x7F6A00: // plate  
                        tile.type = Tile.TYPE_PLATE_3; 
                        break; 
                    case 0xFFD800: // gate 
                        tile.type = Tile.TYPE_GATE_3; 
                        tile.locked = true; 
                        break; 
                    case 0x7F0037: // plate  
                        tile.type = Tile.TYPE_PLATE_2; 
                        break; 
                    case 0xFF006E: // gate 
                        tile.type = Tile.TYPE_GATE_2; 
                        tile.locked = true; 
                        break; 
                    case 0x267F00: // plate  
                        tile.type = Tile.TYPE_PLATE_1; 
                        break; 
                    case 0x4CFF00: // gate 
                        tile.type = Tile.TYPE_GATE_1; 
                        tile.locked = true; 
                        break; 
                    case 0xff0000: // bed 
                        addNewEntity(tile, new Bed()); 
                        break; 
                    case 0: 
                        tile = null; 
                        break; 
                    default: 
                        if ((rgb & 0xFFFF00) == 0x7f3300) {
                            Entity e = new Item(entities); 
                            e.turnsToDeath = rgb & 0xFF; 
                            addNewEntity(tile, e); 
                        }
                        else if ((rgb & 0xFFFF00) == 0x00FF00) {
                            Entity e = new Ghost(); 
                            e.turnsToDeath = rgb & 0xFF; 
                            addNewEntity(tile, e); 
                        }
                        else {
                            System.out.printf("ERROR unknown tile type: %06x\n", rgb); 
                        }
                    }
                    
                    if (tile != null) {
                        tile.offset += -400 + ((width - x) + (height - y)) * -100; 
                    }
                    this.tiles[x + y * width] = tile; 
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load level " + filename); 
        }
    }
    
    private void addNewEntity(Tile t, Entity e) {
        entities.add(e); 
        t.setEntity(e); 
    }
    
    public boolean tryMove(Entity e, int dx, int dy) {
        int nx = e.getTileX() + dx; 
        int ny = e.getTileY() + dy; 
        
        if (!inBounds(nx, ny)) {
            return false; 
        }
        
        Tile tile = getTile(nx, ny); 
        
        if (tile == null) {
            return false; 
        }
        
        if (tile.isWall()) {
            return false; 
        }
        
        if (tile.getEntity() != null) {
            return false; 
        }
        
        e.getTile().setEntity(null); 
        tile.setEntity(e); 
        
        return true; 
    }
    
    public Entity findPossessableEntity(Entity e, int dx, int dy) {
        int nx = e.getTileX() + dx; 
        int ny = e.getTileY() + dy; 
        
        while (inBounds(nx, ny)) {
            Tile tile = getTile(nx, ny); 
            
            if (tile != null) {
                if (tile.isWall()) return null; 
                
                if (tile.getEntity() != null && tile.getEntity().alive && tile.getEntity().turnsToDeath > 0) {
                    return tile.getEntity(); 
                }
            }
            
            nx += dx; 
            ny += dy; 
        }
        
        return null; 
    }
    
    public int getWidth() {
        return width; 
    }
    
    public int getHeight() {
        return height; 
    }
    
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height; 
    }
    
    public void checkBounds(int x, int y) {
        if (!inBounds(x, y)) {
            throw new RuntimeException("Out of bounds: (" + x + ", " + y + ")"); 
        }
    }
    
    public Tile getTile(int x, int y) {
        checkBounds(x, y); 
        return tiles[x + y * width]; 
    }
    
    public boolean entityOnAll(int type) {
        for (Tile t : tiles) {
            if (t != null && t.type == type) {
                if (t.getEntity() == null) {
                    return false; 
                }
            }
        }
        
        return true; 
    }
    
    public void setLocked(int type, boolean locked) {
        boolean any = false; 
        for (Tile t : tiles) {
            if (t != null && t.type == type) {
                if (t.locked != locked && t.getEntity() != null) {
                    t.getEntity().kill();
                    Sound.choose(Sound.EXPLOSION).play(); 
                }
                if (t.locked != locked) {
                    any = true; 
                    t.locked = locked; 
                }
            }
        }
        
        if (any) {
            if (locked) {
                Sound.choose(Sound.LOCK).play(); 
            }
            else {
                Sound.choose(Sound.UNLOCK).play(); 
            }
        }
    }
    
    public Tile findType(int type) {
        for (Tile t : tiles) {
            if (t != null && t.type == type) return t; 
        }
        return null; 
    }
    
    public Tile findWithEntity(Class<? extends Entity> e) {
        for (Tile t : tiles) {
            if (t != null && t.getEntity() != null && t.getEntity().getClass() == e) return t; 
        }
        return null; 
    }
    
    public Entity findPossessed() {
        for (Tile t : tiles) {
            if (t != null && t.getEntity() != null && t.getEntity().possessed) return t.getEntity(); 
        }
        return null; 
    }
    
    public void render(Renderer r) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y); 
                r.setTile(t); 
                if (t != null && !t.isWall()) t.render(r, x, y); 
            }
        }
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y); 
                r.setTile(t); 
                if (t != null) {
                    if (t.isWall()) t.render(r, x, y); 
                    if (t.getEntity() != null) t.getEntity().render(r); 
                }
            }
        }
        
        r.pushZOrder(1000);
        if (filename.equals("level1")) {
            r.setTile(findWithEntity(Bed.class)); 
            r.drawString("Goal", (-0.8f) * Game.TILE_SIZE, (1.3f) * Game.TILE_SIZE, 1, true);
            r.drawImage(SpriteSheet.TILESET.get(1, 2), (-0.3f) * Game.TILE_SIZE, (0.75f) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            
            r.setTile(getTile(0, 3)); 
            r.drawString("Move with arrows", (2f) * Game.TILE_SIZE, (4.5f) * Game.TILE_SIZE, 1, true);
            r.setTile(getTile(1, 3)); 
            r.drawString("Possess with space", (2f) * Game.TILE_SIZE, (5.5f) * Game.TILE_SIZE, 1, true);
            
            Entity mc = findPossessed(); 
            if (mc != null) {
                if (mc.getClass() != Bed.class) {
                    r.setTile(mc.getTile()); 
                    r.drawString("You", (mc.getX()+2.4f) * Game.TILE_SIZE, (mc.getY()+0.2f) * Game.TILE_SIZE, 1, true);
                    r.drawImage(SpriteSheet.TILESET.get(1, 3), (mc.getX()+0.9f) * Game.TILE_SIZE, (mc.getY()-0.3f) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
                }
            }
        }
        else if (filename.equals("level2")) {
            r.setTile(getTile(5, 5)); 
            r.drawString("Possess", (-1.5f+4) * Game.TILE_SIZE, (1.3f+4) * Game.TILE_SIZE, 1, true);
            r.drawImage(SpriteSheet.TILESET.get(1, 2), (-0.3f+4) * Game.TILE_SIZE, (0.75f+4) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else if (filename.equals("level3")) {
            r.setTile(getTile(0, 6)); 
            r.drawString("Gate", (-1.6f) * Game.TILE_SIZE, (6.5f) * Game.TILE_SIZE, 1, true);
            r.drawImage(SpriteSheet.TILESET.get(1, 2), (-0.0f+-1) * Game.TILE_SIZE, (6.0f) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
            
            r.setTile(getTile(4, 9)); 
            r.drawString("Unlock", (5-.15f) * Game.TILE_SIZE, (10.3f) * Game.TILE_SIZE, 1, false);
            r.drawImage(SpriteSheet.TILESET.get(0, 3), (4) * Game.TILE_SIZE, (10.0f-.1f) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        r.popZOrder(); 
    }
    
    public void update(Keyboard input) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y); 
                if (t != null) t.update(input); 
            }
        }
        
        for (Entity e : entities) {
            e.update(input);
        }
        
        boolean anyEntityPossessed = false; 
        boolean winOnPossessed = false; 
        boolean anyDeathAnimation = false; 
        for (Entity e : entities) {
            if (e.possessed && e.winOnPossess && !e.isMoving()) {
                winOnPossessed = true; 
            }
            if (e.explosion >= 0) {
                anyDeathAnimation = true; 
            }
            anyEntityPossessed |= e.possessed; 
        }
        if (winOnPossessed && !anyDeathAnimation) {
            status = STATUS_WIN; 
        }
        if (!anyEntityPossessed) {
            status = STATUS_LOSE; 
        }
        
        setLocked(Tile.TYPE_GATE_1, !entityOnAll(Tile.TYPE_PLATE_1));
        setLocked(Tile.TYPE_GATE_2, !entityOnAll(Tile.TYPE_PLATE_2));
        setLocked(Tile.TYPE_GATE_3, !entityOnAll(Tile.TYPE_PLATE_3));
        
        int i = 0; 
        while (i < entities.size()) {
            if (entities.get(i).alive) {
                i++; 
            }
            else {
                entities.get(i).getTile().setEntity(null); 
                entities.remove(i); 
            }
        }
    }
    
}
