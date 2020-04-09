package ReaderWriter;

import gors.predict.Gor5.G5AliSet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PredictionsWriter {
    private HashMap<String, String[]> predictions;
    private int halfwindowsize;
    public PredictionsWriter(int halfwindowsize) {
        this.halfwindowsize = halfwindowsize;
    }

    public char[] PostProcessString(char[] predStruct){
        char first, middle, last;
        for (int i=halfwindowsize + 1; i < predStruct.length - halfwindowsize; i++){
            first = predStruct[i-1];
            middle = predStruct[i];
            last = predStruct[i+1];
            if (first == last && middle != last && last != '-'){
                predStruct[i] = first;
            }
        }
        return predStruct;
    }

    public HashMap<String, String[]> PostProcessing(HashMap<String, String[]> preds){
        HashMap<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, String[]> prediction: preds.entrySet()){
            String id = prediction.getKey();
            String seq = prediction.getValue()[0];
            char[] predStruct = prediction.getValue()[1].toCharArray();
            String hProb = prediction.getValue()[2];
            String eProb = prediction.getValue()[3];
            String cProb = prediction.getValue()[4];
            predStruct = PostProcessString(predStruct);
            String[] resultArray = {seq, String.valueOf(predStruct), hProb, eProb, cProb};
            result.put(id, resultArray);
        }
        return result;
    }

    public PredictionsWriter(HashMap<String, String[]> preds, int halfwindowsize){
        this.halfwindowsize = halfwindowsize;
        this.predictions = preds;
    }

    public PredictionsWriter(HashMap<String, String[]> preds, int halfwindowsize, boolean postproc){
        if (postproc) {
            this.halfwindowsize = halfwindowsize;
            this.predictions = PostProcessing(preds);
        } else {
            this.halfwindowsize = halfwindowsize;
            this.predictions = preds;
        }
    }

    /**
     *
     * @param gor_model gor model used for prediction
     * @param probabilities also writes state probabilities into outputfile if true
     * @param seq_file name of inputfile
     */
    public void writeToHtml(String gor_model, boolean probabilities, String seq_file){
        String header = "<!doctype html><html lang=\"de\"><head><h1>GOR Secondary Structure Prediction</h1>" + "<h2>" + "Prediction method: " + gor_model + "</h2>";
        String body = "<h2> Inputfile: " + seq_file + "</h2>" + "<br>" + "<h3>" + "Prediction: " + "</h3>" + "<body><textarea rows = 20 cols = 150>";
        String footer = "</html>";
        String result;
        try{
            FileWriter fr = new FileWriter("html_out" + ".html");
            BufferedWriter br = new BufferedWriter(fr);
            for (Map.Entry<String, String[]> prediction : predictions.entrySet()){
                result = "";
                if(probabilities){
                    result += prediction.getKey() + "\n";
                    result += "AS " + prediction.getValue()[0] + "\n";
                    result += "PS " + prediction.getValue()[1] + "\n";
                    result += "PH " + prediction.getValue()[2] + "\n";
                    result += "PE " + prediction.getValue()[3] + "\n";
                    result += "PC " + prediction.getValue()[4] + "\n";
                } else{
                    result += prediction.getKey() + "\n";
                    result += "AS " + prediction.getValue()[0] + "\n";
                    result += "PS " + prediction.getValue()[1] + "\n";
                }
                body += result;
            }
            body += "</textarea></body>";
            br.write(header);
            br.write(body);
            br.write(footer);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This function isnt used, but nice to have!
    /**
     * writes predictions into txt file
     * @param gor_model gor model used for prediction
     * @param probabilities also writes state probabilities into outputfile if true
     */
    public void writeToFile(String gor_model, boolean probabilities, boolean post) {
        try {
            File output_file = new File("/home/jonas/validation_prediction/Predictions/" + gor_model + "prediction1");
            if (post) {
                output_file = new File("/home/jonas/validation_prediction/Predictions/" + gor_model + "postprediction1");
            }
            boolean fileCreation = output_file.createNewFile();
            if (fileCreation) {
                try (Writer file_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file)))){
                    for (Map.Entry<String, String[]> prediction : predictions.entrySet()){
                        file_writer.write(prediction.getKey() + "\n");
                        String result = "";
                        result += "AS " + prediction.getValue()[0] + "\n";
                        result += "PS " + prediction.getValue()[1] + "\n";
                        if(probabilities) {
                            result += "PH " + prediction.getValue()[2] + "\n";
                            result += "PE " + prediction.getValue()[3] + "\n";
                            result += "PC " + prediction.getValue()[4] + "\n";
                        }
                        file_writer.write(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already created");
            }
        } catch (IOException e) {
            System.out.println("The following exception occured: ");
            e.printStackTrace();
        }
    }

    /**
     * writes predictions into stdout
     * @param probabilities also writes state probabilities into stdout if true
     */
    public void writeToStdout(boolean probabilities){
        if(probabilities){
        for (Map.Entry<String, String[]> prediction : predictions.entrySet()) {
            System.out.println(prediction.getKey());
            System.out.println("AS " + prediction.getValue()[0]);
            System.out.println("PS " + prediction.getValue()[1]);
            System.out.println("PH " + prediction.getValue()[2]);
            System.out.println("PE " + prediction.getValue()[3]);
            System.out.println("PC " + prediction.getValue()[4]);
        }
        } else{
            for (Map.Entry<String, String[]> prediction : predictions.entrySet()) {
                System.out.println(prediction.getKey());
                System.out.println("AS " + prediction.getValue()[0]);
                System.out.println("PS " + prediction.getValue()[1]);
            }
        }
    }

    /**
     *
     * @param curSet G5AliSet currently printed
     * @param probabilities also writes state probabilities into stdout if true
     */
    public void writeAliset(G5AliSet curSet, boolean probabilities){
        System.out.println(curSet.getId());
        System.out.println("AS "+ curSet.getAseq());
        System.out.println("PS " + curSet.getPrediction());

        //TODO für abgabeserver immer post processing!!!
        System.out.println("PS " + String.valueOf(PostProcessString(curSet.getPrediction().toCharArray())));

        if (probabilities) {
            System.out.println("PH " + curSet.getPh());
            System.out.println("PE " + curSet.getPe());
            System.out.println("PC " + curSet.getPc());
        }
        System.out.println();
    }

    public void writeFileAliset(G5AliSet curSet, boolean probabilities, String gor_model, boolean post){
        try {
            File output_file = new File("/home/jonas/IdeaProjects/FINALFINALGOR/gor_pros/src/outputs/gor5" +
                    gor_model + curSet.getId().substring(2));
            boolean fileCreation = output_file.createNewFile();
            String result = "";
            if (fileCreation) {
                try (Writer file_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file)))){
                    result += curSet.getId()+"\n";
                    result += "AS " + curSet.getAseq()+"\n";
                    result += "RS "+curSet.getSseq()+"\n";
                    if (post){
                        result += "PS " + String.valueOf(PostProcessString(curSet.getPrediction().toCharArray())) + "\n";
                    } else {
                        result += "PS " + curSet.getPrediction() + "\n";
                    }
                    if (probabilities) {
                        result += "PH "+curSet.getPh()+"\n";
                        result += "PE "+curSet.getPe()+"\n";
                        result += "PC "+curSet.getPc()+"\n";
                    }
                    file_writer.write(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already created");
            }
        } catch (IOException e) {
            System.out.println("The following exception occured: ");
            e.printStackTrace();
        }
    }
}
