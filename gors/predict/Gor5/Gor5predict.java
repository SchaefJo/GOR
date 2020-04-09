package gors.predict.Gor5;


import ReaderWriter.MAReader;
import ReaderWriter.PredictionsWriter;
import gors.predict.Gor1predict;
import gors.predict.Gor3predict;
import gors.predict.Gor4predict;
import storages.AAStateStorage;
import storages.BaseStorage;
import storages.PositionAaStorage;
import storages.StateMatrixStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Gor5predict {
    ArrayList<G5AliSet> data;

    public ArrayList<G5AliSet> getData() {
        return data;
    }

    /**
     * makes predictions for gor5
     * @param dir path to a directory with multiple alignments
     * @param gorMethod gor1, gor3 or gor3
     * @param storage Storage class
     * @param windowsize
     */
    public Gor5predict(String dir, String gorMethod, BaseStorage storage[], int windowsize, double pseudocounts) {
        MAReader reader = new MAReader();
        this.data = reader.readFolder(new File(dir));
        // for all sequences in one alignment file
        for(G5AliSet curAliSet : data){
            HashMap<String, String[]> predictions;
            HashMap<String, String> sequences = new HashMap<>();

            for(int i=0; i < curAliSet.getAlignments().size(); i++){
                g5seq s = curAliSet.getAlignment(i);
                sequences.put(String.valueOf(i),s.getSeqWOGaps());
            }

            if(gorMethod.equals("gor1")){
                Gor1predict gor1 = new Gor1predict((StateMatrixStorage) storage[0], windowsize, pseudocounts);
                predictions = gor1.prediction(sequences);
                for(int i=0; i<curAliSet.getAlignments().size(); i++){
                    g5seq curSeq = curAliSet.getAlignment(i);
                    String[] preds = predictions.get(String.valueOf(i));
                    curSeq.setHProbs(preds[2], windowsize);
                    curSeq.setEProbs(preds[3], windowsize);
                    curSeq.setCProbs(preds[4], windowsize);
                }
            }
            else if(gorMethod.equals("gor3")){
                Gor3predict gor3 = new Gor3predict((AAStateStorage) storage[0], windowsize, pseudocounts);
                predictions = gor3.prediction(sequences);
                for(int i=0; i<curAliSet.getAlignments().size(); i++){
                    g5seq curSeq = curAliSet.getAlignment(i);
                    String[] preds = predictions.get(String.valueOf(i));
                    curSeq.setHProbs(preds[2], windowsize);
                    curSeq.setEProbs(preds[3], windowsize);
                    curSeq.setCProbs(preds[4], windowsize);
                }
            }
            else {
                // gor4
                PositionAaStorage gor4train = (PositionAaStorage) storage[0];
                AAStateStorage gor3train = (AAStateStorage) storage[1];
                Gor4predict gor4 = new Gor4predict(gor4train, gor3train, windowsize, pseudocounts);
                predictions = gor4.prediction(sequences);
                for(int i=0; i<curAliSet.getAlignments().size(); i++){
                    g5seq curSeq = curAliSet.getAlignment(i);
                    String[] preds = predictions.get(String.valueOf(i));
                    curSeq.setHProbs(preds[2], windowsize);
                    curSeq.setEProbs(preds[3], windowsize);
                    curSeq.setCProbs(preds[4], windowsize);
                }
            }

            int cutoff = Math.floorDiv(windowsize,2);
            for(int seqIndex=cutoff; seqIndex<curAliSet.getAseq().length()- cutoff; seqIndex++){
                for(int stateIndex = 0; stateIndex<3; stateIndex++){
                    for(g5seq alignmentSequence : curAliSet.getAlignments()){
                        if(alignmentSequence.getSeq().length()-1 < seqIndex) continue;
                        if(stateIndex == 0) {
                           if(alignmentSequence.getSeq().charAt(seqIndex) != '-') curAliSet.counter[seqIndex-cutoff]++;
                            curAliSet.H[seqIndex -cutoff] += alignmentSequence.HProbs[seqIndex-cutoff];
                        }
                        else if(stateIndex == 1) {
                            curAliSet.E[seqIndex -cutoff] += alignmentSequence.EProbs[seqIndex-cutoff];
                        }
                        else{
                            curAliSet.C[seqIndex -cutoff] += alignmentSequence.CProbs[seqIndex-cutoff];
                        }
                    }
                }
            }
            String prediction = "";
            String ph = "";
            String pe = "";
            String pc = "";
            for(int i=0;i<cutoff;i++){
                prediction+="-";
                ph +="-";
                pe+="-";
                pc+="-";
            }
            for(int seqIndex=cutoff; seqIndex<curAliSet.getAseq().length()-cutoff; seqIndex++){
                double helixprob = 0;
                double sheetprob = 0;
                double coilprob = 0;
                if(curAliSet.counter[seqIndex-cutoff] != 0) {
                    helixprob = curAliSet.H[seqIndex - cutoff] / curAliSet.counter[seqIndex - cutoff];
                    coilprob = curAliSet.C[seqIndex - cutoff] / curAliSet.counter[seqIndex - cutoff];
                    sheetprob = curAliSet.E[seqIndex - cutoff] / curAliSet.counter[seqIndex - cutoff];
                }

                prediction += calcState(coilprob, sheetprob, helixprob);
                ph += (int)(helixprob) > 9 ? 9 : (int)helixprob;
                pe += (int)(sheetprob) > 9 ? 9 : (int)sheetprob;
                pc += (int)(coilprob) > 9 ? 9 : (int)coilprob;
            }
            for(int i=0;i<cutoff;i++){
                prediction+="-";
                ph +="-";
                pe+="-";
                pc+="-";
            }
            curAliSet.setPrediction(prediction);
            curAliSet.setPc(pc);
            curAliSet.setPe(pe);
            curAliSet.setPh(ph);
        }
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
}
