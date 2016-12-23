package de.createplus.data;


import java.awt.*;
import java.awt.image.BufferedImage;

public class manager {
    public BufferedImage KinectColor = null;
    public BufferedImage KinectDepth = null;

    public manager(){

    }

    public BufferedImage getKinectColor() {return KinectColor;}

    public BufferedImage getKinectDepth() {return KinectDepth;}

    public BufferedImage getKinectDepthLine() {return createDepthLineMap(KinectDepth,3);}

    public BufferedImage getKinectColorLine() {return createLineMap(KinectDepth,3);}






    public BufferedImage createLineMap(BufferedImage imgS, int range) {
        BufferedImage img = unsign(imgS);
        //System.out.println(":");
        int width = img.getWidth();
        int height = img.getHeight();
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                int     up = 0,
                        down = 0,
                        left = 0,
                        right = 0,
                        point = img.getRGB(p,k);
                /*if(k-range >= 0){
                    up = img.getRGB(p,k-range);
                }
                if (p-range >= 0){
                    left = img.getRGB(p-range,k);
                }*/
                if (k+range < height){
                    down = img.getRGB(p,k+range);
                }
                if(p+range < width){
                    right = img.getRGB(p+range,k);
                }
                Color c;
                int a = compareColor(RGB(point),RGB(down)) / 3;
                int b = compareColor(RGB(point),RGB(right)) / 3;

                if(a < b) a = b;
                if (a < 30) a = 0;
                a = a * 4;
                if (a > 255)a=255;

                c = new Color(a,a,a);
                img.setRGB(p, k, c.getRGB());


            }

        }
        return img;
    }

    public BufferedImage createDepthLineMap(BufferedImage imgS, int range) {
        BufferedImage img = unsign(imgS);
        //System.out.println(":");
        int width = img.getWidth();
        int height = img.getHeight();
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                int     black = new Color(0,0,0).getRGB(),
                        down = new Color(0,0,0).getRGB(),
                        right = new Color(0,0,0).getRGB(),
                        point = img.getRGB(p,k);
                int maximised = range;
                if (k+range < height){
                    while(down == black && maximised < range+10&&k+maximised < height){
                        down = img.getRGB(p,k+maximised);
                        maximised++;
                    }

                }
                maximised = range;
                if(p+range < width){
                    while(right == black && maximised < range+10&&p+maximised < width){
                        right = img.getRGB(p+maximised,k);
                        maximised++;
                    }
                }

                Color c;
                int a = compareColor(RGB(point),RGB(down)) / 3;
                int b = compareColor(RGB(point),RGB(right)) / 3;

                if(a < b && down != black) a = b;
                if (right == black || a <5) a = 0;

                a = a * 4;
                if (a > 255)a=255;

                c = new Color(a,a,a);

                img.setRGB(p, k, c.getRGB());
            }

        }
        return img;
    }

    public BufferedImage unsign (BufferedImage img){
        BufferedImage New = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
        int width = img.getWidth();
        int height = img.getHeight();
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                New.setRGB(p,k,img.getRGB(p,k));
            }

        }
        return New;
    }

    public int compareColor(Color c1, Color c2){
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        if(r < 0) r = r*(-1);
        if(g < 0) g = g*(-1);
        if(b < 0) b = b*(-1);

        return (r + g + b);
    }

    public Color RGB(int RGB){
        int r = (RGB>>16)&0xFF;
        int g = (RGB>>8)&0xFF;
        int b = (RGB>>0)&0xFF;
        return new Color(r,g,b);
    }
}
