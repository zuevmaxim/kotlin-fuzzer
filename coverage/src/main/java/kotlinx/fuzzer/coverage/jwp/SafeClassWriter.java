/*
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package kotlinx.fuzzer.coverage.jwp;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A ClassWriter that computes the common super class of two classes without
 * actually loading them with a ClassLoader.
 * @author Eric Bruneton
 */
class SafeClassWriter extends ClassWriter {

    private static final String OBJECT = "java/lang/Object";
    private final ClassLoader loader;

    public SafeClassWriter(ClassReader cr, ClassLoader loader, final int flags) {
        super(cr, flags);
        this.loader = loader != null ? loader : ClassLoader.getSystemClassLoader();
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        try {
            ClassReader info1 = typeInfo(type1);
            ClassReader info2 = typeInfo(type2);
            if (isInterface(info1)) {
                return getCommonSuperClassWithInterface(type1, type2, info2);
            }
            if (isInterface(info2)) {
                return getCommonSuperClassWithInterface(type2, type1, info1);
            }
            List<String> ancestors1 = typeAncestors(type1, info1);
            List<String> ancestors2 = typeAncestors(type2, info2);
            String result = OBJECT;
            for (int i = 0; i < Math.min(ancestors1.size(), ancestors2.size()); i++) {
                if (ancestors1.get(i).equals(ancestors2.get(i))) {
                    result = ancestors1.get(i);
                } else {
                    return result;
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /** Find common super class when second type is an interface. */
    private String getCommonSuperClassWithInterface(String interfaceType, String type, ClassReader typeInfo) throws IOException {
        if (typeImplements(type, typeInfo, interfaceType)) {
            return interfaceType;
        } else {
            return OBJECT;
        }
    }

    private boolean isInterface(@NotNull ClassReader info) {
        return (info.getAccess() & Opcodes.ACC_INTERFACE) != 0;
    }

    /**
     * Returns the internal names of the ancestor classes of the given type.
     * @param type the internal name of a class or interface.
     * @param info the ClassReader corresponding to 'type'.
     * @return a List containing the ancestor classes of 'type'.
     * The returned list contains types:
     * type1,type2 ... ,typeN, where typeN is 'type', and type1 is a
     * direct subclass of Object. If 'type' is Object, the returned
     * list is empty.
     * @throws IOException if the bytecode of 'type' or of some of its ancestor class
     *                     cannot be loaded.
     */
    @NotNull
    private List<String> typeAncestors(@NotNull String type, ClassReader info)
            throws IOException {
        List<String> result = new ArrayList<>();
        while (!type.equals(OBJECT)) {
            result.add(type);
            type = info.getSuperName();
            info = typeInfo(type);
        }
        Collections.reverse(result);
        return result;
    }

    /**
     * Returns true if the given type implements the given interface.
     * @param type        the internal name of a class or interface.
     * @param info        the ClassReader corresponding to 'type'.
     * @param anInterface the internal name of a interface.
     * @return true if 'type' implements directly or indirectly 'anInterface'
     * @throws IOException if the bytecode of 'type' or of some of its ancestor class
     *                     cannot be loaded.
     */
    private boolean typeImplements(@NotNull String type, @NotNull ClassReader info, @NotNull String anInterface)
            throws IOException {
        while (!type.equals(OBJECT)) {
            String[] interfaces = info.getInterfaces();
            for (String s : interfaces) {
                if (s.equals(anInterface)) {
                    return true;
                }
            }
            for (String s : interfaces) {
                if (typeImplements(s, typeInfo(s), anInterface)) {
                    return true;
                }
            }
            type = info.getSuperName();
            info = typeInfo(type);
        }
        return false;
    }

    /**
     * Returns a ClassReader corresponding to the given class or interface.
     * @param type the internal name of a class or interface.
     * @return the ClassReader corresponding to 'type'.
     * @throws IOException if the bytecode of 'type' cannot be loaded.
     */
    @NotNull
    private ClassReader typeInfo(final String type) throws IOException {
        String resource = type + ".class";
        try (InputStream is = loader.getResourceAsStream(resource)) {
            if (is == null) {
                throw new IOException("Cannot create ClassReader for type " + type);
            }
            return new ClassReader(is);
        }
    }
}
