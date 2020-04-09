package gors.training;

import seqlib.SeqlibEntry;
import storages.AAStateStorage;

import java.util.ArrayList;

public class Gor3training {
    private int windowsize;

    public Gor3training(int windowsize) {
        this.windowsize = windowsize;
    }

    /**
     * train a StateMatrixStorage with sequences
     * @param input ArrayList with Seqlib entries
     * @return AAStateStorage which are 20 matrices of StateMatrixStorage for the 20 AAs
     */
    public AAStateStorage training(ArrayList<SeqlibEntry> input) {
        AAStateStorage data = new AAStateStorage(windowsize);
        // for all sequences
        for (SeqlibEntry seqlib_entry : input) {
            String seq = seqlib_entry.getAminoacid_seq();
            String struct = seqlib_entry.getSecondary_struc_seq();
            String curr_window;
            // for all windows in one sequence
            for (int i = 0; i < seq.length() - windowsize + 1; i++) {
                curr_window = seq.substring(i, i + windowsize);
                char middle_aa = curr_window.charAt((int) Math.floorDiv(windowsize, 2));
                char middleStruct = struct.charAt(i+ (int)Math.floor(windowsize / 2.0));
                data.trainStates(curr_window, windowsize, middleStruct, middle_aa);
            }
        }
        return data;
    }
}
