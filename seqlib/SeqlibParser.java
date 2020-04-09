package seqlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SeqlibParser {
    public ArrayList<SeqlibEntry> parseSeqlib(File seqlib_file){
        ArrayList<SeqlibEntry> entries = new ArrayList<>();
        try {
            FileReader fr = new FileReader(seqlib_file);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            String header = "";
            String aas = "";
            String secstrucs = "";
            while((line=reader.readLine())!=null){
                if (line.length() > 0){
                    if(line.charAt(0) == '>') {
                        if (secstrucs != "") {
                            SeqlibEntry entry = new SeqlibEntry(header, aas, secstrucs);
                            entries.add(entry);
                            header = line;
                            aas = "";
                            secstrucs = "";
                        } else header = line;
                    }
                    else if (line.charAt(0) == 'A' && line.split(" ").length > 1) aas = line.split(" ")[1];
                    else if (line.split(" ").length > 1) secstrucs = line.split(" ")[1];
                }
            }
            //last entry
            SeqlibEntry entry = new SeqlibEntry(header, aas, secstrucs);
            entries.add(entry);
        }
        catch (IOException e){
            System.out.println("\u001B[31m" + "Problem at reading the seqlib_file!");
            e.printStackTrace();
        }

        return entries;
    }
}
