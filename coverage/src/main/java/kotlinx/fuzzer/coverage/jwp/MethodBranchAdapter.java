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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * {@link MethodVisitor} that manipulates method bytecode before calling the delegating visitor. This inserts static
 * calls to reference in the given {@link MethodRef} on each branch.
 * The bytecodes that the static calls are inserted before are: IFEQ, IFNE, IFLT, IFGE, IFGT,
 * IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, IFNULL, IFNONNULL,
 * TABLESWITCH, and LOOKUPSWITCH. Also, a static call is made at the start of each catch handler as that is considered
 * a branch as well.
 */
class MethodBranchAdapter extends MethodNode {

    private final MethodRef ref;
    private final String className;
    private final MethodVisitor mv;
    private boolean alreadyTransformed;
    private int currentInstruction;

    /**
     * Create this adapter with a {@link MethodRef}, the internal class name for the method, values given from
     * {@link org.objectweb.asm.ClassVisitor#visitMethod(int, String, String, String, String[])}, and a
     * {@link MethodVisitor} to delegate to.
     */
    public MethodBranchAdapter(MethodRef ref, String className, int access, String name,
                               String desc, String signature, String[] exceptions, MethodVisitor mv) {
        super(Opcodes.ASM6, access, name, desc, signature, exceptions);
        this.ref = ref;
        this.className = className;
        this.mv = mv;
    }

    private int insnHashCode(int index) {
        return Arrays.hashCode(new int[]{className.hashCode(), name.hashCode(), desc.hashCode(), index});
    }

    private void insertBeforeAndInvokeStaticWithHash(AbstractInsnNode insn, MethodRef ref) {
        InsnList insns = new InsnList();
        // Add branch hash and make static call
        currentInstruction += 2;
        insns.add(new LdcInsnNode(insnHashCode(currentInstruction)));
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ref.classSig, ref.methodName, ref.methodSig, false));
        instructions.insertBefore(insn, insns);
    }

    private void insertAfterAndInvokeStaticWithHash(AbstractInsnNode insn, MethodRef ref) {
        InsnList insns = new InsnList();
        // Add branch hash and make static call
        insns.add(new LdcInsnNode(insnHashCode(currentInstruction)));
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ref.classSig, ref.methodName, ref.methodSig, false));
        instructions.insert(insn, insns);
        currentInstruction += 2;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        // We have to mark this method as already transformed if there is a call to refs sig
        if (ref.classSig.equals(owner)) alreadyTransformed = true;
    }

    @Override
    public void visitEnd() {
        if (alreadyTransformed) {
            System.err.println("Skipping already transformed method " + className + ":" + name);
            accept(mv);
            return;
        }
        // We need the handler labels for catch clauses
        Set<Label> catchHandlerLabels = new HashSet<>(tryCatchBlocks.size());
        for (TryCatchBlockNode catchBlock : tryCatchBlocks) catchHandlerLabels.add(catchBlock.handler.getLabel());
        // Go over each instruction, injecting static calls where necessary
        ListIterator<AbstractInsnNode> iter = instructions.iterator();
        currentInstruction = 0;
        while (iter.hasNext()) {
            AbstractInsnNode insn = iter.next();
            currentInstruction++;
            int op = insn.getOpcode();
            switch (op) {
                case Opcodes.IFEQ:
                case Opcodes.IFNE:
                case Opcodes.IFLT:
                case Opcodes.IFGE:
                case Opcodes.IFGT:
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPEQ:
                case Opcodes.IF_ICMPNE:
                case Opcodes.IF_ICMPLT:
                case Opcodes.IF_ICMPGE:
                case Opcodes.IF_ICMPGT:
                case Opcodes.IF_ICMPLE:
                case Opcodes.IFNULL:
                case Opcodes.IFNONNULL:
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IF_ACMPNE:
                    insertBeforeAndInvokeStaticWithHash(insn, ref);
                    insertAfterAndInvokeStaticWithHash(insn, ref);
                    break;
            }
        }
        accept(mv);
    }

    /** A reference to a static method call */
    public static class MethodRef {
        /** The internal JVM name/signature of the class containing the method */
        public final String classSig;
        /** The name of the method */
        public final String methodName;
        /** The JVM signature of the method */
        public final String methodSig;

        /** Simple constructor that just sets the fields */
        public MethodRef(String classSig, String methodName, String methodSig) {
            this.classSig = classSig;
            this.methodName = methodName;
            this.methodSig = methodSig;
        }

        /** Create the ref from the given reflected method */
        public MethodRef(Method method) {
            this(Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));
        }
    }
}
