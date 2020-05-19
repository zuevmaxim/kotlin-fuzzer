# Kotlin fuzzer
Randomized JVM testing application based on code coverage.
Implementation is based on [go-fuzz](https://github.com/dvyukov/go-fuzz) library.
The result of fuzzing is crashes report and corpus of inputs - succeed tests with the best found coverage.

## Before fuzzing
1. Create a class with method signature: `fun test(bytes: ByteArray): Int`
    * *bytes* is an input for your algorithm (either from the predefined corpus or generated one)
    * Return value represents status of input: 
      * \>= 1 - input might be added into corpus
      * 0 - input is OK, but should not be added into corpus
      * <= -1 - input is invalid(can not run test with *bytes* input)
      
      Example:
      ```
      fun fuzz(bytes: ByteArray): Int {
          if (bytes.size < 4) { // uninteresting input
              return 0
          }
          val string = String(bytes)
          failOnABCD(string) // may crash
          return 1 // passed OK
      }
      ```
    * If this method throws an exception this means a crash.
      
2. Create "init" directory and add initial corpus into it

## Command line fuzzing
1. Build fuzzer jar: `gradle fatJar`
2. Run fuzzer: `java -jar build/libs/kotlin-fuzzer-all.jar [ARGUMENTS(see 3)]`
3. Arguments:
    * --className -> Target class name (always required) { String }
    * --methodName -> Target method name (always required) { String }
    * --workingDirectory -> Working directory for corpus and crashes (always required) { String }
    * --classpath -> Target ClassPath (delimited with colon) (always required) { String }
    * --packages -> Target packages (delimited with colon) (always required) { String }
    * --maxTaskQueueSize -> Maximum number of tasks in working queue. Use it to control memory usage. { Int }
    * --threadsNumber -> Number of threads for workers. { Int }
    * --help, -h -> Usage info 
    
## Output
Fuzzer outputs results into *workingDirectory* subdirectories:
  * corpus - generated corpus inputs witch passed test
  * executed - hashes of executed inputs
  * crashes - failed inputs are logged in form:
    1. raw input
    2. stack trace
    3. byte array representation

log.txt contains the timeline of crashes, each line represents time passed, crash file name, exception type, exception message

