package me.sirlich.AsmodeusRpg.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class RpgFileReader
{
    BufferedReader reader;
    public RpgFileReader(BufferedReader reader){
        this.reader = reader;
    }

    public double readDouble(String tag, int level){
        double returnThis = 0.0;
        if(level >= 10){
            //Early cancelation because the level is wacked out.
            return returnThis;
        }
        try {
            String line = reader.readLine();
            while (line != null)
            {
                if(line.charAt(0) == '['){
                    //Is it our line?
                    if(line.contains(tag)){
                        //Take the second half
                        String doubleList = line.split("]")[1];
                        //Generate the numbers
                        double[] doubles = Stream.of(doubleList.split(",")).mapToDouble (Double::parseDouble).toArray();
                        returnThis = doubles[level];
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnThis;
    }

    public int readInt(String tag, int level){
        int returnThis = 0;
        if(level >= 10){
            //Early cancelation because the level is wacked out.
            return returnThis;
        }
        try {
            String line = reader.readLine();
            while (line != null)
            {
                if(line.charAt(0) == '['){
                    //Is it our line?
                    if(line.contains(tag)){
                        //Take the second half
                        String intList = line.split("]")[1];
                        //Generate the numbers
                        int[] ints = Stream.of(intList.split(",")).mapToInt (Integer::parseInt).toArray();
                        returnThis = ints[level];
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnThis;
    }

    public String readString(String tag, int level){
        String returnThis = "NULL PLEASE FIX";
        if(level >= 10){
            //Early cancelation because the level is wacked out.
            return returnThis;
        }
        try {
            String line = reader.readLine();
            while (line != null)
            {
                if(line.charAt(0) == '['){
                    //Is it our line?
                    if(line.contains(tag)){
                        //Take the second half
                        String stringList = line.split("]")[1];
                        String[] strings = stringList.split(",");
                        returnThis = strings[level].trim();
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnThis;
    }

    public String readString(String tag){
        String returnThis = "NULL PLEASE FIX";
        try {
            String line = reader.readLine();
            while (line != null)
            {
                if(line.charAt(0) == '['){
                    //Is it our line?
                    if(line.contains(tag)){
                        //Take the second half
                        String stringList = line.split("]")[1];
                        String[] strings = stringList.split(",");

                        //Generate random one instead.
                        int level = ThreadLocalRandom.current().nextInt(0, strings.length + 1);
                        returnThis = strings[level].trim();
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnThis;
    }
}
