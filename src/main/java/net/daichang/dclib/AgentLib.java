package net.daichang.dclib;

import com.sun.tools.attach.VirtualMachine;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.locating.IModFile;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public interface AgentLib extends BaseLib {
    default void attachAgent(String mod_id) {
        ModContainer mods = modContainer(mod_id).get();
        IModFile modFile = mods.getModInfo().getOwningFile().getFile();
        String modFileName = modFile.getFileName();
        try {
            setStaticFinalField(Class.forName("sun.tools.attach.HotSpotVirtualMachine"), "ALLOW_ATTACH_SELF", true);
            String pid = String.valueOf(ProcessHandle.current().pid());
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent("mods/" + modFileName);
            vm.detach();
        } catch (Exception ignored) {}
    }

    default void removeAgent(Instrumentation ins) {
        for (ClassFileTransformer classFileTransformer : TransformerHelper.getAllTransformers(ins)) {
            ins.removeTransformer(classFileTransformer);
        }
    }
}
