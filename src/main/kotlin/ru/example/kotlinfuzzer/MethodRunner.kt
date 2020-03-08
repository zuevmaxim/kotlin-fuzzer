package ru.example.kotlinfuzzer

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IClassCoverage
import org.jacoco.core.analysis.ICounter
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData
import java.io.PrintStream

class MethodRunner(
    path: String,
    private val className: String
) {

    private val classBytes = MyClassLoader.loadClassBytes(path, className)

    fun run(methodName: String): IClassCoverage {
        val runtime = LoggerRuntime()
        val data = RuntimeData()
        runtime.startup(data)

        val targetClass = CodeCoverageClassTransformer.transform(className, classBytes, runtime)

        val targetInstance = InstanceCreator.create(targetClass)
        targetClass.declaredMethods.filter { it.name == methodName }.forEach {
            val parameters = InstanceCreator.createParameters(it.parameters)
            it.invoke(targetInstance, *parameters)
        }

        val executionData = ExecutionDataStore()
        val sessionInfos = SessionInfoStore()
        data.collect(executionData, sessionInfos, false)
        runtime.shutdown()

        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(executionData, coverageBuilder)
        analyzer.analyzeClass(classBytes, targetClass.name)

        return coverageBuilder.classes.first()
    }
}


fun printResult(out: PrintStream, coverage: IClassCoverage) {
    fun printCounter(unit: String, counter: ICounter) {
        val missed = Integer.valueOf(counter.missedCount)
        val total = Integer.valueOf(counter.totalCount)
        out.println("$missed of $total $unit missed")
    }

    out.printf("Coverage of class %s%n", coverage.name)

    printCounter("instructions", coverage.instructionCounter)
    printCounter("branches", coverage.branchCounter)
    printCounter("lines", coverage.lineCounter)
    printCounter("methods", coverage.methodCounter)
    printCounter("complexity", coverage.complexityCounter)

}
