package com.smu.appmod;

import android.widget.ImageView;

public class Helper {
    public static void setAppImage(ImageView image, String appname) {
        if (appname.equalsIgnoreCase("YouTube")) {
            image.setImageResource(R.drawable.app_yt);
        } else if (appname.equalsIgnoreCase("Facebook")) {
            image.setImageResource(R.drawable.app_fb);
        } else if (appname.equalsIgnoreCase("Instagram")) {
            image.setImageResource(R.drawable.app_is);
        } else if (appname.equalsIgnoreCase("Gmail")) {
            image.setImageResource(R.drawable.app_gm);
        } else if (appname.equalsIgnoreCase("Whatsapp")) {
            image.setImageResource(R.drawable.app_wh);
        } else if (appname.equalsIgnoreCase("Clock")) {
            image.setImageResource(R.drawable.app_cl);
        } else if (appname.equalsIgnoreCase("Candy")) {
            image.setImageResource(R.drawable.app_cr);
        } else if (appname.equalsIgnoreCase("Sudoku")) {
            image.setImageResource(R.drawable.app_sk);
        } else if (appname.equalsIgnoreCase("Bubble")) {
            image.setImageResource(R.drawable.app_bubble);
        } else if (appname.equalsIgnoreCase("Compass")) {
            image.setImageResource(R.drawable.app_compass);
        } else if (appname.equalsIgnoreCase("Linkedin")) {
            image.setImageResource(R.drawable.app_linkedin);
        } else if (appname.equalsIgnoreCase("Microsoft")) {
            image.setImageResource(R.drawable.app_outlook);
        } else if (appname.equalsIgnoreCase("Tumblr")) {
            image.setImageResource(R.drawable.app_tumblr);
        } else if (appname.equalsIgnoreCase("Mahjong")) {
            image.setImageResource(R.drawable.app_mahjong);
        } else if (appname.equalsIgnoreCase("Line")) {
            image.setImageResource(R.drawable.app_line);
        } else if (appname.equalsIgnoreCase("VLC")) {
            image.setImageResource(R.drawable.app_vlc);
        } else if (appname.equalsIgnoreCase("My")) {
            image.setImageResource(R.drawable.app_mytalkingtom);
        } else if (appname.equalsIgnoreCase("Calculator")) {
            image.setImageResource(R.drawable.app_calculator);
        } else if (appname.equalsIgnoreCase("Snapchat")) {
            image.setImageResource(R.drawable.app_snapchat);
        } else if (appname.equalsIgnoreCase("Messenger")) {
            image.setImageResource(R.drawable.app_messenger);
        } else if (appname.equalsIgnoreCase("Google+")) {
            image.setImageResource(R.drawable.app_googleplus);
        } else if (appname.equalsIgnoreCase("Happy")) {
            image.setImageResource(R.drawable.app_happycolor);
        } else if (appname.equalsIgnoreCase("Skype")) {
            image.setImageResource(R.drawable.app_skype);
        } else if (appname.equalsIgnoreCase("MX")) {
            image.setImageResource(R.drawable.app_mxplayer);
        } else {
            image.setImageResource(R.drawable.app);
        }
    }
}
