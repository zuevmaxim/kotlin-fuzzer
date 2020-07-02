# kotlin-fuzzer

## Quick start
1. Add fuzzer into dependencies:
    ```
    repositories {
        maven("https://dl.bintray.com/zuevmaxim/com.github.zuevmaxim")
        maven("https://kotlin.bintray.com/kotlinx")
    }
   
    dependencies {
       implementation("com.github.zuevmaxim:kotlin-fuzzer:0.0.3")
    }
    ``` 
   For more information see [repository](https://bintray.com/zuevmaxim/com.github.zuevmaxim/kotlin-fuzzer/0.0.3)
2. Run fuzzer:
    ```
    class FuzzTest {
    
        @Fuzz(workingDirectory = "results")
        fun fuzz(bytes: ByteArray): Int {
            ...
        }
    
        @Test
        fun test() {
            Fuzzer(FuzzTest::class.java).start() // pass class with @Fuzz method
        }
    }
    ```
Complete project example see in 'examples' directory.
