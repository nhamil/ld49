package xyz.thinic.ld49;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import xyz.thinic.ld49.gamestate.AboutScreen;
import xyz.thinic.ld49.gamestate.ControlsScreen;
import xyz.thinic.ld49.gamestate.LevelSelectScreen;
import xyz.thinic.ld49.gamestate.MenuScreen;
import xyz.thinic.ld49.gamestate.PlayScreen;
import xyz.thinic.ld49.gamestate.Screen;
import xyz.thinic.ld49.gamestate.VictoryScreen;

public class Game {

    public static final String TITLE = "Astral Instability"; 
    public static final int WIDTH = 1024; 
    public static final int HEIGHT = 538; 
    
    private Canvas canvas; 
    private Frame frame; 
    private boolean running = false; 
    
    public final Keyboard keyboard = new Keyboard(); 
    public final Camera camera = new Camera(); 
    
    public static final int MAX_LEVEL = 5; 
    
    public static final float TILE_SIZE = 16; 
    
    private Map<String, Screen> screens = new HashMap<>(); 
    private List<Screen> screenStack = new ArrayList<>(); 
    
    public Game() {
        frame = new Frame(TITLE); 
        frame.addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                System.exit(0); 
            }
        });
        try {
            frame.setIconImage(ImageIO.read(Game.class.getResourceAsStream("/images/icon.png")));
        }
        catch (Exception e) {
            System.out.println("Couldn't load icon");
        }
        
        
        canvas = new Canvas(); 
        canvas.setSize(WIDTH, HEIGHT); 
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyboard.setKey(e.getKeyCode(), true); 
            }
            @Override
            public void keyReleased(KeyEvent e) {
                keyboard.setKey(e.getKeyCode(), false); 
            }
        });
        
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyboard.setKey(e.getKeyCode(), true); 
            }
            @Override
            public void keyReleased(KeyEvent e) {
                keyboard.setKey(e.getKeyCode(), false); 
            }
        });
        
        frame.add(canvas); 
        frame.pack(); 
        frame.setLocationRelativeTo(null); 
//        frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        frame.setVisible(true); 
        
        canvas.setFocusable(true); 
        canvas.requestFocus();
    }
    
    public void registerScreen(String id, Screen screen) {
        screens.put(id, screen); 
    }
    
    public void pushScreen(String id) {
        screenStack.add(screens.get(id)); 
        getScreen().onEnter(); 
        getScreen().onResume(); 
    }
    
    public void popScreen() {
        screenStack.remove(screenStack.size() - 1); 
        if (screenStack.size() > 0) getScreen().onResume(); 
    }
    
    public void swapForScreen(String id) {
        screenStack.remove(screenStack.size() - 1); 
        screenStack.add(screens.get(id)); 
        getScreen().onEnter(); 
        getScreen().onResume(); 
    }
    
    public Screen getScreen() {
        return screenStack.get(screenStack.size() - 1); 
    }
    
    public int getWidth() {
        return canvas.getWidth(); 
    }
    
    public int getHeight() {
        return canvas.getHeight(); 
    }
    
    public void start() {
        if (running) throw new RuntimeException("Already running"); 
        
        running = true; 
        run(); 
    }
    
    public void stop() {
        if (!running) throw new RuntimeException("Not running"); 
        
        running = false; 
        System.exit(0); 
    }
    
    private void run() {
        init(); 
        
        long frameTimer = System.nanoTime(); 
        long skipFrames = 1000000000 / 60; 
        
        while (running) {
            int loops = 0; 
            while (frameTimer < System.nanoTime() && loops++ < 10) {
                keyboard.update(); 
                update(); 
                frameTimer += skipFrames; 
            }
            
            render(); 
            
            try { Thread.sleep(1); } catch (Exception e) {} 
        }
    }
    
    public void init() {
        registerScreen(PlayScreen.ID, new PlayScreen(this)); 
        registerScreen(MenuScreen.ID, new MenuScreen(this)); 
        registerScreen(LevelSelectScreen.ID, new LevelSelectScreen(this)); 
        registerScreen(ControlsScreen.ID, new ControlsScreen(this)); 
        registerScreen(AboutScreen.ID, new AboutScreen(this)); 
        registerScreen(VictoryScreen.ID, new VictoryScreen(this)); 
        pushScreen(MenuScreen.ID); 
    }
    
    public void update() {
        getScreen().update(keyboard); 
    }
    
    public void render() {
        BufferStrategy bs = canvas.getBufferStrategy(); 
        
        int loops = 0; 
        while (bs == null && loops++ < 10) {
            try {
                canvas.createBufferStrategy(2); 
                bs = canvas.getBufferStrategy(); 
                Thread.sleep(10);
            }
            catch (Exception e) {} 
        }
        
        if (bs == null) return; 
        
        Graphics2D g = (Graphics2D) bs.getDrawGraphics(); 
        g.setColor(new Color(0x66BBFF));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); 
        Renderer r = new Renderer(g, camera, canvas.getWidth(), canvas.getHeight()); 
        
        for (int i = 0; i < screenStack.size(); i++) {
            Screen s = screenStack.get(i); 
            if (i == screenStack.size() - 1 || s.renderWhenInactive()) {
                s.render(r); 
                r.flush(); 
            }
        }
        
        r.flush(); 
        g.dispose(); 
        bs.show(); 
    }
    
    public static void main(String[] args) {
        new Game().start(); 
    }
    
}
