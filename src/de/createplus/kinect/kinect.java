package de.createplus.kinect;
import de.createplus.data.manager;
import edu.ufl.digitalworlds.j4k.J4KSDK;

import java.awt.*;
import java.awt.image.BufferedImage;

public class kinect extends J4KSDK{
    public manager manager;
    @Override
    public void onDepthFrameEvent(short[] depth_frame, byte[] body_index, float[] xyz, float[] uv) {}

    @Override
    public void onSkeletonFrameEvent(boolean[] skeleton_tracked, float[] positions,float[] orientations, byte[] joint_status) {}

    @Override
    public void onColorFrameEvent(byte[] color_frame) {}


    //---------------------------------[Standart Images]---------------------------------
    public BufferedImage createRGBImage(byte[] bytes, int width, int height) {
        BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        int i = 0;
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                //System.out.println(bytes[i] + " " + bytes[i+1] + " " + bytes[i+2] + " " + i);

                int     r = bytes[i+2],
                        g = bytes[i+1],
                        b = bytes[i];

                if(r < 0) r = r+255;
                if(g < 0) g = g+255;
                if(b < 0) b = b+255;
                int col = new Color(r, g, b).getRGB();
                img.setRGB(p, k, col);
                i+=4;
            }

        }
        return img;
    }

    public BufferedImage createDepthImage(float[] bytes, int width, int height) {
        //System.out.println(":");
        BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        int i = 0;
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                //System.out.print(bytes[i+2]);
                int b = (int)(bytes[i+2]*64);
                int col = new Color(b, b, b).getRGB();
                //if(b == 0) col = new Color(255, 0, 0).getRGB();
                img.setRGB(p, k, col);
                i+=3;
            }

        }
        return img;
    }


    //---------------------------------[Line Maps]---------------------------------
    public BufferedImage createLineMap(BufferedImage imgS, int range, int trigger) {
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

    public BufferedImage createDepthLineMap(BufferedImage imgS, int range, int trigger) {
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


    //---------------------------------[Surfaces]---------------------------------
    public BufferedImage createSurfaceMap(BufferedImage imgS, int range) {
        BufferedImage img = unsign(imgS);
        int width = img.getWidth();
        int height = img.getHeight();
        int darkest = 255;
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                //System.out.println(RGB(img.getRGB(p,k)).getRed());
                if (RGB(img.getRGB(p,k)).getRed() > 0 && RGB(img.getRGB(p,k)).getRed() < darkest){//RGB(img.getRGB(p,k)).getRed() > 0 &&
                    darkest = RGB(img.getRGB(p,k)).getRed();
                }
                //img.setRGB(p,k,RGB(img.getRGB(p,k)).getRGB());
            }

        }
        //System.out.println(darkest);
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                if(RGB(img.getRGB(p,k)).getRed() < darkest+range && RGB(img.getRGB(p,k)).getRed() > darkest-range){
                    img.setRGB(p,k,new Color(darkest,darkest,darkest).getRGB());
                }else img.setRGB(p,k,new Color(0,0,0).getRGB());

            }

        }

        return img;
    }

    //---------------------------------[Surfaces]---------------------------------
    public BufferedImage MatchDepthColorLines(BufferedImage DepthS, BufferedImage ColorS){
        int ColorLineTrigger = 10;
        BufferedImage Depth = unsign(DepthS);
        BufferedImage Color = unsign(ColorS);
        BufferedImage DepthLine = createDepthLineMap(Depth,3,50);
        BufferedImage ColorLine = createLineMap(Color,3,50);
        int width = Color.getWidth();
        int height = Color.getHeight();
        BufferedImage Result = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                Color   DepthPoint = RGB(Depth.getRGB(p,k)),
                        ColorPoint = RGB(Color.getRGB(p,k)),
                        DepthLinePoint = RGB(DepthLine.getRGB(p,k)),
                        ColorLinePoint = RGB(ColorLine.getRGB(p,k));
                if(ColorLinePoint.getRed() > ColorLineTrigger){

                }
            }
        }
        return Result;
    }


    //---------------------------------[usefull Methods]---------------------------------
    public BufferedImage scaleImage(BufferedImage img,int width,int height){
        float widthScale = width / img.getWidth();
        float heightScale = height / img.getHeight();

        BufferedImage New = new BufferedImage(width,height,img.getType());


        Graphics2D grph = (Graphics2D) New.getGraphics();
        grph.scale(widthScale, heightScale);

        grph.drawImage(img, 0, 0, null);
        grph.dispose();

        return New;
    }

    public BufferedImage createMap(BufferedImage imgS) {
        BufferedImage img = unsign(imgS);
        //System.out.println(":");
        int width = img.getWidth();
        int height = img.getHeight();
        for (int k = 0; k < height; k++) {
            for (int p = 0; p < width; p++) {
                img.setRGB(p,k,RGB(img.getRGB(p,k)).getRGB());
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
