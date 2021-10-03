package xyz.thinic.ld49;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Keyboard {

    public static final String UP = "up"; 
    public static final String DOWN = "down"; 
    public static final String LEFT = "left"; 
    public static final String RIGHT = "right"; 
    public static final String ACTION = "action"; 
    public static final String RESTART = "restart"; 
    public static final String ESCAPE = "escape"; 
    
    private int curLastKey = 0; 
    private int lastKey = 0; 
    
    private Set<Integer> curKeys = new HashSet<>(); 
    private Set<Integer> downKeys = new HashSet<>(); 
    private Set<Integer> lastKeys = new HashSet<>(); 
    
    private Map<String, List<Integer>> keybindings = new HashMap<>(); 
    
    public Keyboard() {
        setKeybind(UP, KeyEvent.VK_UP); 
        setKeybind(DOWN, KeyEvent.VK_DOWN); 
        setKeybind(LEFT, KeyEvent.VK_LEFT); 
        setKeybind(RIGHT, KeyEvent.VK_RIGHT);
        setKeybind(ACTION, KeyEvent.VK_SPACE); 
        setKeybind(ACTION, KeyEvent.VK_ENTER); 
        setKeybind(RESTART, KeyEvent.VK_BACK_SPACE); 
        setKeybind(ESCAPE, KeyEvent.VK_ESCAPE); 
    }
    
    /**
     * Update this first 
     */
    public void update() {
        lastKey = curLastKey; 
        lastKeys.clear();
        lastKeys.addAll(downKeys); 
        downKeys.clear(); 
        downKeys.addAll(curKeys); 
    }
    
    public void setKey(int keycode, boolean down) {
        if (down) {
            curKeys.add(keycode); 
            curLastKey = keycode; 
        }
        else {
            curKeys.remove(keycode); 
            curLastKey = 0; 
        }
    }
    
    public boolean getLastKey(int keycode) {
        return lastKey == keycode; 
    }
    
    public boolean getLastKey(String id) {
        for (int i : keybindings.get(id)) {
            if (getLastKey(i)) return true; 
        }
        return false; 
    }
    
    public boolean getKey(int keycode) {
        return downKeys.contains(keycode); 
    }
    
    public boolean getKey(String id) {
        for (int i : keybindings.get(id)) {
            if (getKey(i)) return true; 
        }
        return false; 
    }
    
    public boolean getKeyJustDown(int keycode) {
        return downKeys.contains(keycode) && !lastKeys.contains(keycode); 
    }
    
    public boolean getKeyJustDown(String id) {
        for (int i : keybindings.get(id)) {
            if (getKeyJustDown(i)) return true; 
        }
        return false; 
    }
    
    public void setKeybind(String id, int keycode) {
        if (!keybindings.containsKey(id)) {
            keybindings.put(id, new ArrayList<>()); 
        }
        keybindings.get(id).add(keycode); 
    }
    
}
