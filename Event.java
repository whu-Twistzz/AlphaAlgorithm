package org.processmining.plugins.gettingstarted.alphaalgorithm;

//�����¼�
public class Event {
    private String name;

    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Event)) {
            return false;
        }

        return this.name.equals(((Event) obj).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
