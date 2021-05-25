package fuzz.example

fun failOnABCD(string: String) {
    if (string[0] == 'a') {
        if (string[1] == 'b') {
            if (string[2] == 'c') {
                if (string[3] == 'd') {
                    error("Crash")
                }
            }
        }
    }
}
