package storages;

import java.util.HashMap;

public class AAStateStorage extends BaseStorage{
    private HashMap<Character, StateMatrixStorage> aminoacids;

    public char[] AAs = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'Y'};

    public AAStateStorage(int windowsize){
        this.aminoacids = new HashMap<>();
        aminoacids.put('A', new StateMatrixStorage(windowsize));
        aminoacids.put('C', new StateMatrixStorage(windowsize));
        aminoacids.put('D', new StateMatrixStorage(windowsize));
        aminoacids.put('E', new StateMatrixStorage(windowsize));
        aminoacids.put('F', new StateMatrixStorage(windowsize));
        aminoacids.put('G', new StateMatrixStorage(windowsize));
        aminoacids.put('H', new StateMatrixStorage(windowsize));
        aminoacids.put('I', new StateMatrixStorage(windowsize));
        aminoacids.put('K', new StateMatrixStorage(windowsize));
        aminoacids.put('L', new StateMatrixStorage(windowsize));
        aminoacids.put('M', new StateMatrixStorage(windowsize));
        aminoacids.put('N', new StateMatrixStorage(windowsize));
        aminoacids.put('P', new StateMatrixStorage(windowsize));
        aminoacids.put('Q', new StateMatrixStorage(windowsize));
        aminoacids.put('R', new StateMatrixStorage(windowsize));
        aminoacids.put('S', new StateMatrixStorage(windowsize));
        aminoacids.put('T', new StateMatrixStorage(windowsize));
        aminoacids.put('V', new StateMatrixStorage(windowsize));
        aminoacids.put('W', new StateMatrixStorage(windowsize));
        aminoacids.put('Y', new StateMatrixStorage(windowsize));
    }

    public void trainStates(String curr_window, int windowsize, char middleStruct, char middle_aa){
        if (checkAA(middle_aa)) {
            aminoacids.get(middle_aa).trainStates(curr_window, windowsize, middleStruct);
        }
    }

    public void setAA(char aminoacid, StateMatrixStorage new_sms){
        aminoacids.put(aminoacid, new_sms);
    }

    public boolean checkAA(char aa){
        for (char curA : AAs){
            if (curA == aa){
                return true;
            }
        }
        return false;
    }

    public StateMatrixStorage getAA(char aminoacid){
        return aminoacids.get(aminoacid);
    }

    public HashMap<Character, StateMatrixStorage> getAminoacids() {
        return aminoacids;
    }

    public char[] getAAs() {
        return AAs;
    }
}
