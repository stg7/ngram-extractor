#!/bin/bash
if [ ! -f "./build/libs/ngram-extractor-all.jar" ]; then
    ./gradlew shadowJar
fi

java -jar "./build/libs/ngram-extractor-all.jar" "$@"

