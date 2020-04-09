package gors.predict;

import storages.Matrix;
import storages.StateMatrixStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Gor1predict {
    private StateMatrixStorage logedStateMatrix;
    private int windowsize;
    private double pseudocounts;


    public Gor1predict(StateMatrixStorage normalStateMatrix, int windowsize, double pseudocounts) {
        this.windowsize = windowsize;
        this.logedStateMatrix = logStateMatrix(normalStateMatrix);
        this.pseudocounts = pseudocounts;
    }

    /**
     * calculate logged Matrix from absolute count Matrix
     * @param normalStateMatrix StateMatrixStorage with counts
     * @return StateMatrixStorage with propensities
     */
    public StateMatrixStorage logStateMatrix(StateMatrixStorage normalStateMatrix){
        StateMatrixStorage logStateMatrix = new StateMatrixStorage(windowsize);

        double colSum = 0;
        double hSum = 0;
        double eSum = 0;
        double cSum = 0;
        double pseudocounts = 0;//Math.pow(10, -6);
        double curState;
        Matrix logMatrix;
        // loop through indexes of secondary structures
        for(int i=0; i < normalStateMatrix.getStructures().length; i++){
            // get 2d Matrix for a certain structure
            Matrix curMatrix = normalStateMatrix.getStructures()[i];
            // append the sum of the middle column
            double curColSum = curMatrix.sumColumn((int) Math.floor(windowsize / 2.0), pseudocounts);
            colSum += curColSum;
            if(i == normalStateMatrix.getCIND()) cSum = curColSum;
            else if(i == normalStateMatrix.getHIND()) hSum = curColSum;
            else eSum = curColSum;
        }
        for (int i=0; i < normalStateMatrix.getStructures().length; i++){
            Matrix curMatrix = normalStateMatrix.getStructures()[i];
            double curField;
            double notCurField;
            double log_1, log_2;
            // go through all AAs
            for (char curAA: normalStateMatrix.getAAs()){
                // go through all positions
                for (int position = 0; position < windowsize; position++){
                    if (i == normalStateMatrix.getHIND()) curState = hSum;
                    else if (i == normalStateMatrix.getCIND()) curState = cSum;
                    else curState = eSum;
                    curField = curMatrix.getValue(curAA, position) + pseudocounts;
                    notCurField = 0;
                    for (int j=0; j< normalStateMatrix.getStructures().length; j++){
                        Matrix tempMatrix = normalStateMatrix.getStructures()[j];
                        if (j != i){
                            notCurField += tempMatrix.getValue(curAA, position) + pseudocounts;
                        }
                    }
                    // calculate propensities
                    log_1 = Math.log(curField) - Math.log(notCurField);
                    log_2 = Math.log(colSum - curState) - Math.log(curState);

                    logMatrix = logStateMatrix.getStructures()[i];
                    double logPos = logMatrix.getValue(curAA, position) + log_1 + log_2;
                    // update Matrix
                    logMatrix.setValue(logPos, curAA, position);
                }
            }
        }
        return logStateMatrix;
    }

    /**
     * calculate the sum of one window
     * @param stateMatrix Matrix for one state
     * @param curWindow char array with one window
     * @return the sum of the window from the matrix
     */
    public double WindowSum(Matrix stateMatrix, char[] curWindow){
        double stateSum = 0;
        for (int i = 0; i < windowsize; i++){
            if (stateMatrix.checkAA(curWindow[i])) {
                stateSum += stateMatrix.getValue(curWindow[i], i);
            }
        }
        return stateSum;
    }

    /**
     * make prediction for one window
     * @param logOddStateMatrix StateMatrixStorage of log odds
     * @param curWindow window as a char array
     * @return Single Prediction object
     */
    public SinglePrediction WindowPrediction(StateMatrixStorage logOddStateMatrix, char[] curWindow){
        double sumH = 0;
        double sumC = 0;
        double sumE = 0;
        double deltaE, deltaC, deltaH;

        for(int i=0; i<logOddStateMatrix.getStructures().length; i++){
            double sum = WindowSum(logOddStateMatrix.getStructures()[i], curWindow);
            if(i == logOddStateMatrix.getCIND()) sumC = sum;
            else if (i == logOddStateMatrix.getHIND()) sumH = sum;
            else sumE = sum;
        }
        deltaE = sumE;
        deltaC = sumC;
        deltaH = sumH;
        double deltaSum = deltaC + deltaE + deltaH;
        double curDelta = 0;

        double pnprobH = 0;
        double pnprobC = 0;
        double pnprobE = 0;
        double curProp;
        for (int i=0; i<logOddStateMatrix.getStructures().length; i++){
            if(i == logOddStateMatrix.getCIND()) curDelta = deltaC;
            else if (i == logOddStateMatrix.getHIND()) curDelta = deltaH;
            else curDelta = deltaE;
            curProp = Math.exp(curDelta) / deltaSum;
            if(i == logOddStateMatrix.getCIND()) pnprobC = curProp;
            else if (i == logOddStateMatrix.getHIND()) pnprobH = curProp;
            else pnprobE = curProp;
        }
        double probSum = pnprobC + pnprobE + pnprobH;
        double probC = pnprobC / probSum;
        double probH = pnprobH / probSum;
        double probE = pnprobE / probSum;

        char predState = calcState(probC, probE, probH);
        return new SinglePrediction(predState, probH, probC, probE);
    }

    /**
     * calculates which probability is the biggest, returns the structure as a char
     * @param probC probability for structure C
     * @param probE probability for structure E
     * @param probH probability for structure H
     * @return char of the predicted structure
     */
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

    public StateMatrixStorage getLogedStateMatrix() {
        return logedStateMatrix;
    }

    /**
     * makes predictions for a hashmap of sequences
     * @param sequences hashmap
     * @return hashmap with sequence id as a key and the predictions in a String array
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
                // make predictions for all windows
                for (int i = 0; i < seq_array.length - windowsize + 1; i++) {
                    char[] currentWindow = Arrays.copyOfRange(seq_array, i, i + 17);
                    singlePrediction = WindowPrediction(this.logedStateMatrix, currentWindow);
                    prediction += singlePrediction.predictedState;
                    hprob += (int)(singlePrediction.getPropH()*10);
                    eprob += (int)(singlePrediction.getProbE()*10);
                    cprob += (int)(singlePrediction.getProbC()*10);
                }
                for(int i=0;i<Math.floorDiv(windowsize, 2);i++){
                    prediction += "-";
                    hprob += "-";
                    eprob += "-";
                    cprob += "-";
                }
                String[] outcome = {sequence, prediction, hprob, eprob, cprob};
                predStructure.put(header, outcome);
            // if sequence is too short
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
