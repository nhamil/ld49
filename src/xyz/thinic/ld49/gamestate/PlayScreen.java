package xyz.thinic.ld49.gamestate;

import xyz.thinic.ld49.Game;
import xyz.thinic.ld49.Keyboard;
import xyz.thinic.ld49.Level;
import xyz.thinic.ld49.Renderer;

public class PlayScreen extends Screen {

    public static final String ID = "Play"; 
    
    public static int levelNum; 
    private Level level; 
    
    public PlayScreen(Game game) {
        super(game); 
        
        loadLevel(1); 
    }
    
    public void onEnter() {
        loadLevel(levelNum); 
    }
    
    public void onResume() {
        if (!ControlsScreen.seen) {
            game.pushScreen(ControlsScreen.ID); 
        }
        if (!AboutScreen.seen) {
            game.pushScreen(AboutScreen.ID); 
        }
    }
    
    public void loadLevel(int num) {
        levelNum = num; 
        level = new Level("level" + num); 
    }
    
    public void nextLevel() {
        levelNum++; 
        if (levelNum > Game.MAX_LEVEL) {
            levelNum = 1; 
            game.swapForScreen(VictoryScreen.ID); 
        }
        else {
            loadLevel(levelNum); 
        }
    }
    
    public void update(Keyboard input) {
        level.update(input); 
        
        if (level.status != Level.STATUS_ONGOING) {
            if (level.status == Level.STATUS_WIN) {
                nextLevel(); 
            }
            else {
                loadLevel(levelNum); 
            }
        }
        
        if (input.getKeyJustDown(Keyboard.RESTART)) {
            loadLevel(levelNum); 
        }
        
        if (input.getKeyJustDown(Keyboard.ESCAPE)) {
            game.popScreen(); 
        }
    }
    
    public void render(Renderer r) {
        game.camera.x = 0.5f * level.getWidth() * Game.TILE_SIZE; 
        game.camera.y = 0.5f * level.getHeight() * Game.TILE_SIZE - Game.TILE_SIZE / 4; 
        game.camera.zoom = (int) (8 * Math.round(Math.min(game.getHeight() / (level.getHeight() + 2), game.getWidth() / (level.getWidth() + 2)) / 8)) / Game.TILE_SIZE; 
        level.render(r); 
    }
    
}
