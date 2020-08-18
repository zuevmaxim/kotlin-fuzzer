/*
 * MIT License
 *
 * Copyright (c) 2018 Chad Retz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package kotlinx.fuzzer.coverage.jwp;

import org.objectweb.asm.*;

/** The {@link ClassVisitor} that uses {@link MethodBranchAdapter} to insert branch calls in methods */
class ClassBranchAdapter extends ClassVisitor {

    private final MethodBranchAdapter.MethodRef ref;
    private String className;

    /** Create this adapter with the given {@link MethodBranchAdapter.MethodRef} to call */
    public ClassBranchAdapter(MethodBranchAdapter.MethodRef ref, ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
        this.ref = ref;
    }

    /** Create new classfile bytecode set from given original classfile bytecode using this adapter */
    public static byte[] transform(byte[] origBytes) {
        ClassReader reader = new ClassReader(origBytes);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        reader.accept(new ClassBranchAdapter(BranchTracker.ref, writer), 0);
        return writer.toByteArray();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodBranchAdapter(ref, className, access, name, desc, signature, exceptions, mv);
    }
}
