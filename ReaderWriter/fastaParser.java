package ReaderWriter;

import java.io.*;
import java.util.HashMap;

public class fastaParser {

    /**
     * reads a input file in fasta format into a hashmap containing sequence ids as
     * keys and their corresponding Aminoacid sequences as values for prediction
     * @param fasta_file input fasta file
     * @return Hashmap containing all sequences with ids as keys
     */
    public HashMap<String, String> fastaToHashmap(File fasta_file) {
        HashMap<String, String> parsed_file = new HashMap<>();
        try {
            FileReader fr = new FileReader(fasta_file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String header = "";
            String sequence = "";
            while ((line = br.readLine()) != null) {
                if(line.length() > 0) {
                    if (line.charAt(0) == '>') {

                        if (sequence != "") {
                            parsed_file.put(header, sequence);
                            header = line;
                            sequence = "";
                        } else {
                            header = line;
                        }
                    } else{
                            sequence += line;
                        }
                    }
                }
            //last entry
            parsed_file.put(header,sequence);
            }
            catch (IOException e) {
            System.out.println("Issue while reading the file");
            e.printStackTrace();
        }
    return parsed_file;
    }
}
