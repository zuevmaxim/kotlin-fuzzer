#! /bin/bash

if [ $# -ne 3 ]; then
  echo "usage: test CLASS_NAME METHOD_NAME PACKAGES"
  exit 1
fi


className=$1
methodName=$2
testPackages=$3
dirName="results/$className.$methodName"
fuzzerJar="../build/libs/kotlin-fuzzer-0.0.3-SNAPSHOT-all.jar"
mkdir -p "$dirName"
cd .. && ./gradlew fatJar && cd test-project || exit 1
./gradlew jar || exit 1
java \
  -Xbootclasspath/a:$fuzzerJar \
  -jar $fuzzerJar \
  --classpath build/libs/test-project-1.0-SNAPSHOT-all.jar \
  --packages "$testPackages" \
  --className "$className" \
  --methodName "$methodName" \
  --workingDirectory "$dirName"
