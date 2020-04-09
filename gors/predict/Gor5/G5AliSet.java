package gors.predict.Gor5;

import java.util.ArrayList;

public class G5AliSet {
    /**
     * class with all information about multiple alginments
     */
    String id;
    String aseq;
    String sseq;
    double[] H;
    double[] C;
    double[] E;
    int[] counter;
    ArrayList<g5seq> alignments = new ArrayList<>();
    String prediction;
    String ph;
    String pc;
    String pe;

    public String getPrediction() {
        return prediction;
    }

    public String getPh() {
        return ph;
    }

    public String getPc() {
        return pc;
    }

    public String getPe() {
        return pe;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public G5AliSet() {
    }

    public double[] getH() {
        return H;
    }


    public double[] getC() {
        return C;
    }


    public double[] getE() {
        return E;
    }


    public G5AliSet(String id, String aseq, String sseq) {
        this.id = id;
        this.aseq = aseq;
        this.sseq = sseq;

    }

    public g5seq getAlignment(int i){
        return alignments.get(i);
    }

    public void appendAlignment(g5seq aliSeq){
        alignments.add(aliSeq);
    }

    public String getAseq() {
        return aseq;
    }

    public void setAseq(String aseq) {
        this.aseq = aseq;
        this.counter = new int[aseq.length()];
        this.H = new double[aseq.length() - 16];
        this.C = new double[aseq.length() - 16];
        this.E = new double[aseq.length() - 16];
    }

    public String getSseq() {
        return sseq;
    }

    public void setSseq(String sseq) {
        this.sseq = sseq;
    }


    public ArrayList<g5seq> getAlignments() {
        return alignments;
    }

    public void setAlignments(ArrayList<g5seq> alignments) {
        this.alignments = alignments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
