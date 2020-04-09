package storages;

import java.util.HashMap;

public class AaAAStorage extends BaseStorage{
    private HashMap<Character, AAStateStorage> outerAminoacids;

    public char[] AAs = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'Y'};

    public AaAAStorage(int windowsize){
        this.outerAminoacids = new HashMap<>();
        outerAminoacids.put('A', new AAStateStorage(windowsize));
        outerAminoacids.put('C', new AAStateStorage(windowsize));
        outerAminoacids.put('D', new AAStateStorage(windowsize));
        outerAminoacids.put('E', new AAStateStorage(windowsize));
        outerAminoacids.put('F', new AAStateStorage(windowsize));
        outerAminoacids.put('G', new AAStateStorage(windowsize));
        outerAminoacids.put('H', new AAStateStorage(windowsize));
        outerAminoacids.put('I', new AAStateStorage(windowsize));
        outerAminoacids.put('K', new AAStateStorage(windowsize));
        outerAminoacids.put('L', new AAStateStorage(windowsize));
        outerAminoacids.put('M', new AAStateStorage(windowsize));
        outerAminoacids.put('N', new AAStateStorage(windowsize));
        outerAminoacids.put('P', new AAStateStorage(windowsize));
        outerAminoacids.put('Q', new AAStateStorage(windowsize));
        outerAminoacids.put('R', new AAStateStorage(windowsize));
        outerAminoacids.put('S', new AAStateStorage(windowsize));
        outerAminoacids.put('T', new AAStateStorage(windowsize));
        outerAminoacids.put('V', new AAStateStorage(windowsize));
        outerAminoacids.put('W', new AAStateStorage(windowsize));
        outerAminoacids.put('Y', new AAStateStorage(windowsize));
    }

    public HashMap<Character, AAStateStorage> getOuterAminoacids() {
        return outerAminoacids;
    }

    public void trainStates(String curr_window, int windowsize, char middleStruct, char middleAA, char AAPos){
        if (checkAA(AAPos)) {
            outerAminoacids.get(AAPos).trainStates(curr_window, windowsize, middleStruct, middleAA);
        }
    }

    public boolean checkAA(char aa){
        for (char curA : AAs){
            if (curA == aa){
                return true;
            }
        }
        return false;
    }



    public char[] getAAs() {
        return AAs;
    }
}
