import java.io.File;
import java.io.IOException;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

class TextExtractor {
    TikaConfig tika;

    public TextExtractor() {
        try {
            tika = new TikaConfig();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String parseUsingAutoDetect(String filename, TikaConfig tikaConfig,
                                              Metadata metadata) throws Exception {
        AutoDetectParser parser = new AutoDetectParser(tikaConfig);
        ContentHandler handler = new BodyContentHandler(-1);

        TikaInputStream stream =  TikaInputStream.get(new File(filename), metadata);

        parser.parse(stream, handler, metadata, new ParseContext());
        return handler.toString();
    }

    public String getText(String filename) {

        Metadata metadata = new Metadata();
        String text = null;
        try {
            text = parseUsingAutoDetect(filename.toString(), tika, metadata);
        } catch (Exception e) {
            Log.error("invalide file " + filename);
            return "";
        }
        return text;
    }

}