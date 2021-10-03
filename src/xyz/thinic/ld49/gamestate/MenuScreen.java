package xyz.thinic.ld49.gamestate;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import xyz.thinic.ld49.Game;
import xyz.thinic.ld49.Keyboard;
import xyz.thinic.ld49.Renderer;
import xyz.thinic.ld49.Sound;

public class MenuScreen extends Screen {
    
    public static final String ID = "Menu"; 
    
    public static final int ITEM_PLAY = 0; 
    public static final int ITEM_LEVELS = 1; 
    public static final int ITEM_ABOUT = 2; 
    public static final int ITEM_CONTROLS = 3; 
    public static final int ITEM_QUIT = 4; 
    public static final int NUM_ITEMS = 5; 
    public static final Map<Integer, String> ITEM_NAMES = new HashMap<>(); 
    
    static {
        ITEM_NAMES.put(ITEM_PLAY, "Play"); 
        ITEM_NAMES.put(ITEM_LEVELS, "Levels"); 
        ITEM_NAMES.put(ITEM_ABOUT, "About"); 
        ITEM_NAMES.put(ITEM_CONTROLS, "Controls"); 
        ITEM_NAMES.put(ITEM_QUIT, "Quit"); 
    }
    
    public static final float DZOOM_SPEED = 0.02f; 
    
    private int selected = 0; 
    private float zoom = 1.0f; 
    private float dZoom = DZOOM_SPEED; 
    
    private BufferedImage title, background;  
    
    public MenuScreen(Game game) {
        super(game); 
        
        try {
            title = ImageIO.read(MenuScreen.class.getResourceAsStream("/images/title.png"));
            background = ImageIO.read(MenuScreen.class.getResourceAsStream("/images/background.png"));
        }
        catch (Exception e) {
            System.out.println("Could not load title resources"); 
        }
    }
    
    public boolean renderWhenInactive() {
        return false; 
    }
    
    public void onResume() {
        selected = 0; 
        
        Sound.MAIN_THEME.playIfStopped(true); 
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
        while (selected < 0) selected += NUM_ITEMS; 
        while (selected >= NUM_ITEMS) selected -= NUM_ITEMS; 
        
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
            System.exit(0); 
        }
    }
    
    public void render(Renderer r) {
        game.camera.x = 0; 
        game.camera.y = 0; 
        game.camera.zoom = (int) (8 * Math.round(Math.min(game.getHeight() / 12, game.getWidth() / 24)) / 8) / Game.TILE_SIZE; 
        
        float h = 512; 
        float w = h * background.getWidth() / background.getHeight(); 
        r.drawImage(background, -w/2, -h/2, w, h);
        
        float size = 1; 
        for (int i = 0; i < NUM_ITEMS; i++) {
            r.drawString(ITEM_NAMES.get(i), 0, (i-0) * Game.TILE_SIZE, size * (selected == i ? zoom : 1), true); 
        }
        
        h = 128; 
        w = h * title.getWidth() / title.getHeight(); 
        r.drawImage(title, -w/2, -h/2, w, h);
        
        
    }
    
    private void itemSelected() {
        switch (selected) {
        case ITEM_PLAY: 
            game.pushScreen(PlayScreen.ID); 
            break; 
        case ITEM_LEVELS: 
            game.pushScreen(LevelSelectScreen.ID); 
            break; 
        case ITEM_ABOUT: 
            game.pushScreen(AboutScreen.ID); 
            break; 
        case ITEM_CONTROLS: 
            game.pushScreen(ControlsScreen.ID); 
            break; 
        case ITEM_QUIT: 
            System.exit(0); 
            break; 
        }
    }
    
}
