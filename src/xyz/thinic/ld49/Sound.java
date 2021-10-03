package xyz.thinic.ld49;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    public static Sound MAIN_THEME = new Sound("theme", true).setGain(0.90f); 
    
    public static Sound SELECT = new Sound("select").setGain(0.75f); 
    
    public static Sound EXPLOSION[] = new Sound[] { 
            new Sound("explosion1").setGain(0.85f), 
            new Sound("explosion2").setGain(0.85f), 
            new Sound("explosion3").setGain(0.85f)
    }; 
    
    public static Sound MOVE[] = new Sound[] { 
            new Sound("move1").setGain(0.85f), 
            new Sound("move2").setGain(0.85f), 
            new Sound("move3").setGain(0.85f), 
            new Sound("move4").setGain(0.85f), 
            new Sound("move5").setGain(0.85f), 
    }; 
    
    public static Sound POSSESS[] = new Sound[] { 
            new Sound("possess1").setGain(0.85f), 
            new Sound("possess2").setGain(0.85f), 
            new Sound("possess3").setGain(0.85f), 
    };
    
    public static Sound LOCK[] = new Sound[] { 
            new Sound("lock1").setGain(0.85f), 
    };
    
    public static Sound UNLOCK[] = new Sound[] { 
            new Sound("unlock1").setGain(0.85f), 
    };
    
    public static Sound GOAL[] = new Sound[] { 
            new Sound("goal1").setGain(0.85f), 
            new Sound("goal2").setGain(0.85f), 
            new Sound("goal3").setGain(0.85f), 
            new Sound("goal4").setGain(0.85f), 
            new Sound("goal5").setGain(0.85f), 
    };
    
    private String filename; 
    private Clip clip; 
    private boolean music; 
    
    private static Sound curMusic = null; 
    
    public Sound(String filename) {
        this(filename, false); 
    }
    
    public Sound(String filename, boolean music) {
        this.filename = filename; 
        this.music = music; 
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/sounds/" + filename + ".wav"))); 
            clip = AudioSystem.getClip(null); 
            clip.open(ais);
        }
        catch (Exception e) {
            System.out.println("Could not load sound from " + filename + ": " + e.getMessage()); 
        }
    }
    
    public static Sound choose(Sound[] list) {
        return list[(int) (Math.random() * list.length)]; 
    }
    
    public boolean isLoaded() {
        return clip != null; 
    }
    
    public String getFileName() {
        return filename; 
    }
    
    public boolean isMusic() {
        return music; 
    }
    
    public Sound setGain(float amt) {
        try {
            if (isLoaded()) {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                control.setValue(control.getMinimum() + amt * (control.getMaximum() - control.getMinimum()));
            }
        }
        catch (Exception e) {
            System.out.println("Could not change volume: " + e.getMessage()); 
        } 
        
        return this; 
    }
    
    public Sound setGainChange(float amt) {
        if (isLoaded()) {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(control.getValue() + (control.getMinimum() + amt * (control.getMaximum() - control.getMinimum())));
        }
        
        return this; 
    }
    
    public void play() {
        play(false); 
    }
    
    public void play(boolean loop) {
        if (isLoaded()) {
            if (curMusic != null && curMusic != this) {
                curMusic.stop(); 
            }
            
            stop(); 
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); 
            }
            else {
                clip.loop(0); 
            }
            clip.start(); 
        }
    }
    
    public void playIfStopped() {
        playIfStopped(false); 
    }
    
    public void playIfStopped(boolean loop) {
        if (isLoaded() && !clip.isRunning()) {
            if (curMusic != null && curMusic != this) {
                curMusic.stop(); 
            }
            
            stop(); 
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); 
            }
            else {
                clip.loop(0); 
            }
            clip.start(); 
        }
    }
    
    public void stop() {
        if (isLoaded()) {
            if (clip.isRunning()) {
                clip.stop(); 
            }
            clip.setFramePosition(0); 
        }
    }
    
}
