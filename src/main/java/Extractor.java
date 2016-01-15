import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import com.google.code.externalsorting.*;


import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

class Extractor {
    private final static int max_n = 5;

    public static void main(String[] args) throws IOException {
        Log.info("ngram extractor");

        // argument parsing
        ArgumentParser parser = ArgumentParsers.newArgumentParser("ngram extractor")
            .defaultHelp(true)
            .description("Calculate ngram frequencies based on all input files.");

        parser.addArgument("file")
            .nargs("*")
            .help("File for analysing");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        // get all ngrams and store frequencies for each input file in a temporary file
        TextExtractor extractor = new TextExtractor();
        String temporary_file = "./tmp";

        BufferedWriter writer;
        try {
            writer = Files.newBufferedWriter(Paths.get(temporary_file));
        } catch (IOException e) {
            Log.error("tmp file cannot be opened " + temporary_file);
            return;
        }

        for (String name : ns.<String> getList("file")) {
            Path path = Paths.get(name);

            String text = extractor.getText(path.toAbsolutePath().toString());

            String[] tokens = text.split("\\s+");
            HashMap<String, Integer> freqs = new HashMap<String, Integer>();

            for (int n = 1; n <= max_n; n++) {
                System.out.println(n);
                for (int i = 0; i < tokens.length - n; i+= n) {
                    String t = String.join(" ", Arrays.asList(tokens).subList(i, i + n));
                    freqs.put(t, freqs.getOrDefault(t, 0) + 1);
                }
            }

            for(String k : freqs.keySet()) {
                if (k.length() > 0) {
                    String out = k + "\t" + freqs.get(k) + "\n";
                    writer.write(out);
                }
            }
        }

        writer.close();

        // sort temporary file
        ExternalSort.sort(new File(temporary_file), new File("./sorted"));


        // calculate frequencies for each ngram


        Log.info("done");


    }
}