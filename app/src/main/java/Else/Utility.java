package Else;

public class Utility {

    public static String convertDur(long dur){
        long minutes = (dur/1000) / 60;
        long seconds = (dur/1000) % 60;
        String co = String.format("%d:%02d",minutes,seconds);
        return co;
    }
}
