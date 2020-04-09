package gors.training;

import seqlib.SeqlibEntry;
import storages.StateMatrixStorage;

import java.util.ArrayList;

public class Gor1training {
    private int windowsize;

    public Gor1training(int windowsize) {
        this.windowsize = windowsize;
    }

    /**
     * train a StateMatrixStorage with sequences
     * @param input ArrayList with Seqlib entries
     * @return StateMatrixStorage which are three matrices for the secondary structures H,E and C
     */
    public StateMatrixStorage training(ArrayList<SeqlibEntry> input) {
        StateMatrixStorage data = new StateMatrixStorage(windowsize);
        // go through all sequences
        for (SeqlibEntry seqlib_entry : input) {
            String seq = seqlib_entry.getAminoacid_seq();
            String struct = seqlib_entry.getSecondary_struc_seq();
            String curr_window;
            // go through all windows in one sequence
            for (int i = 0; i < seq.length() - windowsize + 1; i++) {
                curr_window = seq.substring(i, i + windowsize);
                data.trainStates(curr_window, windowsize, struct.charAt(i + (int)Math.floor(windowsize / 2.0)));
            }
        }
        return data;
    }
}
