package gors.predict;

import storages.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Gor4predict {
    private PositionAaStorage gor4Storage;
    private AAStateStorage gor3Storage;
    private int halfWindowSize;
    private int windowsize;
    private double pseudocount;
    private char[] states = {'H', 'E', 'C'};

    public Gor4predict(PositionAaStorage gor4Train, AAStateStorage gor3Train, int windowsize, double pseudocount) {
        this.windowsize = windowsize;
        this.gor4Storage = gor4Train;
        this.gor3Storage = gor3Train;
        this.halfWindowSize = Math.floorDiv(windowsize, 2);
        this.pseudocount = pseudocount;
    }

    /**
     * calculate first log formula
     * @param state current structure
     * @param StateMatrix StateMatrixStorage
     * @param innerPos position in the 17er window
     * @param innerAA AA at the innerPos
     * @return double
     */
    public double calcLogs(char state, StateMatrixStorage StateMatrix, int innerPos, char innerAA){
        double Nenner = 0;
        double Zaehler = 0;
        Nenner = StateMatrix.get(state).getValue(innerAA, innerPos)+pseudocount;
        for(char curState: states){
            if (curState != state){
                Zaehler += StateMatrix.get(curState).getValue(innerAA, innerPos)+pseudocount;
            }
        }
        return Math.log(Nenner/Zaehler);

    }

    /**
     * calculate second log formula
     * @param state current structure
     * @param StateMatrix StateMatrixStorage
     * @param pos position in the 17er window
     * @param aa AA at pos in the window
     * @return
     */
    public double calcLogs2(char state, StateMatrixStorage StateMatrix, int pos, char aa){
        double Nenner = 0;
        double Zaehler = 0;
        Nenner = StateMatrix.get(state).getValue(aa, pos)+pseudocount;
        for (char curState: states){
            if (curState != state){
                Zaehler += StateMatrix.get(curState).getValue(aa, pos)+pseudocount;
            }
        }
        return Math.log(Nenner/Zaehler);
    }

    public char calcState(double probC, double probE, double probH){
        if (probC > probE && probC > probH) return 'C';
        else if (probE > probC && probE > probH) return 'E';
        else if (probH > probE && probH > probC) return 'H';
        else {
            if (probC == probE) return 'C';
            else if (probC == probH) return 'C';
            else return 'H';
        }
    }

    /**
     * make predictions for one window
     * @param currentWindow char array for one window
     * @param middleAA char in the middle of the window
     * @return Single Prediction object
     */
    public SinglePrediction windowPrediction(char[] currentWindow, char middleAA){
        SinglePrediction singlePrediction;
        double probH = 0;
        double probE = 0;
        double probC = 0;
        double result = 0;
        double resultFirstLog = 0;
        double resultSecondLog = 0;
        // go through all states, positions and inner positions
        for (char state: states){
            double firstLogSum = 0;
            double secondLogSum = 0;
            for (int pos=0; pos < windowsize; pos++){
                char curAA = currentWindow[pos];
                if (gor3Storage.checkAA(curAA)) {
                    StateMatrixStorage StateMatrix = this.gor4Storage.get(pos).getOuterAminoacids().get(curAA).getAA(middleAA);
                    for (int innerPos = pos + 1; innerPos < windowsize; innerPos++) {
                        char AAInnerPos = currentWindow[innerPos];
                        if (gor3Storage.checkAA(AAInnerPos)) {
                            firstLogSum += calcLogs(state, StateMatrix, innerPos, AAInnerPos);
                        }
                    }
                }
            }
            resultFirstLog = firstLogSum * (2.0 / windowsize);

            StateMatrixStorage StateMatrix = this.gor3Storage.getAA(middleAA);
            for (int pos = 0; pos < windowsize; pos++) {
                char curAA = currentWindow[pos];
                if (gor3Storage.checkAA(curAA)) {
                    secondLogSum += calcLogs2(state, StateMatrix, pos, curAA);
                }
            }
            resultSecondLog = secondLogSum * ((windowsize - 2.0)/(windowsize));
            result = resultFirstLog - resultSecondLog;
            if (state == 'H') probH = result;
            else if (state == 'E') probE = result;
            else probC = result;
        }
        double finalProbSum = probC + probE + probH;
        double finalProbH = (Math.exp(probH)) / finalProbSum;
        double finalProbE = (Math.exp(probE)) / finalProbSum;
        double finalProbC = (Math.exp(probC)) / finalProbSum;
        double normalization = finalProbC + finalProbE + finalProbH;
        finalProbH = finalProbH / normalization;
        finalProbE = finalProbE / normalization;
        finalProbC = finalProbC / normalization;

        char predictionChar = calcState(finalProbC, finalProbE, finalProbH);
        singlePrediction = new SinglePrediction(predictionChar, finalProbH, finalProbC, finalProbE);
        return singlePrediction;
    }

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
                    if (gor3Storage.checkAA(middleAA)) {
                        singlePrediction = windowPrediction(currentWindow, middleAA);
                        prediction += singlePrediction.predictedState;
                        hprob += (int) (singlePrediction.getPropH() * 10);
                        eprob += (int) (singlePrediction.getProbE() * 10);
                        cprob += (int) (singlePrediction.getProbC() * 10);
                    } else {
                        prediction += 'C';
                        hprob += 0;
                        eprob += 0;
                        cprob += 0;
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
            }  else {
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
