package xyz.thinic.ld49.gamestate;

import xyz.thinic.ld49.Game;
import xyz.thinic.ld49.Keyboard;
import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.Sound;

public class LevelSelectScreen extends Screen {
    
    public static final String ID = "LevelSelect"; 
    
    public static final float DZOOM_SPEED = 0.02f; 
    
    private int selected = 0; 
    private float zoom = 1.0f; 
    private float dZoom = DZOOM_SPEED; 
    
    public LevelSelectScreen(Game game) {
        super(game); 
    }
    
    public boolean renderWhenInactive() {
        return false; 
    }
    
    public void onResume() {
        selected = 0; 
    }
    
    public void update(Keyboard input) {
        if (input.getKeyJustDown(Keyboard.DOWN)) {
            selected++; 
            Sound.SELECT.play(); 
        }
        if (input.getKeyJustDown(Keyboard.UP)) {
            selected--; 
            Sound.SELECT.play(); 
        }
        while (selected < 0) selected += Game.MAX_LEVEL+1; 
        while (selected > Game.MAX_LEVEL) selected -= Game.MAX_LEVEL+1; 
        
        zoom += dZoom; 
        if (zoom > 1.2f) {
            zoom = 1.2f; 
            dZoom = -DZOOM_SPEED; 
        }
        else if (zoom < 0.8f) {
            zoom = 0.8f; 
            dZoom = DZOOM_SPEED; 
        }
        
        if (input.getKeyJustDown(Keyboard.ACTION)) {
            itemSelected(); 
            Sound.SELECT.play(); 
        }
        else if (input.getKeyJustDown(Keyboard.ESCAPE)) {
            game.popScreen(); 
            Sound.SELECT.play(); 
        }
    }
    
    public void render(Renderer r) {
        game.camera.x = 0; 
        game.camera.y = 0; 
        game.camera.zoom = (int) (8 * Math.round(Math.min(game.getHeight() / 8, game.getWidth() / 16)) / 8) / Game.TILE_SIZE; 
        
        float size = 0.5f; 
        for (int i = 0; i <= Game.MAX_LEVEL; i++) {
            if (i < Game.MAX_LEVEL) {
                r.drawString("Level " + (i+1), 0, i * Game.TILE_SIZE * size, size * (selected == i ? zoom : 1), true); 
            }
            else {
                r.drawString("Back", 0, i * Game.TILE_SIZE * size, size * (selected == i ? zoom : 1), true); 
            }
        }
    }
    
    private void itemSelected() {
        if (selected == Game.MAX_LEVEL) {
            game.popScreen(); 
            return; 
        }
        
        PlayScreen.levelNum = selected + 1; 
        game.swapForScreen(PlayScreen.ID); 
    }
    
}
