package net.fabricmc.fabric.api.event;

public abstract class Event<T> {

    public static final int DEFAULT_PHASE = 0;

    public abstract void register(T listener);

    public abstract T invoker();

    // ponytail: phase-aware overloads so Fabric mods that order callbacks compile & run.
    // We register regardless of phase; ordering is best-effort (FIFO) for now.
    public void register(int phase, T listener) {
        register(listener);
    }

    public void addPhaseOrdering(int before, int after) {
        // no-op: single FIFO list preserves registration order
    }
}
