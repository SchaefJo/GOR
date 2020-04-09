package gors.predict;

import storages.AAStateStorage;
import storages.Matrix;
import storages.StateMatrixStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import gors.predict.SinglePrediction;

public class Gor3predict {
    private AAStateStorage AASS;
    private int windowsize;
    private double pseudocounts;


    public Gor3predict(AAStateStorage AASS, int windowsize, double pseudocounts) {
        this.windowsize = windowsize;
        this.AASS = AASS;
    }

    /**
     * train a AAStateStorage with sequences
     * @param sequences HashMap with sequences id as key and sequence as value
     * @return HashMap with predictions, id as a key and predictions as a String array
     */
    public HashMap<String, String[]> prediction(HashMap<String,String> sequences){
        HashMap<String, String[]> predStructure = new HashMap<>();
        for (Map.Entry<String,String> entry : sequences.entrySet()) {
            String header = entry.getKey();
            String sequence = entry.getValue();
            if (sequence.length() > windowsize) {
                char[] seq_array = sequence.toCharArray();
                String prediction = "";
                String hprob = "";
                String eprob = "";
                String cprob = "";
                for(int i=0;i<Math.floorDiv(windowsize, 2);i++){
                    prediction += "-";
                    hprob += "-";
                    eprob += "-";
                    cprob += "-";
                }
                SinglePrediction singlePrediction;
                for (int i = 0; i < seq_array.length - windowsize + 1; i++) {
                    char[] currentWindow = Arrays.copyOfRange(seq_array, i, i + 17);
                    char middleAA = currentWindow[8];
                    if (AASS.checkAA(middleAA)) {
                        // create a StateMatrixStorage for every middleAA and update these StateMatrices
                        StateMatrixStorage SMS = AASS.getAA(middleAA);
                        Gor1predict gor1 = new Gor1predict(SMS, windowsize, pseudocounts);
                        StateMatrixStorage logGor3 = gor1.getLogedStateMatrix();
                        singlePrediction = gor1.WindowPrediction(logGor3, currentWindow);
                        prediction += singlePrediction.predictedState;
                        hprob += (int)(singlePrediction.getPropH()*10);
                        eprob += (int)(singlePrediction.getProbE()*10);
                        cprob += (int)(singlePrediction.getProbC()*10);
                    } else {
                        // if AA is ambigous return C
                        prediction += "C";
                    }
                }
                for(int i=0;i<Math.floorDiv(windowsize, 2);i++){
                    prediction += "-";
                    hprob += "-";
                    eprob += "-";
                    cprob += "-";
                }
                String[] outcome = {sequence, prediction, hprob, eprob, cprob};
                predStructure.put(header, outcome);
            } else {
                String prediction = "";
                String hprob = "";
                String eprob = "";
                String cprob = "";
                for(int i=0;i<sequence.length();i++){
                    prediction += "-";
                    hprob += "-";
                    eprob += "-";
                    cprob += "-";
                }
                String[] outcome = {sequence, prediction, hprob, eprob, cprob};
                predStructure.put(header, outcome);
            }
        }
        return predStructure;
    }
}

