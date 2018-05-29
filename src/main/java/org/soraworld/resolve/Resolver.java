package org.soraworld.resolve;

import org.bukkit.event.Listener;
import org.soraworld.resolve.command.CommandResolve;
import org.soraworld.resolve.config.RslConfig;
import org.soraworld.resolve.constant.Constant;
import org.soraworld.resolve.listener.EventListener;
import org.soraworld.violet.VioletPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Resolver extends VioletPlugin {

    @Nonnull
    protected IIConfig registerConfig(File path) {
        return new RslConfig(path, this);
    }

    @Nonnull
    protected List<Listener> registerEvents(IIConfig config) {
        ArrayList<Listener> listeners = new ArrayList<>();
        if (config instanceof RslConfig) {
            RslConfig cfg = (RslConfig) config;
            listeners.add(new EventListener(cfg));
        }
        return listeners;
    }

    @Nullable
    protected IICommand registerCommand(IIConfig config) {
        if (config instanceof RslConfig) return new CommandResolve(Constant.PLUGIN_ID, null, (RslConfig) config, this);
        return null;
    }

    protected void afterEnable() {

    }

    protected void beforeDisable() {

    }

}
