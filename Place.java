package org.processmining.plugins.gettingstarted.alphaalgorithm;

import java.util.Set;

//定义库所，用于连接活动集
public class Place {
   
    Pair<Set<Event>, Set<Event>> eventsPair;

    String name;
    public static char nameGenerator1 = 'A';
    public static char nameGenerator2 = 'A';

    private boolean token;
    
    public Place(Set<Event> in, Set<Event> out) {
        eventsPair = new Pair<>(in, out);
      //防止库所重名
        if(nameGenerator2<='Z')
            name = "" + nameGenerator1+nameGenerator2++;
        else
        {
            nameGenerator2='A';
            nameGenerator1++;
            name = "" + nameGenerator1+nameGenerator2++;
        }
    }

    public Place(String name, Set<Event> in, Set<Event> out) {
        eventsPair = new Pair<>(in, out);
        this.name = name;
    }

    public Set<Event> getInEvents() {
        return eventsPair.getFirst();
    }

    public Set<Event> getOutEvents() {
        return eventsPair.getSecond();
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((eventsPair == null) ? 0 : eventsPair.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Place other = (Place) obj;
        if (eventsPair == null) {
            if (other.eventsPair != null)
                return false;
        } else if (!eventsPair.equals(other.eventsPair))
            return false;
        return true;
    }


    public boolean isSuperPlace(Place potentialSubPlace) {
        if (getInEvents().containsAll(potentialSubPlace.getInEvents())) {
            if (getOutEvents().containsAll(
                    potentialSubPlace.getOutEvents())) {
                return true;
            }
        }
        return false;
    }

    public void addInEvent(Event event) {
        eventsPair.getFirst().add(event);
    }

    public void addOutEvent(Event eventName) {
        eventsPair.getSecond().add(eventName);
    }

    public String getName() {
        return name;
    }

    public boolean hasToken() {
        return token;
    }

    public void putToken() {
        token = true;
    }

    public void takeToken() {
        token = false;
    }

    public void clearToken(){ token = false;}
}
