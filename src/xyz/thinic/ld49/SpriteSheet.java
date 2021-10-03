package xyz.thinic.ld49;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class SpriteSheet {

    public static SpriteSheet TILESET = new SpriteSheet("tileset", 16, 16); 
    public static SpriteSheet FONT = new SpriteSheet("font", 6, 8); 
    
    private BufferedImage original; 
    private BufferedImage[][] sprites; 
    
    public SpriteSheet(String filename, int spriteWidth, int spriteHeight) {
        try {
            original = ImageIO.read(SpriteSheet.class.getResourceAsStream("/images/" + filename + ".png")); 
        } 
        catch (Exception e) {
            throw new RuntimeException("Could not load sprite sheet from " + filename); 
        }
        
        int wide = original.getWidth() / spriteWidth; 
        int high = original.getHeight() / spriteHeight; 
        
        sprites = new BufferedImage[high][wide]; 
        
        for (int y = 0; y < high; y++) {
            for (int x = 0; x < wide; x++) {
                sprites[y][x] = original.getSubimage(x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight); 
            }
        }
    }
    
    public BufferedImage get(int x, int y) {
        return sprites[y][x]; 
    }
    
}
