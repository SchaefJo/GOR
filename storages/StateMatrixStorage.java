package storages;

public class StateMatrixStorage extends BaseStorage {
    private final int HIND = 0;
    private final int CIND = 1;
    private final int EIND = 2;


    private Matrix[] structures;
    private final char[] AAs = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'Y'};

    public char[] getAAs() {
        return AAs;
    }

    public StateMatrixStorage(Matrix h, Matrix c, Matrix e) {
        this.structures = new Matrix[]{h, c, e};
    }

    public StateMatrixStorage(int windowsize) {
        this.structures = new Matrix[]{new Matrix(windowsize), new Matrix(windowsize), new Matrix(windowsize)};
    }

    public void trainStates(String curr_window, int windowsize, char middleStruct){
        if (middleStruct == 'H') {
            trainWindow(structures[HIND], curr_window, windowsize);
        } else if (middleStruct == 'C') {
            trainWindow(structures[CIND], curr_window, windowsize);
        } else if (middleStruct == 'E') {
            trainWindow(structures[EIND], curr_window, windowsize);
        }
    }

    public Matrix trainWindow(Matrix count_matrix, String window, int windowlength){
        for (int i=0; i<windowlength; i++){
            char current_aa  = window.charAt(i);
            if(checkAA(current_aa)){
                count_matrix.increment(current_aa, i);
            }
        }
        return  count_matrix;
    }

    public Matrix get(char struc){
        if(struc == 'H') return structures[HIND];
        if(struc == 'C') return structures[CIND];
        else return structures[EIND];
    }

    public boolean checkAA(char aa){
        for (char curA : AAs){
            if (curA == aa){
                return true;
            }
        }
        return false;
    }

    public Matrix[] getStructures() {
        return structures;
    }


    public void setStructures(Matrix[] structures) {
        this.structures = structures;
    }

    public Matrix getH() {
        return structures[HIND];
    }

    public void setH(Matrix h) {
        this.structures[HIND] = h;
    }

    public Matrix getC() {
        return structures[CIND];
    }

    public void setC(Matrix c) {
        this.structures[CIND] = c;
    }

    public Matrix getE() {
        return structures[EIND];
    }

    public void setE(Matrix e) {
        this.structures[EIND] = e;
    }

    public int getHIND() {
        return HIND;
    }

    public int getCIND() {
        return CIND;
    }

    public int getEIND() {
        return EIND;
    }
}
