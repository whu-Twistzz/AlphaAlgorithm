package org.processmining.plugins.gettingstarted.alphaalgorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



//���������罨ģ
public class WorkflowNetwork {
    private final Set<Event> eventsList;//�
    private final Set<Place> workflowPlaces;//����
    private final Set<Pair<Event, Place>> eventToPlaceTransitions;//�-������
    private final Map<Event, Set<Place>> eventToPlaceTransitionsMap;
    private final Set<Pair<Place, Event>> placeToEventTransitions;//����-���
    private final Place in;//��ʼ
    private final Place out;//��ֹ
    private final Map<Event, Pair<Set<Place>, Set<Place>>> eventPrePostMap;

    public WorkflowNetwork(Set<Place> workflowPlaces,
                           Set<Event> eventsList,
                           Set<Pair<Event, Place>> eventToPlaceTransitions,
                           Map<Event, Set<Place>> eventToPlaceTransitionsMap,
                           Set<Pair<Place, Event>> placeToEventTransitions,
                           Place in,
                           Place out) {
        this.workflowPlaces = workflowPlaces;
        this.eventsList = eventsList;
        this.eventToPlaceTransitions = eventToPlaceTransitions;
        this.eventToPlaceTransitionsMap = eventToPlaceTransitionsMap;
        this.placeToEventTransitions = placeToEventTransitions;
        this.in = in;
        this.out = out;
        this.eventPrePostMap = createActivityPrePostMap(this.workflowPlaces, this.eventsList);
    }

    public Set<Place> getWorkflowPlaces() {
        return workflowPlaces;
    }

    public Set<Event> getEventsList() {
        return eventsList;
    }

    public Set<Pair<Event, Place>> getEventToPlaceTransitions() {
        return eventToPlaceTransitions;
    }

    public Map<Event, Set<Place>> getEventToPlaceTransitionsMap() {
        return eventToPlaceTransitionsMap;
    }

    public Set<Pair<Place, Event>> getPlaceToEventTransitions() {
        return placeToEventTransitions;
    }

    public Place getIn() {
        return in;
    }

    public Place getOut() {
        return out;
    }

    //����ͼ���ӻ��ַ�������
    @Override
    public String toString() {
        Set<Event> eventList = this.getEventsList();
        Set<Place> workflowPlaces = this.getWorkflowPlaces();
        StringBuilder sb = new StringBuilder(
                40
                        * (this.getPlaceToEventTransitions().size() + this.getEventToPlaceTransitions()
                        .size()) + eventList.size() + 15
                        * workflowPlaces.size());

        sb.append("node[shape=box];\n{\n");
        for (Event event : eventList) {
            sb.append(event).append(";\n");
        }
        sb.append("}\n");
        sb.append("node[shape=circle];\n{\n");
        for (Place place : workflowPlaces) {
            sb.append(place).append("[label=\"\"];\n");
        }
        sb.append(in).append("[label=\"start\"];\n");
        sb.append(out);
        sb.append("[label=\"end\"];\n");
        sb.append("}\n");

        for (Pair<Place, Event> transition : this.getPlaceToEventTransitions()) {
            sb.append(String.format("%s->%s;\n",
                    transition.getFirst(), transition.getSecond()));
        }

        for (Pair<Event, Place> transition : this.getEventToPlaceTransitions()) {
            sb.append(String.format("%s->%s;\n",
                    transition.getFirst(), transition.getSecond()));
        }
        sb.append("}\n");

        return sb.toString();
    }
    //���ɻ����
    private Map<Event, Pair<Set<Place>, Set<Place>>> createActivityPrePostMap(Set<Place> workflowPlaces, Set<Event> eventList) {
        Map<Event, Pair<Set<Place>, Set<Place>>> eventPrePostMap = new HashMap<>();
        for (Event event : eventList) {
            Set<Place> first = new HashSet<>();
            Set<Place> second = new HashSet<>();
            eventPrePostMap.put(event, new Pair<>(first, second));
        }

        for (Place p : workflowPlaces) {
            Set<Event> inA = p.getInEvents();
            for (Event event : inA) {
                Pair<Set<Place>, Set<Place>> pair = eventPrePostMap.get(event);
                pair.getSecond().add(p);
            }

            Set<Event> outA = p.getOutEvents();
            for (Event activity : outA) {
                Pair<Set<Place>, Set<Place>> pair = eventPrePostMap.get(activity);
                pair.getFirst().add(p);
            }
        }

        return eventPrePostMap;
    }


}
