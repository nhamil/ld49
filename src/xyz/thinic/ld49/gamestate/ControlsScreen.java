package xyz.thinic.ld49.gamestate;

import xyz.thinic.ld49.Game;
import xyz.thinic.ld49.Keyboard;
import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.Sound;

public class ControlsScreen extends Screen {
    
    public static final String ID = "Controls"; 
    
    public static boolean seen = false; 
    
    public ControlsScreen(Game game) {
        super(game); 
    }
    
    public boolean renderWhenInactive() {
        return false; 
    }
    
    public void onResume() {
        seen = true; 
    }
    
    public void update(Keyboard input) {
        if (input.getKeyJustDown(Keyboard.ACTION)) {
            game.popScreen(); 
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
        
        String[] lines = new String[] {
                "         Arrow keys      move                  ", 
                "         Spacebar        toggle possession mode", 
                "         Backspace       reload level          ", 
                "         Escape          exit to menu          ", 
                "", 
                "", 
                "", 
                "", 
                "", 
                "Press spacebar to continue"
        };
        
        float size = 0.5f; 
        for (int i = 0; i < lines.length; i++) {
             r.drawString(lines[i], 0, i * Game.TILE_SIZE * size - 32, size, true); 
        }
    }
    
}
