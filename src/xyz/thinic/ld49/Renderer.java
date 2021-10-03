package xyz.thinic.ld49;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Renderer {

    private Graphics2D graphics; 
    private Camera camera; 
    private int halfWidth; 
    private int halfHeight; 
    private List<Float> zOrder = new ArrayList<>(); 
    
    private float yOffset = 0; 
    
    private List<DrawCommand> drawCommands = new ArrayList<>(); 
    
    private abstract class DrawCommand {
        float z; 
        
        DrawCommand() {
            this.z = zOrder.get(zOrder.size() - 1); 
        }
        
        abstract void draw(); 
    }
    
    private class DrawImageCommand extends DrawCommand {
        Image image; 
        float x, y, w, h; 
        float z; 
        
        DrawImageCommand(Image image, float x, float y, float w, float h) {
            this.image = image; 
            this.x = x; 
            this.y = y + yOffset; 
            this.w = w; 
            this.h = h; 
        }
        
        void draw() {
            int xi = transformX(x); 
            int yi = transformY(y); 
            int wi = transformX(x + w) - xi; 
            int hi = transformY(y + h) - yi; 
            
            graphics.drawImage(image, xi, yi, wi, hi, null);
        }
    }
    
    private class DrawTextCommand extends DrawCommand {
        String text; 
        Color color; 
        float x, y, w, h; 
        float size; 
        float z; 
        boolean center; 
        
        DrawTextCommand(String text, Color color, float x, float y, float size, boolean center) {
            this.color = color; 
            this.text = text; 
            this.x = x; 
            this.y = y + yOffset; 
            this.size = size; 
            this.center = center; 
        }
        
        void draw() {
            float xi = transformX(x); 
            float yi = transformY(y); 
            
            text = text.toUpperCase(); 
            
            BufferedImage sprite = SpriteSheet.FONT.get(0, 0); 
            float spriteW = sprite.getWidth() * camera.zoom * size; 
            float spriteH = sprite.getHeight() * camera.zoom * size; 
            
            if (center) {
                xi -= (text.length() * spriteW) / 2; 
                yi -= (spriteH) / 2; 
            }
            
            graphics.setColor(color); 
//            graphics.drawString(text, xi, yi);
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                int fx = 0; 
                int fy = 0; 
                if (Character.isAlphabetic(c)) {
                    fx = (int) (c - 'A'); 
                }
                else if (Character.isDigit(c)) {
                    fx = (int) (c - '0') + 26; 
                }
                else if (c == '.') {
                    fx = 36; 
                }
                else if (c == '!') {
                    fx = 37; 
                }
                else if (c == '?') {
                    fx = 38; 
                }
                else if (c == ',') {
                    fx = 39; 
                }
                else {
                    continue; 
                }
                sprite = SpriteSheet.FONT.get(fx, fy); 
                graphics.drawImage(sprite, (int) (xi + i * spriteW), (int) (yi), (int) (spriteW), (int) (spriteH), null); 
            }
        }
    }
    
    public Renderer(Graphics2D graphics, Camera camera, int width, int height) {
        this.graphics = graphics; 
        this.camera = camera; 
        this.halfWidth = width / 2; 
        this.halfHeight = height / 2; 
        this.zOrder.add(0.0f); 
    }
    
    public void setTile(Tile tile) {
        if (tile == null) {
            yOffset = 0; 
        }
        else {
            yOffset = tile.offset * 0.2f; 
        }
    }
    
    public void setTile(Tile from, Tile to, float x) {
        float y1 = 0; 
        float y2 = 0; 
        
        if (from != null) {
            y1 = from.offset; 
        }
        
        if (to != null) {
            y2 = from.offset; 
        }
        
        yOffset = y1 + x * (y2 - y1); 
    }
    
    public int transformX(float x) {
        return (int) Math.floor((x - camera.x) * camera.zoom) + halfWidth; 
    }
    
    public int transformY(float y) {
        return (int) Math.floor((y - camera.y) * camera.zoom) + halfHeight; 
    }
    
    public void setColor(Color col) {
        graphics.setColor(col); 
    }
    
//    public void fillRect(float x, float y, float w, float h) {
//        int xi = transformX(x); 
//        int yi = transformY(y); 
//        int wi = transformX(x + w) - xi; 
//        int hi = transformY(y + h) - yi; 
//        
//        graphics.fillRect(xi, yi, wi, hi);
//    }
    
    public void pushZOrder(float z) {
        zOrder.add(z); 
    }
    
    public void popZOrder() {
        zOrder.remove(zOrder.size() - 1); 
    }
    
    public void drawImage(Image image, float x, float y, float w, float h) {
        drawCommands.add(new DrawImageCommand(image, x, y, w, h)); 
    }
    
    public void drawString(String text, float x, float y, float size, boolean center) {
        drawCommands.add(new DrawTextCommand(text, graphics.getColor(), x, y, size, center)); 
    }
    
    public void flush() {
        drawCommands.sort(new Comparator<DrawCommand>() {
            public int compare(DrawCommand a, DrawCommand b) {
                float res = a.z - b.z;
                return res < 0 ? -1 : res > 0 ? 1 : 0; 
            }
            
        });
        
        for (DrawCommand cmd : drawCommands) {
            cmd.draw(); 
        }
        
        drawCommands.clear(); 
    }
    
}
