import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.code.externalsorting.*;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

class NgramExtractor {

    // global constants
    private final static int max_n = 5;
    private final static String temporary_file = "./tmp";
    private final static String sorted_file = "./sorted";


    public static void main(String[] args) throws IOException {
        // argument parsing
        ArgumentParser parser = ArgumentParsers.newArgumentParser("ngram extractor")
            .defaultHelp(true)
            .description("Calculate ngram frequencies based on all input files.");

        parser.addArgument("file")
            .nargs("+")
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

        BufferedWriter writer;
        try {
            writer = Files.newBufferedWriter(Paths.get(temporary_file));
        } catch (IOException e) {
            Log.error("tmp file cannot be opened " + temporary_file);
            return;
        }

        // iterate over all input files and store ngrams with frequencies in temp file
        for (String name : ns.<String> getList("file")) {

            Path path = Paths.get(name);
            String text = extractor.getText(path.toAbsolutePath().toString());

            String[] tokens = text.split("\\s+");
            HashMap<String, Integer> freqs = new HashMap<String, Integer>();

            for (int i = 0; i < tokens.length; i++) {
                for (int n = 0; n < max_n; n++) {
                    if (i + n < tokens.length) {
                        String t = String.join(" ", Arrays.asList(tokens).subList(i, i + n + 1));
                        freqs.put(t, freqs.getOrDefault(t, 0) + 1);
                    }
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
        ExternalSort.sort(new File(temporary_file), new File(sorted_file));

        // calculate global frequencies for each ngram,
        // read all lines, group by key and apply group aggregation

        Stream<String> lines = Files.lines(Paths.get(sorted_file));

        lines.parallel().map(l -> {
                return l.split("\t");
            }).collect(
                Collectors.groupingBy(
                    p -> p[0],
                    Collectors.summingInt(p -> Integer.valueOf(p[1]))
                )
        ).forEach((k,v) -> {System.out.println(k + "\t" + v);});

        // tidy up generated files
        Files.delete(Paths.get(temporary_file));
        Files.delete(Paths.get(sorted_file));
    }
}