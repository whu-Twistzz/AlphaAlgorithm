package org.processmining.plugins.gettingstarted.alphaalgorithm;

//��ֹ����ͼ�г��ֵ����ѭ��
public class Loop {
    Event prevEvent;
    Event loopedEvent;
    Event nextEvent;

    public Loop(Event prevEvent, Event loopedEvent,
                Event nextEvent) {
        this.prevEvent = prevEvent;
        this.loopedEvent = loopedEvent;
        this.nextEvent = nextEvent;
    }

    public Event getPrevEvent() {
        return prevEvent;
    }

    public void setPrevEvent(Event prevEvent) {
        this.prevEvent = prevEvent;
    }

    public Event getLoopedEvent() {
        return loopedEvent;
    }

    public void setLoopedEvent(Event loopedEvent) {
        this.loopedEvent = loopedEvent;
    }

    public Event getNextEvent() {
        return nextEvent;
    }

    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public String toString() {
        return "LoopLengthOne [prev=" + prevEvent + ", looped="
                + loopedEvent + ", next=" + nextEvent + "]";
    }

}
