import ReaderWriter.PredictionsWriter;
import ReaderWriter.modelParser;
import gors.predict.Gor1predict;
import gors.predict.Gor3predict;
import gors.predict.Gor4predict;
import gors.predict.Gor5.G5AliSet;
import gors.predict.Gor5.Gor5predict;
import org.apache.commons.cli.*;
import storages.AAStateStorage;
import storages.BaseStorage;
import storages.PositionAaStorage;
import storages.StateMatrixStorage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import ReaderWriter.fastaParser;
public class PredictRunner {
    public static void main(String[] args) throws ParseException, IOException {
        Options options = new Options();

        options.addOption(Option.builder("model").longOpt("model").hasArg().required().build());
        options.addOption(Option.builder("format").longOpt("format").hasArg().build());
        options.addOption(Option.builder("pseudocount").hasArg().longOpt("pseudocount").build());
        options.addOption(Option.builder("seq").longOpt("seq").hasArg().build());
        options.addOption(Option.builder("maf").longOpt("maf").hasArg().build());
        options.addOption(Option.builder("probabilities").longOpt("probabilities").build());
        options.addOption(Option.builder("post").longOpt("post").build());
        options.addOption(Option.builder("windowsize").longOpt("windowsize").build());


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        int WINDOWSIZE;
        int HALFWINDOWSIZE;
        double PSEUDO;
        boolean POST = cmd.hasOption("post");
        String pseudoStr = cmd.getOptionValue("pseudocount");
        if (pseudoStr != null){
            PSEUDO = Double.parseDouble(pseudoStr);
        } else {
            PSEUDO = Math.pow(10, -6);
        }
        String window = cmd.getOptionValue("windowsize");
        if (window != null && Integer.parseInt(cmd.getOptionValue("windowsize")) % 2 == 1){
            WINDOWSIZE = Integer.parseInt(cmd.getOptionValue("windowsize"));
        } else {
            WINDOWSIZE = 17;
        }
        HALFWINDOWSIZE = Math.floorDiv(WINDOWSIZE, 2);

        if(!cmd.hasOption("maf") && !cmd.hasOption("seq")){
            System.err.println("Please enter the maf or seq flag input");
            return;
        } else if(cmd.hasOption("maf") && cmd.hasOption("seq")){
            System.err.println("Please enter only maf option or seq option");
            return;
        }

        String format = cmd.getOptionValue("format");
        boolean probabilities = cmd.hasOption("probabilities");

        File model_file = new File(cmd.getOptionValue("model"));
        modelParser model_file_parser = new modelParser();
        String model = model_file_parser.getModel(model_file);

        if (cmd.hasOption("maf")){
            Gor5predict gor5;
            if (model.equals("gor1")){
                StateMatrixStorage trainset =  model_file_parser.gor1ModelToStateMatrix(model_file, WINDOWSIZE);
                gor5 = new Gor5predict(cmd.getOptionValue("maf"), "gor1", new BaseStorage[]{trainset},
                        WINDOWSIZE, PSEUDO);

            }
            else if (model.equals("gor3")){
                AAStateStorage trainset =  model_file_parser.gor3ModelToAAState(model_file, WINDOWSIZE);
                gor5 = new Gor5predict(cmd.getOptionValue("maf"), "gor3", new BaseStorage[]{trainset},
                        WINDOWSIZE, PSEUDO);
            } else {
                BaseStorage[] baseStorage =  model_file_parser.gor4ModelParser(model_file, WINDOWSIZE);
                gor5 = new Gor5predict(cmd.getOptionValue("maf"), "gor4", baseStorage, WINDOWSIZE, PSEUDO);
            }

            PredictionsWriter writer = new PredictionsWriter(HALFWINDOWSIZE);
            for(G5AliSet curSet : gor5.getData()){
                //writer.writeAliset(curSet, probabilities);
                writer.writeFileAliset(curSet, probabilities, model, POST);
            }

        } else {
            File fasta = new File(cmd.getOptionValue("seq"));
            fastaParser fastaParser = new fastaParser();
            HashMap<String, String> seqs = fastaParser.fastaToHashmap(fasta);
            String seq_file = cmd.getOptionValue("seq");

            if (model.equals("gor1")){
                StateMatrixStorage trainset =  model_file_parser.gor1ModelToStateMatrix(model_file, WINDOWSIZE);
                Gor1predict gor1 = new Gor1predict(trainset, WINDOWSIZE, PSEUDO);
                HashMap<String, String[]> predictionResult = gor1.prediction(seqs);
                PredictionsWriter predictionsWriter = new PredictionsWriter(predictionResult, HALFWINDOWSIZE, POST);
                if(cmd.hasOption("format") && format.equals("txt")){
                    predictionsWriter.writeToStdout(probabilities);
                }
                else if(cmd.hasOption("format") && format.equals("html")){
                    predictionsWriter.writeToHtml(model,probabilities, seq_file);
                } else {
                    predictionsWriter.writeToFile(model, probabilities, POST);
                }
            } else if (model.equals("gor3")){
                AAStateStorage trainset =  model_file_parser.gor3ModelToAAState(model_file, WINDOWSIZE);
                Gor3predict gor3 = new Gor3predict(trainset, WINDOWSIZE, PSEUDO);
                HashMap<String, String[]> predictionResult = gor3.prediction(seqs);
                PredictionsWriter predictionsWriter = new PredictionsWriter(predictionResult, HALFWINDOWSIZE, POST);
                if(cmd.hasOption("format") && format.equals("txt")){
                    predictionsWriter.writeToStdout(probabilities);
                }
                else if(cmd.hasOption("format") && format.equals("html")){
                    predictionsWriter.writeToHtml(model,probabilities, seq_file);
                } else {
                    predictionsWriter.writeToFile(model, probabilities, POST);
                }
            } else if (model.equals("gor4")){
                BaseStorage[] basetrain =  model_file_parser.gor4ModelParser(model_file, WINDOWSIZE);
                AAStateStorage gor3train = (AAStateStorage) basetrain[1];
                PositionAaStorage gor4train = (PositionAaStorage) basetrain[0];
                Gor4predict gor4 = new Gor4predict(gor4train, gor3train, WINDOWSIZE, PSEUDO);
                HashMap<String, String[]> predictionResult = gor4.prediction(seqs);
                PredictionsWriter predictionsWriter = new PredictionsWriter(predictionResult, HALFWINDOWSIZE, POST);
                if(cmd.hasOption("format") && format.equals("txt")){
                    predictionsWriter.writeToStdout(probabilities);
                }
                else if(cmd.hasOption("format") && format.equals("html")){
                    predictionsWriter.writeToHtml(model,probabilities, seq_file);
                } else {
                    predictionsWriter.writeToFile(model, probabilities, POST);
                }
            }
        }
    }
}
