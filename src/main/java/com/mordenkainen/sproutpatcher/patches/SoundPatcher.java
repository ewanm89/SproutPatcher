package com.mordenkainen.sproutpatcher.patches;

import java.util.ListIterator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mordenkainen.sproutpatcher.SproutConfig;
import com.mordenkainen.sproutpatcher.asmhelper.ASMHelper;

public class SoundPatcher implements IPatch {

    @Override
    public boolean shouldLoad() {
        return SproutConfig.SoundPatch;
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("paulscode.sound.Source".equals(name)) {
            final ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);
            classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "removed", "Z", null, null));
            return ASMHelper.writeClassToBytes(classNode, ClassWriter.COMPUTE_FRAMES);
        }
            
        if ("paulscode.sound.Library".equals(name)) {
            final ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);
            
            MethodNode method = ASMHelper.findMethodNodeOfClass(classNode, "removeSource", "(Ljava/lang/String;)V");
            for (ListIterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext();) {
                AbstractInsnNode insn = (AbstractInsnNode) iterator.next(); 
                if(insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("paulscode/sound/Source") && ((MethodInsnNode) insn).name.equals("cleanup"))
                {
                    method.instructions.insertBefore(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mordenkainen/sproutpatcher/patches/SoundFix", "cleanupSource", "(Lpaulscode/sound/Source;)V", false));
                    method.instructions.remove(insn);
                    break;
                }
            }
            return ASMHelper.writeClassToBytes(classNode, ClassWriter.COMPUTE_FRAMES);
        }
        
        if ("paulscode.sound.StreamThread".equals(name)) {
            final ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);
            MethodNode method = ASMHelper.findMethodNodeOfClass(classNode, "run", "()V");
            for (ListIterator<AbstractInsnNode> iterator = method.instructions.iterator(); iterator.hasNext();) {
                AbstractInsnNode insn = (AbstractInsnNode) iterator.next();
                if(insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("java/util/ListIterator") && ((MethodInsnNode) insn).name.equals("next"))
                {
                    insn = insn.getNext().getNext();
                    
                    int varIndex = ((VarInsnNode) insn).var;
                    
                    method.instructions.insert(insn, new VarInsnNode(Opcodes.ASTORE, varIndex));
                    method.instructions.insert(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mordenkainen/sproutpatcher/patches/SoundFix", "removeSource", "(Lpaulscode/sound/Source;)Lpaulscode/sound/Source;", false));
                    method.instructions.insert(insn, new VarInsnNode(Opcodes.ALOAD, varIndex));
                    break;
                }
            }
            
            
            return ASMHelper.writeClassToBytes(classNode, ClassWriter.COMPUTE_FRAMES);
        }
        
        return basicClass;
    }    
}
