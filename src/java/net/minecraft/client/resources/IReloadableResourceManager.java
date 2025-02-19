package net.minecraft.client.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {
    void reloadResources(List<IResourcePack> p_110541_1_, boolean lang);

    void registerReloadListener(IResourceManagerReloadListener reloadListener);
}
