#! /bin/bash

if [ $# -ne 2 ]; then
  echo "usage: tesh CLASS_NAME METHOD_NAME"
  exit 1
fi

../gradlew jar
./gradlew jar
className="ru.example.kotlinfuzzer.tests.$1"
methodName=$2
mkdir -p results
dirName="results/$className.$methodName"
rm -rf "$dirName"
mkdir "$dirName"
java -jar ../build/libs/kotlin-fuzzer-all.jar \
  --classpath build/libs/test-project-1.0-SNAPSHOT.jar \
  --packages ru.example.kotlinfuzzer.tests \
  --className "$className" \
  --methodName "$methodName" \
  --workingDirectory "$dirName"
