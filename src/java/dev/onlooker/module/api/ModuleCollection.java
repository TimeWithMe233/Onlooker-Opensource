package dev.onlooker.module.api;

import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.ArrayListMod;
import dev.onlooker.module.impl.display.NotificationsMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleCollection {

    public static boolean reloadModules;

    private HashMap<Object, Module> modules = new HashMap<>();
    private final List<Class<? extends Module>> hiddenModules = new ArrayList<>(Arrays.asList(ArrayListMod.class, NotificationsMod.class));

    public List<Class<? extends Module>> getHiddenModules() {
        return hiddenModules;
    }

    public List<Module> getModules() {
        return new ArrayList<>(this.modules.values());
    }

    public HashMap<Object, Module> getModuleMap() {
        return modules;
    }

    public void setModules(HashMap<Object, Module> modules) {
        this.modules = modules;
    }

    public List<Module> getModulesInCategory(Category c) {
        return this.modules.values().stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }

    public Module get(Class<? extends Module> mod) {
        return this.modules.get(mod);
    }

    public <T extends Module> T getModule(Class<T> mod) {
        return (T) this.modules.get(mod);
    }

    public List<Module> getModulesThatContainText(String text) {
        return this.getModules().stream().filter(m -> m.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public Module getModuleByName(String name) {
        return this.modules.values().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesContains(String text) {
        return this.modules.values().stream().filter(m -> m.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public final List<Module> getToggledModules() {
        return this.modules.values().stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    public final List<Module> getArraylistModules(ArrayListMod arraylistMod, List<Module> modules) {
        return modules.stream().filter(module -> module.isEnabled() && !((module.getCategory() == Category.RENDER && arraylistMod.hideModules.isEnabled("Render")) || (module.getCategory() == Category.DISPLAY && arraylistMod.hideModules.isEnabled("Display")) || (module.getCategory() == Category.MISC && arraylistMod.hideModules.isEnabled("Misc")) || (module.getCategory() == Category.COMBAT && arraylistMod.hideModules.isEnabled("Combat") || (module.getCategory() == Category.WORLD && arraylistMod.hideModules.isEnabled("World")) || (module.getCategory() == Category.PLAYER && arraylistMod.hideModules.isEnabled("Player")) || (module.getCategory() == Category.MOVEMENT && arraylistMod.hideModules.isEnabled("Movement"))))).collect(Collectors.toList());
    }
}
