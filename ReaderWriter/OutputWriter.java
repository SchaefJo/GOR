package ReaderWriter;

import storages.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import enums.*;

public class OutputWriter {
    private char[] innerAAs;
    private char[] outerAAs;
    private int windowsize;
    private char[] states = {'C', 'E', 'H'};

    public OutputWriter(char[] aas, int windowsize) {
        this.innerAAs = aas;
        this.outerAAs = aas;
        this.windowsize = windowsize;
    }

    /**
     * writes data into gor1 model file
     * @param data StateMatrixStorage containing all the relevant data for writing a gor1 model
     * @param path path to outputfile
     */
    public void writeModelGor1(StateMatrixStorage data, String path){

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), StandardCharsets.UTF_8))) {
            writer.write("// Matrix3D\n\n");
            for (States state : States.values()) {
                writer.write("="+state+"=\n\n");
                StringBuilder result = new StringBuilder();
                for (char aa: innerAAs){
                    result.append(aa).append("\t");
                    Matrix m;
                    if(state.name().equals("H")) m = data.getH();
                    else if(state.name().equals("C")) m = data.getC();
                    else m = data.getE();
                    for (int i=0; i<windowsize; i++){
                        result.append((int) m.getValue(aa, i) + "\t");
                    }
                    result.append("\n");
                }
                result.append("\n\n");
                writer.write(result.toString());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes data into gor3 model file
     * @param data AAStateStorage containing all the relevant data for writing a gor3 model
     * @param path path to outputfile
     * @param append whether to append gor3 output to file or not
     */
    public void writeModelGor3(AAStateStorage data, String path, boolean append){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path, append), StandardCharsets.UTF_8))) {
            writer.write("// Matrix4D\n\n");
            for (Map.Entry<Character, StateMatrixStorage> entry : data.getAminoacids().entrySet()) {
                char outeraa = entry.getKey();
                StateMatrixStorage curStateMatrix = entry.getValue();

                for (States state : States.values()) {
                    writer.write("=" +outeraa + ","+ state + "=\n\n");
                    StringBuilder result = new StringBuilder();
                    for (char aa : innerAAs) {
                        result.append(aa).append("\t");
                        Matrix m;
                        if (state.name().equals("H")) m = curStateMatrix.getH();
                        else if (state.name().equals("C")) m = curStateMatrix.getC();
                        else {
                            m = curStateMatrix.getE();
                        }
                        for (int i = 0; i < windowsize; i++) {
                            result.append((int) m.getValue(aa, i) + "\t");
                        }
                        result.append(" ");
                        result.append("\n");
                    }
                    result.append("\n\n");
                    writer.write(result.toString());
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes data into gor4 model file
     * @param data PositionAaStorage containing all the relevant data for writing a gor4 model
     * @param path path to outputfile
     */
    public void writeModelGor4(PositionAaStorage data, String path){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), StandardCharsets.UTF_8))) {
            writer.write("// Matrix6D\n\n");
            for (char state: states){
                for(char innera : innerAAs){
                    for(char outera : outerAAs){
                        for (int i=0; i< data.getPositions().length; i++){
                            AaAAStorage store = data.get(i);
                            writer.write("=" + state + ","+ innera + "," + outera + ","+ (i - Math.floorDiv(windowsize, 2))+ "=\n\n");
                            writer.write(store.getOuterAminoacids().get(outera).getAA(innera).get(state).retPrintMatrix(i));
                            writer.write("\n");
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}