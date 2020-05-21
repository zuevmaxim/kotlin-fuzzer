package ru.example.kotlinfuzzer.tests

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File

private class EmptyVisitor : ClassVisitor(Opcodes.ASM7)

class AsmTest {
    fun test(bytes: ByteArray): Int {
        val reader = ClassReader(bytes)
        val visitor = EmptyVisitor()
        reader.accept(visitor, 0)
        return 1
    }
}
