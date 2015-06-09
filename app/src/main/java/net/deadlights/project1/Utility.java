package net.deadlights.project1;

/**
 * Created by atropos on 6/7/15.
 */
public class Utility {

    public static String getFormattedTime(double time)
    {
        Integer seconds = (int) (time / 1000) % 60 ;
        Integer minutes = (int) ((time / (1000*60)) % 60);
        String s = String.format("%d:%02d", minutes, seconds);
        return s;
    }
}
