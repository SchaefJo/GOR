package storages;

public class PositionAaStorage extends BaseStorage {
    private AaAAStorage[] positions;

    public char[] AAs = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'Y'};

    public PositionAaStorage(int windowsize){
        this.positions = new AaAAStorage[windowsize];
        for(int i=0; i<positions.length; i++){
            positions[i] = new AaAAStorage(windowsize);
        }

    }

    public void trainStates(String curr_window, int windowsize, char middleStruct, char middleAA, char AAPos, int pos){
        positions[pos].trainStates(curr_window, windowsize, middleStruct, middleAA, AAPos);
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

    public AaAAStorage get(int i){
        return this.positions[i];
    }

    public void set(int i, AaAAStorage store){
        positions[i] = store;
    }

    public AaAAStorage[] getPositions() {
        return positions;
    }
}
