package com.jwjo1g20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {

    public static String removeTag(String line) {
        String ret;
        ret = line.substring(line.indexOf('>') + 1);
        ret = ret.substring(0, ret.indexOf('<'));

        return ret;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        //Creating main ecs URL & Console Reader
        URL sotonUrl = new URL("https://www.ecs.soton.ac.uk/");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        //Gets the email ID
        System.out.print("Enter an email ID: ");
        String emailID = consoleReader.readLine();
        System.out.println();

        //Creates URL to ID webpage, prints to console
        URL personURL = new URL(sotonUrl, "/people/" + emailID);
        System.out.println(personURL.toString());

        //Creating new URL Reader and variable for html code
        BufferedReader personIn = new BufferedReader(new InputStreamReader(personURL.openStream()));
        String inputLine = null;

        //Runs through the first bit of code until it hits the correct line, then closes input stream
        for (int i = 0; i < 8; i++) {
            inputLine = personIn.readLine();
        }
        personIn.close();

        //Variables for string manipulation
        String targetString;
        Integer endOfNameIndex;

        //Finds the index of the '|' Character which occurs at the end of the Name. Then gets the substring from the start to the end of the name
        //Outputs the name then leaves space below
        endOfNameIndex = inputLine.indexOf('|');
        targetString = inputLine.substring(11, endOfNameIndex);
        System.out.println(targetString);

        System.out.println();
        System.out.println("-".repeat(30));
        System.out.println();



        //Finding Related People -- Not currently working as web page is on an intranet which is above my capabilities.
        //Creating Related People URL and new URL reader
        URL relatedURL = new URL("https://secure.soton.ac.uk/people/" + emailID + "/related_people");
        //System.out.println(relatedURL.toString());
        BufferedReader relatedIn = new BufferedReader(new InputStreamReader(personURL.openStream()));

        //Loop through code lines until there is no more to be read
        String codeLine = null;
        String removeStrong = null;
        String removeHRef = null;
        String removeTD = null;
        Integer skipLoops = 0;
        Boolean isReadingNames = false;
        Boolean isReadingID = false;
        while ((codeLine = relatedIn.readLine()) != null) {

            if (codeLine.contains("<strong>")) {
                //If <strong> used, name of group of related people. substring used to remove tags
                removeStrong = removeTag(codeLine);
                System.out.println(removeStrong);

                //Sets the bool to true to indicate that subsequent loops must read names in this group
                isReadingNames = true;
            } else if (isReadingNames && codeLine.contains("a href")) {
                //If group encountered, take their names and output them along with their id
                removeHRef = removeTag(codeLine);
                System.out.print(removeHRef);

                //Sets the bool to true to indicate that ID is being read, specifies times until correct line is achieved
                isReadingID = true;
                skipLoops = 2;
            } else if (isReadingID == true) {
                //Skips unimportant lines
                if (skipLoops != 0) {
                    skipLoops--;
                    continue;
                } else {
                    //Gets their ID and puts it next to their name
                    removeTD = removeTag(codeLine);
                    System.out.println("\t\t\t" + removeTD);

                    isReadingID = false;
                }

            } else if (codeLine == "<br>") {
                //End of group
                isReadingNames = false;
            } else {
                //Not interested in these lines
                continue;
            }
        }
        relatedIn.close();

    }
}
