package xyz.thinic.ld49.gamestate;

import xyz.thinic.ld49.Game;
import xyz.thinic.ld49.Keyboard;
import xyz.thinic.ld49.Renderer;

public class Screen {

    protected final Game game; 
    
    public Screen(Game game) {
        this.game = game; 
    }
    
    public boolean renderWhenInactive() {
        return false; 
    }
    
    public void onEnter() {
        
    }
    
    public void onResume() {
        
    }
    
    public void update(Keyboard input) {
        
    }
    
    public void render(Renderer r) {
        
    }
    
}
