import ReaderWriter.OutputWriter;
import gors.training.Gor1training;
import gors.training.Gor3training;
import gors.training.Gor4training;

import org.apache.commons.cli.*;
import seqlib.SeqlibEntry;
import seqlib.SeqlibParser;
import storages.AAStateStorage;
import storages.PositionAaStorage;
import storages.StateMatrixStorage;

import java.io.File;
import java.util.ArrayList;

public class TrainRunner {
    public static void main(String[] args) throws ParseException {
        char[] AAs = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
                'V', 'W', 'Y'};

        Options options = new Options();

        options.addOption(Option.builder("db").longOpt("db").hasArg().required().build());
        options.addOption(Option.builder("method").longOpt("method").hasArg().required().build());
        options.addOption(Option.builder("model").longOpt("model").hasArg().required().build());
        options.addOption(Option.builder("windowsize").longOpt("windowsize").hasArg().build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        SeqlibParser seqlibParser = new SeqlibParser();
        int WINDOWSIZE;
        if (cmd.hasOption("windowsize") && Integer.parseInt(cmd.getOptionValue("windowsize")) % 2 == 1){
            WINDOWSIZE = Integer.parseInt(cmd.getOptionValue("windowsize"));
        } else {
            WINDOWSIZE = 17;
        }

        ArrayList<SeqlibEntry> data = seqlibParser.parseSeqlib(new File(cmd.getOptionValue("db")));
        OutputWriter writer = new OutputWriter(AAs, WINDOWSIZE);

        if (cmd.getOptionValue("method").equals("gor1")){
            Gor1training gor1 = new Gor1training(WINDOWSIZE);
            StateMatrixStorage trainData = gor1.training(data);
            writer.writeModelGor1(trainData, cmd.getOptionValue("model"));
        }
        if (cmd.getOptionValue("method").equals("gor3")){
            Gor3training gor3 = new Gor3training(WINDOWSIZE);
            AAStateStorage trainData = gor3.training(data);
            writer.writeModelGor3(trainData, cmd.getOptionValue("model"), false);
        }
        if (cmd.getOptionValue("method").equals("gor4")){
            Gor4training gor4 = new Gor4training(WINDOWSIZE);
            PositionAaStorage trainData = gor4.training(data);
            writer.writeModelGor4(trainData, cmd.getOptionValue("model"));
            Gor3training gor3 = new Gor3training(WINDOWSIZE);
            AAStateStorage g3Data = gor3.training(data);
            writer.writeModelGor3(g3Data, cmd.getOptionValue("model"), true);
        }
    }
}
