package xyz.thinic.ld49.entity;

import java.awt.Image;

import xyz.thinic.ld49.Game;
import xyz.thinic.ld49.Keyboard;
import xyz.thinic.ld49.Level;
import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.Sound;
import xyz.thinic.ld49.SpriteSheet;
import xyz.thinic.ld49.Tile;

public class Entity {

    public boolean possessed = false; 
    public boolean waitInput = false; 
    
    public boolean possessionMenu = false; 
    
    public boolean alive = true; 
    public boolean winOnPossess = false; 
    
    private Tile tile; 
    private float tileMove; 
    private float x, y, xOff, yOff; 
    protected boolean killOnLeave = false; 
    private float possessX, possessY, possessMove = 1; 
    private int possessDir; 
    
    public int turnsToDeath = 16; 
    
    private static final int ANIMATION_FRAMES = 2; 
    private static final int EXPLOSION_SPEED = 2; 
    private static final int EXPLOSION_FRAMES = 8 * EXPLOSION_SPEED; 
    
    private int nextAnimation = 0; 
    public int explosion = -1; 
    
    public void onTileSet(Tile tile) {
        if (this.tile == null) {
            this.x = tile.x; 
            this.y = tile.y; 
        }
        else {
            this.x = this.tile.x; 
            this.y = this.tile.y; 
        }
        
        possessX = this.x; 
        possessY = this.y; 
        this.tileMove = 0.0f; 
        this.tile = tile; 
    }
    
    public void setPossessed(Entity from, boolean p) {
        if (possessed != p) {
            possessed = p; 
            if (p) {
                waitInput = true; 
                possessX = from.getX(); 
                possessY = from.getY(); 
                possessMove = 0; 
                
                if (tile.x < from.tile.x) {
                    possessDir = 3; 
                }
                else if (tile.x > from.tile.x) {
                    possessDir = 1; 
                }
                else if (tile.y < from.tile.y) {
                    possessDir = 2; 
                }
                else if (tile.y > from.tile.y) {
                    possessDir = 0; 
                }
            }
        }
    }
    
    public void kill() {
        explosion = 0; 
        nextAnimation = 0; 
    }
    
    public final int getTileX() {
        return tile.x; 
    }
    
    public final int getTileY() {
        return tile.y; 
    }
    
    public final float getX() {
        return x + tileMove * (tile.x - x); 
    }
    
    public final float getY() {
        return y + tileMove * (tile.y - y); 
    }
    
    public final float getPossessionX() {
        return possessX + possessMove * (tile.x - possessX); 
    }
    
    public final float getPossessionY() {
        return possessY + possessMove * (tile.y - possessY); 
    }
    
    public final float getRenderX() {
        return getX() + xOff; 
    }
    
    public final float getRenderY() {
        return getY() + yOff; 
    }
    
    public final Level getLevel() {
        return tile.level; 
    }
    
    public final Tile getTile() {
        return tile; 
    }
    
    public boolean hasNewAnimationFrame() {
        return nextAnimation == ANIMATION_FRAMES; 
    }
    
    public boolean isMoving() {
        return tileMove < 1 || possessMove < 1; 
    }
    
    public void onPossessed() {
        
    }
    
    public void update(Keyboard input) {
        nextAnimation++; 
        
        if (hasNewAnimationFrame()) {
            if (explosion >= 0) {
                explosion++; 
                
                if (explosion >= EXPLOSION_FRAMES) {
                    alive = false; 
                    return; 
                }
            }
            else if (possessed && possessMove == 1) {
                if (turnsToDeath < 1) {
                    xOff = (float) (Math.random() * 2 - 1) * 8f / Game.TILE_SIZE; 
                    yOff = (float) (Math.random() * 2 - 1) * 8f / Game.TILE_SIZE; 
                }
                else if (turnsToDeath < 4) {
                    xOff = (float) (Math.random() * 2 - 1) * 4f / Game.TILE_SIZE; 
                    yOff = (float) (Math.random() * 2 - 1) * 4f / Game.TILE_SIZE; 
                }
                else {
                    xOff = (float) (Math.random() * 2 - 1) * 1f / Game.TILE_SIZE; 
                    yOff = (float) (Math.random() * 2 - 1) * 1f / Game.TILE_SIZE; 
                }
            }
            else {
                xOff = yOff = 0; 
            }
        }
        
        if (explosion >= 0) {
            // do nothing, waiting for animation to be over 
        }
        else if (possessMove < 1) {
            possessMove += 1f / 16f; 
            if (possessMove > 1) {
                possessMove = 1; 
                onPossessed(); 
            }
        }
        else if (tileMove < 1) {
            tileMove += 2.0f / 16f; 
            if (tileMove > 1) {
                tileMove = 1; 
            }
        }
        else if (!waitInput) {
            if (possessionMenu) {
                if (input.getKeyJustDown(Keyboard.ACTION)) {
                    possessionMenu = false; 
                }
                else {
                    Entity target = null; 
                    boolean pressed = false; 
                    if (input.getKeyJustDown(Keyboard.UP)) {
                        pressed = true; 
                        target = getLevel().findPossessableEntity(this, 0, -1); 
                    }
                    else if (input.getKeyJustDown(Keyboard.DOWN)) {
                        pressed = true; 
                        target = getLevel().findPossessableEntity(this, 0, 1); 
                    }
                    else if (input.getKeyJustDown(Keyboard.LEFT)) {
                        pressed = true; 
                        target = getLevel().findPossessableEntity(this, -1, 0); 
                    }
                    else if (input.getKeyJustDown(Keyboard.RIGHT)) {
                        pressed = true; 
                        target = getLevel().findPossessableEntity(this, 1, 0); 
                    }
                    if (pressed) {
                        possessionMenu = false; 
                        if (target != null) {
                            if (target.winOnPossess) { 
                                Sound.choose(Sound.GOAL).play(); 
                            }
                            else {
                                Sound.choose(Sound.POSSESS).play(); 
                            }
                            target.setPossessed(this, true);
                            this.possessed = false; 
                            if (killOnLeave) {
                                kill(); 
                            }
                            decreaseTurnTimer(); 
                        }
                    }
                }
                
            }
            else {
                if (possessed) handlePossessed(input); 
            }
        }
        
        waitInput = false; 
        if (nextAnimation >= ANIMATION_FRAMES) {
            nextAnimation = 0; 
        }
    }
    
    public void handlePossessed(Keyboard input) {
        boolean moved = false; 
        
        if (input.getKeyJustDown(Keyboard.ACTION)) {
            possessionMenu = true; 
        }
        else if (input.getKeyJustDown(Keyboard.UP)) {
            moved = getLevel().tryMove(this, 0, -1); 
        }
        else if (input.getKeyJustDown(Keyboard.DOWN)) {
            moved = getLevel().tryMove(this, 0, 1); 
        }
        else if (input.getKeyJustDown(Keyboard.LEFT)) {
            moved = getLevel().tryMove(this, -1, 0); 
        }
        else if (input.getKeyJustDown(Keyboard.RIGHT)) {
            moved = getLevel().tryMove(this, 1, 0); 
        }
        
        if (moved) {
            if (decreaseTurnTimer()) Sound.choose(Sound.MOVE).play();  
        }
    }
    
    public final void render(Renderer r) {
        r.setTile(tile); 
        
        if (explosion >= 0) {
            int sx = killOnLeave ? 11 : 13; 
            int sy = (explosion / EXPLOSION_SPEED) * 2; 
            r.drawImage(SpriteSheet.TILESET.get(sx+0, sy+0), (getX() - 0.5f) * Game.TILE_SIZE, (getY() - 0.5f) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(sx+1, sy+0), (getX() + 0.5f) * Game.TILE_SIZE, (getY() - 0.5f) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(sx+0, sy+1), (getX() - 0.5f) * Game.TILE_SIZE, (getY() + 0.5f) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(sx+1, sy+1), (getX() + 0.5f) * Game.TILE_SIZE, (getY() + 0.5f) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        else {
            if (possessMove < 1) {
                r.drawImage(SpriteSheet.TILESET.get(4 + possessDir % 2, possessDir / 2), (getPossessionX()) * Game.TILE_SIZE, (getPossessionY()) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            }
            
            renderObject(r); 
            renderMenu(r); 
            
            if (turnsToDeath >= 1 && turnsToDeath <= 16) {
                r.pushZOrder(10000);
                r.drawImage(SpriteSheet.TILESET.get(15, turnsToDeath-1), (getRenderX()) * Game.TILE_SIZE, (getRenderY() - 1.5f) * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE);
                r.popZOrder(); 
            }
        }
    }
    
    public void renderObject(Renderer r) {
        
    }
    
    public void renderImage(Renderer r, Image img, int xOff, int yOff) {
        r.drawImage(img, (getRenderX()) * Game.TILE_SIZE + xOff, (getRenderY()) * Game.TILE_SIZE + yOff, Game.TILE_SIZE, Game.TILE_SIZE);
    }
    
    public void renderMenu(Renderer r) {
        if (possessionMenu) {
            r.pushZOrder(100000);
            r.drawImage(SpriteSheet.TILESET.get(0, 2), (getX()) * Game.TILE_SIZE, (getY() + 1) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(0, 3), (getX()) * Game.TILE_SIZE, (getY() - 1) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(1, 2), (getX() + 1) * Game.TILE_SIZE, (getY()) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.drawImage(SpriteSheet.TILESET.get(1, 3), (getX() - 1) * Game.TILE_SIZE, (getY()) * Game.TILE_SIZE - 6, Game.TILE_SIZE, Game.TILE_SIZE);
            r.popZOrder(); 
        }
    }
    
    public boolean decreaseTurnTimer() {
        turnsToDeath--; 
        if (turnsToDeath <= 0) {
            kill(); 
            Sound.choose(Sound.EXPLOSION).play(); 
            return false; 
        }
        return true; 
    }
    
}
