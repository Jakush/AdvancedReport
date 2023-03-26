package me.retamrovec.advancedreport.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * A class that implements Taskable.java is executing its specific task
 * while executing task, there can be an exception. This interface
 * is sending more detailed info about exception.
 */
public interface Taskable {

    /**
     * Runs class specific task
     *
     * @return Returns if it was successful
     */
    default Valuable runTask(@NotNull Runnable runnable) {
        Valuable valuable = new Valuable(null, null);
        try {
            runnable.run();
        } catch (Exception e) {
            valuable.setKey(false);
            valuable.setValue(e);
            return valuable;
        }
        valuable.setKey(true);
        return valuable;
    }

    class Valuable {

        private Object key;
        private Object value;
        public Valuable(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

}