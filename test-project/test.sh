#! /bin/bash

if [ $# -ne 2 ]; then
  echo "usage: test CLASS_NAME METHOD_NAME"
  exit 1
fi

cd .. && ./gradlew jar && cd test-project
./gradlew jar
className="ru.example.kotlinfuzzer.tests.$1"
methodName=$2
mkdir -p results
dirName="results/$className.$methodName"
rm -rf "$dirName"
mkdir "$dirName"
java -jar ../build/libs/kotlin-fuzzer-all.jar \
  --classpath build/libs/test-project-1.0-SNAPSHOT-all.jar \
  --packages ru.example.kotlinfuzzer.tests \
  --className "$className" \
  --methodName "$methodName" \
  --workingDirectory "$dirName"
