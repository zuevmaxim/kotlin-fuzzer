#! /bin/bash

if [ $# -ne 3 ]; then
  echo "usage: test CLASS_NAME METHOD_NAME PACKAGE"
  exit 1
fi

cd .. && ./gradlew jar && cd test-project
./gradlew jar
className="ru.example.kotlinfuzzer.tests.$1"
methodName=$2
testPackage=$3
mkdir -p results
dirName="results/$className.$methodName"
rm -rf "$dirName"
mkdir "$dirName"
java -jar ../build/libs/kotlin-fuzzer-all.jar \
  --classpath build/libs/test-project-1.0-SNAPSHOT-all.jar \
  --packages ru.example.kotlinfuzzer.tests:"$testPackage" \
  --className "$className" \
  --methodName "$methodName" \
  --workingDirectory "$dirName"
