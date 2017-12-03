package cz.brose;

import org.fredy.jsrt.api.SRT;
import org.fredy.jsrt.api.SRTInfo;
import org.fredy.jsrt.api.SRTReader;
import org.fredy.jsrt.api.SRTReaderException;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Vojtech Bruza
 */
//TODO implements runnable - bude to samo modifikovat promennou ktera bude obsahovat text
public class SRTObject {
    ArrayList<SRT> srts;
    private int srtIndex;

    SRTObject(String srtFileName) {
        SRTInfo inputSRT;
        try {
            inputSRT = SRTReader.read(new File(srtFileName));
            srts = new ArrayList<>();
            for (SRT srt : inputSRT) {
                srts.add(srt);
            }
            srtIndex = -1;
        } catch (SRTReaderException e) {
            System.err.println("Check this file, not able to read it: " + srtFileName);
            System.exit(1);
        }
    }

    final long ONEHOURINMILLIS = 3600000;

    String getNextLyrics(long millis){
        if(srtIndex + 1 >= srts.size()){
            System.out.println("none");
            return ""; //there are no other lyrics
        }
        StringBuilder nextLyrics = new StringBuilder();
        if(millis >= getNextLyricsStartTime()){ //
            System.out.println("SONG POSITION: " + millis);
            srtIndex++;
            SRT actualSRT = srts.get(srtIndex);
            if(actualSRT == null){
                System.out.println("none index");
                return "";
            }
            while (millis > getNextLyricsEndTime()){
                actualSRT = srts.get(srtIndex);
                srtIndex++;
                if(srtIndex >= srts.size() || actualSRT == null || getNextLyricsEndTime() == Long.MAX_VALUE){
                    System.out.println("none2");
                    return "";
                }
            }
            for (String line : actualSRT.text) {
                nextLyrics.append(line);
            }
            System.out.println("NEXT LYRICS START: " + getNextLyricsStartTime());
            System.out.println("NEXT LYRICS END: " + getNextLyricsEndTime());
            System.out.print("LYRICS: ");
        }
        System.out.println(nextLyrics.toString());
        System.out.println();
        return nextLyrics.toString();
    }

    long getNextLyricsStartTime(){
        int nextSRTIndex = srtIndex + 1;
        if(nextSRTIndex >= srts.size()){
            return Long.MAX_VALUE;
        }
        return srts.get(nextSRTIndex).startTime.getTime() + ONEHOURINMILLIS; //Magical constant to make it work
    }

    long getNextLyricsEndTime(){
        int nextSRTIndex = srtIndex + 1;
        if(nextSRTIndex >= srts.size()){
            return Long.MAX_VALUE;
        }
        return srts.get(nextSRTIndex).endTime.getTime() + ONEHOURINMILLIS;
    }
}
