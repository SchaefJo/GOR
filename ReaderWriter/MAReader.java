package ReaderWriter;

import gors.predict.Gor5.G5AliSet;
import gors.predict.Gor5.g5seq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MAReader {

    /**
     * reads a folder containing multiple sequence alignment files into G5AliSet objects, each
     * containing the sequence id, amino acid sequence, secondary structure sequence and all alignments
     * @param folder multiple alignment files folder
     * @return Arraylist containing all multiple alignment folders as G5AliSet objets
     */
    public ArrayList<G5AliSet> readFolder(File folder) {
        File[] listOfFiles = folder.listFiles();
        ArrayList<G5AliSet> alns = new ArrayList<>();
        for (File file: listOfFiles){
            if (file.isFile()){
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    G5AliSet singleAln = new G5AliSet();

                    while ((line = br.readLine()) != null) {
                        if(line.length() > 0) {
                            if (line.charAt(0) == '>') {
                                singleAln.setId(line);
                            } else if (Character.isDigit(line.charAt(0))){
                                g5seq seq = new g5seq(line.split(" ")[1]);
                                singleAln.appendAlignment(seq);

                            } else if(line.charAt(0) == 'A') {
                                singleAln.setAseq(line.split(" ")[1]);
                            }
                            else if(line.charAt(0) == 'S') singleAln.setSseq(line.split(" ")[1]);
                        }
                    }
                    alns.add(singleAln);

                }
                catch (IOException e) {
                    System.out.println("Issue while reading the file");
                    e.printStackTrace();
                }
            }
        }
        return alns;
    }


}
