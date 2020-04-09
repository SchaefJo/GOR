package ReaderWriter;

import storages.*;

import java.io.*;
import java.util.HashMap;

public class modelParser {

    /**
     * peeks into file and looks for gor method that can use the trained model for prediction
     * @param model_file input trained file for prediction
     * @return string of model to be trained
     * @throws IOException
     */
    public String getModel(File model_file) throws IOException {
        String model = "";
        FileReader fr = new FileReader(model_file);
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        if (line.equals("// Matrix3D")) {
            model = "gor1";
        } else if (line.equals("// Matrix4D")) {
            model = "gor3";
        } else if (line.equals("// Matrix6D")){
            model = "gor4";
        }
        return model;
    }

    /**
     * reads a training model file into an StateMatrixStorage object for gor1 prediction
     * @param model_file gor1 trained model file input
     * @return StateMatrixStorage object
     * @throws IOException
     */
    public StateMatrixStorage gor1ModelToStateMatrix(File model_file, int windowsize) throws IOException {
        StateMatrixStorage gor1_model = new StateMatrixStorage(windowsize);
        FileReader fr = new FileReader(model_file);
        BufferedReader reader = new BufferedReader(fr);
        Character structure = null;
        char aminoacid;
        String line;
        Matrix struct_matrix = new Matrix(windowsize);

        while ((line = reader.readLine()) != null) {
            if (line.length() > 0 && line.charAt(0) != '/') {
                if (line.charAt(0) == '='){
                    structure = line.charAt(1);
                    struct_matrix = new Matrix(windowsize);
                } else {
                    aminoacid = line.charAt(0);
                    String[] training_values = line.substring(2).split("\t");
                    for (int i = 0; i < training_values.length; i++) {
                        struct_matrix.setValue(Double.parseDouble(training_values[i]), aminoacid, i);
                    }
                }
            } if (structure != null) {
                if (structure == 'C') {
                    gor1_model.setC(struct_matrix);
                } else if (structure == 'E') {
                    gor1_model.setE(struct_matrix);
                } else if (structure == 'H') {
                    gor1_model.setH(struct_matrix);
                }
            }
        }
        return gor1_model;
    }

    /**
     * reads a training model file into an AAStateStorage object for gor3 prediction
     * @param model_file gor3 trained model file input
     * @return AAStateStorage object
     */
    public AAStateStorage gor3ModelToAAState(File model_file, int windowsize) {
        AAStateStorage aastate = new AAStateStorage(windowsize);
        try {
        FileReader fr = new FileReader(model_file);
        BufferedReader reader = new BufferedReader(fr);
        String line;
        char outerKey = ' ';
        char innerKey = ' ';
        boolean foundStart = false;
        while ((line = reader.readLine()) != null) {
            if(!foundStart && line.length() > 0 && line.charAt(0) == '/' && line.charAt(9) == '4') foundStart = true;
                if(foundStart) {
                    if (line.length() > 0 && line.charAt(0) != '/' && !line.equals("\t")) {
                        if (line.charAt(0) == '=') {
                            outerKey = line.charAt(1);
                            innerKey = line.charAt(3);
                        }
                        //matrix line
                        else {
                            StateMatrixStorage state = aastate.getAminoacids().get(outerKey);
                            Matrix mat = state.get(innerKey);
                            String[] training_values = line.trim().split("\t");
                            char aminoacid = line.charAt(0);

                            for (int i = 1; i < training_values.length; i++) {
                                int value = (Integer.parseInt(training_values[i]));
                                mat.setValue(value, aminoacid, i - 1);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Issue while reading the file");
            e.printStackTrace();
        }
        return aastate;
    }

    /**
     * reads a training model file into an BaseStorage array object for gor4 prediction
     * @param model_file gor4 trained model file input
     * @param windowsize size of prediction window
     * @return BaseStorage Array object
     */
    public BaseStorage[] gor4ModelParser(File model_file, int windowsize) {
        AAStateStorage aastate;
        PositionAaStorage storage = new PositionAaStorage(windowsize);
        try {
            FileReader fr = new FileReader(model_file);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            char state = ' ';
            char outerA = ' ';
            char innerA= ' ';
            int position = -1;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 && line.charAt(0) != '/' && !line.equals("\t")) {
                    //keys line
                    if (line.charAt(0) == '=') {
                        state = line.charAt(1);
                        outerA = line.charAt(5);
                        innerA = line.charAt(3);
                        if(position < windowsize-1) position++;
                        else position = 0;
                    }
                    //matrix line
                    else {
                        Matrix mat = storage.get(position).getOuterAminoacids().get(outerA).getAminoacids().get(innerA).get(state);
                        String[] training_values = line.trim().split("\t");
                        char aminoacid = line.charAt(0);
                        for (int i = 1; i < training_values.length; i++) {
                            int value = (Integer.parseInt(training_values[i]));
                            mat.setValue(value, aminoacid, i - 1);
                        }
                    }
                }
                else if(line.length() > 0 && line.charAt(0) == '/' && line.charAt(9) == '4'){
                    aastate = gor3ModelToAAState(model_file, windowsize);
                    return new BaseStorage[]{storage, aastate};
                }
            }
        } catch (IOException e) {
            System.out.println("Issue while reading the file");
            e.printStackTrace();
        }
        return null;
    }
}
