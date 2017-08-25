package de.griot_app.griot;

/**
 * Created by marcel on 25.08.17.
 */

public class Helper {

    static public String getLengthStringFromMiliseconds(long miliseconds) {
        int seconds, hours, minutes;
        seconds = (int)(miliseconds/1000.0);
        hours = (int)((double)seconds/(60*60));
        seconds = seconds - hours * 60*60;
        minutes = (int)((double)seconds/60);
        seconds = seconds - minutes * 60;
        return "" + (hours==0 ? "" : hours + ":") + (minutes<10 ? "0" + minutes : minutes) + ":" + (seconds<10 ? "0" + seconds : seconds);
    }
}
