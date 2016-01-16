Ngram Extractor (dataset generation for PhrasIt)
================================================

You need:

* java 8

First test, simply run:
```
./ngram-extractor.sh -h
```
You will get the help screen after the project was successfully compiled.

Now you can extract n-grams with:
```
./ngram-extractor.sh FILES
```

e.g.
```
./ngram-extractor.sh in_data/*.pdf
```

The tool will print out all n-grams with its frequency in your collection to stdout.
Each line has the following format:
```
Ngram \t freq
```

Supported Formats
-----------------
As input formats are all text formats possible that tika supports, see [formats](https://tika.apache.org/1.11/formats.html).

* txt
* html
* pdf
* ...

Development Notes
-----------------
You can manually compile the project via gradle:
```
./gradlew build
./gradlew run -Pargs=FIlE1,FILE2
```

It is possible to build a jar with all dependencies via:
```
./gradlew shadowJar
```