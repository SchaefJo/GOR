package storages;

import java.util.HashMap;
import java.util.Map;

public class Matrix {
    private double[][] matrix;
    private int windowsize;
    private HashMap<Character,Integer> aa_indices;
    public final char[] aas = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'Y'};

    public Map<Character, Integer> getAAIndices() {
        return aa_indices;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public HashMap<Character, Integer> getAAHash(){
        HashMap<Character, Integer> aa_hash = new HashMap<>();
        for (int i=0; i<this.aas.length; i++){
            aa_hash.put(aas[i], i);
        }
        return aa_hash;
    }

    public Matrix(int windowsize) {
        int aa_count = 20;
        int window_length = windowsize;
        this.matrix = new double[aa_count][window_length];
        for (int i=0;i<aa_count;i++){
            for (int j=0;j<window_length;j++){

                this.matrix[i][j] = 0;
            }
        }
        this.aa_indices = getAAHash();
    }

    public double sumRow(char aa) {
        int window_length = windowsize;

        int row_index = getAAIndex(aa);
        double row_sum = 0;
        for (int i = 0; i < window_length; i++) {
            row_sum += this.matrix[row_index][i];
        }
        return row_sum;
    }


    public void setValue(double value, char aa, int col){
        this.matrix[getAAIndex(aa)][col] = value;
    }


    public double sumColumn(int column_index, double pseudocount) {
        int aa_count = 20;

        int col_index = column_index;
        double col_sum = 0;
        for (int i = 0; i < aa_count; i++) {
            col_sum += this.matrix[i][col_index] + pseudocount;
        }
        return col_sum;
    }

    public void increment(char aa, int window_pos) {
        if (this.aa_indices.containsKey(aa)) {
            int row_index = getAAIndex(aa);
            int col_index = window_pos;
            this.matrix[row_index][col_index] += 1;
        }
    }

    public double getValue(char aa, int window_pos) {
        int row_index = getAAIndex(aa);
        int col_index = window_pos;
        return this.matrix[row_index][col_index];
    }

    public boolean checkAA(char aa){
        for (char curA : aas){
            if (curA == aa){
                return true;
            }
        }
        return false;
    }

    public int getAAIndex(char aa) {
        return this.aa_indices.get(aa);
    }

    public void printMatrix(){
        for (double[] row : this.matrix)
        {
            for (double window_pos_val : row)
            {
                System.out.print(window_pos_val + " ");
            }
            System.out.println();
        }
    }
    public String retPrintMatrix(int pos){
        String ret = "";
        int i = 0;

        for (double[] row : this.matrix)
        {
            ret += aas[i] + "\t";
            int x = 0;
            for (double window_pos_val : row)
            {
                if (x > pos){
                    ret += ((int) window_pos_val + "\t");
                }
                else ret += (0 + "\t");
                x++;
            }
            ret += "\n";
            i++;
        }
        return ret;
    }
}