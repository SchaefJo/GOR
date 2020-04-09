package gors.predict.Gor5;

public class g5seq {
    String seq;
    String seqWOGaps;
    int[] HProbs;
    int[] CProbs;
    int[] EProbs;

    public g5seq(String seq) {
        this.seq = seq;
        seqWOGaps = stripGaps(seq);
    }

    public String getSeq() {
        return seq;
    }


    public int[] getHProbs() {
        return HProbs;
    }

    public int[] getCProbs() {
        return CProbs;
    }

    public int[] getEProbs() {
        return EProbs;
    }

    public String stripGaps(String s){
        String gapless = "";
        for(char c : s.toCharArray() ){
            if(c != '-') gapless += c;
        }
        return gapless;
    }


    public void setHProbs(String probsWOGaps, int windowsize) {
       HProbs = addGaps(windowsize, probsWOGaps);
    }

    public void setCProbs(String probsWOGaps, int windowsize) {
        CProbs = addGaps(windowsize, probsWOGaps);
    }

    public void setEProbs(String probsWOGaps, int windowsize) {
        EProbs = addGaps(windowsize, probsWOGaps);
    }

    public String getSeqWOGaps() {
        return seqWOGaps;
    }

    /**
     * add the gaps again to the sequence predictions
     * @param windowsize int
     * @param probsWOGaps probability string for the predictions
     * @return int[] where the probabilities from the String and the 0 probability for the gaps were added
     */
    public int[] addGaps(int windowsize, String probsWOGaps){
        int cutOffs = Math.floorDiv(windowsize,2);
        int[] ar = new int[seq.length()- cutOffs];
        int gapCounter = 0;
        for(int i =cutOffs; i<seq.length()-cutOffs; i++){
            char curChar = seq.charAt(i);
            if(probsWOGaps.length() <= (i-gapCounter)) break;
            char noGapChar = probsWOGaps.charAt(i - gapCounter);
                if (curChar == '-') {
                    ar[i - gapCounter - cutOffs] = 0;
                    gapCounter++;
                } else ar[i - gapCounter - cutOffs] = Character.getNumericValue(noGapChar);
        }
        return ar;





    }


}


