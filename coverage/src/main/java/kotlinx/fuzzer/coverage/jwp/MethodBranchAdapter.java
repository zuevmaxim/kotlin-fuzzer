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

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link MethodVisitor} that manipulates method bytecode before calling the delegating visitor. This inserts static
 * calls to reference in the given {@link MethodReference} on each branch.
 * The bytecodes that the static calls are inserted before are: IFEQ, IFNE, IFLT, IFGE, IFGT,
 * IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, IFNULL, IFNONNULL,
 * TABLESWITCH, and LOOKUPSWITCH. Also, a static call is made at the start of each catch handler as that is considered
 * a branch as well.
 */
class MethodBranchAdapter extends MethodNode {
    /** Index for next branch. */
    private static final AtomicInteger currentInstructionIndex = new AtomicInteger(0);

    /** Reference to a static method. */
    private final MethodReference ref;

    private final String className;

    /** Labels in codes that has been already marked with brach hash. It is used to mark every else label only once. */
    private final HashMap<LabelNode, LabelNode> markedLabels = new HashMap<>();

    private final List<Label> catchLabels = new ArrayList<>();

    public MethodBranchAdapter(MethodReference ref, String className, int access, String name,
                               String desc, String signature, String[] exceptions, MethodVisitor mv) {
        super(Opcodes.ASM7, access, name, desc, signature, exceptions);
        this.ref = ref;
        this.className = className;
        this.mv = mv;
    }

    /** Generate instructions for a static call at next branch. */
    @NotNull
    private InsnList invokeStaticWithHash() {
        InsnList insns = new InsnList();
        int index = currentInstructionIndex.incrementAndGet();
        BranchTracker.indexToClass.put(index, className + " " + name);
        insns.add(new LdcInsnNode(index));
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ref.classSig, ref.methodName, ref.methodSig, false));
        return insns;
    }

    /** Instrument whole instructions list. Insert static calls at branch points. */
    @Override
    public void visitEnd() {
        for (AbstractInsnNode instruction : instructions) {
            int opcode = instruction.getOpcode();
            switch (opcode) {
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
                    visitJumpInstruction((JumpInsnNode) instruction);
                    break;
                case Opcodes.TABLESWITCH:
                    visitTableSwitchInstruction((TableSwitchInsnNode) instruction);
                    break;
                case Opcodes.LOOKUPSWITCH:
                    visitLookupSwitchInstruction((LookupSwitchInsnNode) instruction);
                    break;
            }
        }
        for (Label handler : catchLabels) {
            instructions.insert(getLabelNode(handler), invokeStaticWithHash());
        }
        accept(mv);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        catchLabels.add(handler);
    }

    /**
     * Insert a static call before originalLabel.
     * @return new label before original one
     */
    @NotNull
    private LabelNode insertBeforeLabel(@NotNull LabelNode originalLabelNode) {
        LabelNode labelNode = markedLabels.get(originalLabelNode);
        if (labelNode != null) return labelNode;

        LabelNode newLabelNode = new LabelNode(new Label());
        markedLabels.put(originalLabelNode, newLabelNode);

        InsnList extraInstructions = new InsnList();
        extraInstructions.add(newLabelNode);
        extraInstructions.add(invokeStaticWithHash());
        instructions.insertBefore(originalLabelNode, extraInstructions);

        return newLabelNode;
    }

    private void visitJumpInstruction(@NotNull JumpInsnNode node) {
        instructions.insert(node, invokeStaticWithHash());
        node.label = insertBeforeLabel(node.label);
    }

    private void visitTableSwitchInstruction(@NotNull TableSwitchInsnNode node) {
        List<LabelNode> newLabels = new ArrayList<>();
        for (LabelNode label : node.labels) {
            newLabels.add(insertBeforeLabel(label));
        }
        node.labels = newLabels;
        node.dflt = insertBeforeLabel(node.dflt);
    }

    private void visitLookupSwitchInstruction(@NotNull LookupSwitchInsnNode node) {
        List<LabelNode> newLabels = new ArrayList<>();
        for (LabelNode label : node.labels) {
            newLabels.add(insertBeforeLabel(label));
        }
        node.labels = newLabels;
        node.dflt = insertBeforeLabel(node.dflt);
    }

}
