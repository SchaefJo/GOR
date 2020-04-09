package gors.training;

import seqlib.SeqlibEntry;
import storages.PositionAaStorage;

import java.util.ArrayList;

public class Gor4training {
    private int windowsize;

    public Gor4training(int windowsize) {
        this.windowsize = windowsize;
    }

    /**
     * train a PositionAaStorage with sequences
     * @param input ArrayList with Seqlib entries
     * @return PositionAaStorage which are 17 matrices of AaAAStorages
     */
    public PositionAaStorage training(ArrayList<SeqlibEntry> input) {
        PositionAaStorage data = new PositionAaStorage(windowsize);
        // for all sequences
        for (SeqlibEntry seqlib_entry : input) {
            String seq = seqlib_entry.getAminoacid_seq();
            String struct = seqlib_entry.getSecondary_struc_seq();
            String curr_window;
            // for all windows in one sequence
            for (int i = 0; i < seq.length() - windowsize + 1; i++) {
                curr_window = seq.substring(i, i + windowsize);
                char middleAA = curr_window.charAt((int) Math.floorDiv(windowsize, 2));
                char middleStruct = struct.charAt(i+ (int)Math.floor(windowsize / 2.0));
                // for all 17 positions
                for (int pos=0; pos < windowsize; pos++){
                    char AAPos = curr_window.charAt(pos);
                    data.trainStates(curr_window, windowsize, middleStruct, middleAA, AAPos, pos);
                }
            }
        }
        return data;
    }
}
