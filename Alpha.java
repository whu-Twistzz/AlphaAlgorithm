package org.processmining.plugins.gettingstarted.alphaalgorithm;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;




public class Alpha {
    public static boolean takeInAccountLoopsLengthTwo = true;

    public static WorkflowNetwork discoverWorkflowNetwork(Set<Trace> eventsLogArg) {
        Set<Loop> recordedLLOs = new HashSet<>();
        for (Trace trace : eventsLogArg) {
            preProcessLLOs(trace, recordedLLOs);
        }

        Set<Event> eventList = new HashSet<>();
        HashSet<Event> startingEvents = new HashSet<>();
        HashSet<Event> endingEvents = new HashSet<>();


        Alpha.extractEvents(eventsLogArg, eventList,
                startingEvents, endingEvents);


        Footprint footprint = new Footprint(eventList, eventsLogArg,
                takeInAccountLoopsLengthTwo);
        System.out.println("------------------------");
        System.out.println("Footprint matrix:");
        System.out.println(footprint);
        System.out.println("------------------------");


        Set<Place> XL = Alpha.getPlacesFromFootprint(footprint, eventList);


        Set<Place> workflowPlaces = Alpha.reducePlaces(XL); //PL
        Alpha.postProcessWF(recordedLLOs, workflowPlaces, eventList);


        Set<Pair<Event, Place>> eventToPlaceTransitions = new HashSet<>();
        Map<Event, Set<Place>> eventToPlaceTransitionsMap = new HashMap<>();
        Set<Pair<Place, Event>> placeToEventTransitions = new HashSet<>();
        Alpha.createEventToPlaceTransitions(eventList, workflowPlaces, eventToPlaceTransitions, eventToPlaceTransitionsMap);
        Alpha.createPlaceToEventTransitions(eventList, workflowPlaces, placeToEventTransitions);

        //绘制流程图
        Place in = new Place("in", new HashSet<>(), new HashSet<>());
        Place out = new Place("out", new HashSet<>(), new HashSet<>());
        Alpha.connectSourceAndSink(in, out, startingEvents, endingEvents, workflowPlaces, placeToEventTransitions, eventToPlaceTransitions);
        WorkflowNetwork network = new WorkflowNetwork(workflowPlaces, eventList, eventToPlaceTransitions, eventToPlaceTransitionsMap, placeToEventTransitions, in, out);
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        gv.add(network.toString());
        System.out.println(gv.getDotSource());
        String type = "png";
        File fout = new File("E:\\prompic\\graphOut." + type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), fout);
        return network;
    }


   //抽取事件
    private static void extractEvents(Set<Trace> eventLog, Set<Event> allEvents,
                                      Set<Event> startingEvents, Set<Event> endingEvents) {
        allEvents.clear();
        startingEvents.clear();
        endingEvents.clear();
        for (Trace singleTrace : eventLog) {
            startingEvents.add(singleTrace.getFirstEvent());
            endingEvents.add(singleTrace.getLastEvent());
            allEvents.addAll(singleTrace.getEventsList());
        }
    }

   
    private static Set<Place> getPlacesFromFootprint(Footprint footprint, Set<Event> eventList) {
        Set<Place> xl = new HashSet<>();
        System.out.println("Getting places from footprint:");
        Set<Set<Event>> powerSet = Utils.powerSet(eventList);
        System.out.println("Got powerSet: " + powerSet.size());
        powerSet.remove(new HashSet<Event>());
        @SuppressWarnings("unchecked")
        Set<Event>[] powerSetArray = powerSet.toArray(new Set[powerSet.size()]);
        System.out.println("Power set cast to array");
        for (int i = 0; i < powerSetArray.length; i++) {
            Set<Event> first = powerSetArray[i];
            for (int j = 0; j < powerSetArray.length; j++) {
                if (i == j) {
                    continue;
                }

                Set<Event> second = powerSetArray[j];
                if (footprint.areEventsConnected(first, second)) {
                    xl.add(new Place(first, second));
                }
            }
        }

        System.out.println("Places (XL) created from footprint. # of places in XL: " + xl.size());
        return xl;
    }
    //去除冗余
    private static Set<Place> reducePlaces(Set<Place> xl) {
        Set<Place> toRemove = new HashSet<>();
        Place[] potentialPlaces = xl.toArray(new Place[xl.size()]);
        for (int i = 0; i < potentialPlaces.length - 1; i++) {
            Place potentialPlace1 = potentialPlaces[i];
            for (int j = i + 1; j < potentialPlaces.length; j++) {
                if (potentialPlace1.getInEvents().containsAll(
                        potentialPlaces[j].getInEvents())) {
                    if (potentialPlaces[i].getOutEvents().containsAll(
                            potentialPlaces[j].getOutEvents())) {
                        toRemove.add(potentialPlaces[j]);
                        continue;
                    }
                }

                if (potentialPlaces[j].getInEvents().containsAll(
                        potentialPlaces[i].getInEvents())) {
                    if (potentialPlaces[j].getOutEvents().containsAll(
                            potentialPlaces[i].getOutEvents())) {
                        toRemove.add(potentialPlaces[i]);
                    }
                }
            }
        }

        Set<Place> yl = new HashSet<>(xl);
        yl.removeAll(toRemove);
        return yl;
    }
    //建立起活动——库所连接
    private static void createEventToPlaceTransitions(final Set<Event> eventList,
                                                      final Set<Place> workflowPlaces,
                                                      final Set<Pair<Event, Place>> eventToPlaceTransitions,
                                                      final Map<Event, Set<Place>> eventToPlaceTransitionsMap) {
        eventToPlaceTransitions.clear();
        eventToPlaceTransitionsMap.clear();
        for (Event event : eventList) {
            Set<Place> eventToPlace = new HashSet<>();
            eventToPlaceTransitionsMap.put(event, eventToPlace);
            workflowPlaces.stream()
                    .filter(place -> place.getInEvents().contains(event))
                    .forEach(place -> {
                        eventToPlaceTransitions.add(new Pair<>(event, place));
                        eventToPlace.add(place);
                    });
        }
    }
    //建立起库所——活动连接
    private static void createPlaceToEventTransitions(final Set<Event> eventList,
                                                      final Set<Place> workflowPlaces,
                                                      final Set<Pair<Place, Event>> placeToEventTransitions) {
        placeToEventTransitions.clear();
        for (Event event : eventList) {
            placeToEventTransitions.addAll(
                    workflowPlaces.stream()
                            .filter(place -> place.getOutEvents().contains(event))
                            .map(place -> new Pair<>(place, event))
                            .collect(Collectors.toList())
            );
        }
    }
    //起止活动连接
    private static void connectSourceAndSink(final Place in,
                                             final Place out,
                                             final Set<Event> startingEvents,
                                             final Set<Event> endingEvents,
                                             final Set<Place> workflowPlaces,
                                             final Set<Pair<Place, Event>> placeToEventTransitions,
                                             final Set<Pair<Event, Place>> eventToPlaceTransitions) {
        for (Event startEvent : startingEvents) {
            in.addOutEvent(startEvent);
            placeToEventTransitions
                    .add(new Pair<>(in, startEvent));
        }

        for (Event endEvent : endingEvents) {
            out.addInEvent(endEvent);
            eventToPlaceTransitions.add(new Pair<>(endEvent, out));
        }

        workflowPlaces.add(in);
        workflowPlaces.add(out);
    }

    //判断是否存在单活动死循环
    private static int checkForCycleLengthOne(List<Event> eventsInTrace) {
        for (int i = 0; i < eventsInTrace.size() - 1; i++) {
            if (eventsInTrace.get(i).equals(eventsInTrace.get(i + 1))) {
                return i;
            }
        }

        return -1;
    }


    private static void preProcessLLOs(Trace singleTrace, Set<Loop> recordedLLOs) {
        int start;
        List<Event> eventsInTrace = singleTrace.getEventsList();
        while ((start = checkForCycleLengthOne(eventsInTrace)) != -1) {
            int prev = start - 1;
            int currentEvent = start;
            while (currentEvent < eventsInTrace.size() - 1 && eventsInTrace.get(currentEvent).equals(eventsInTrace.get(currentEvent + 1))) {
                currentEvent++;
            }

            currentEvent++;
            Loop llo = new Loop(eventsInTrace.get(prev),
                    eventsInTrace.get(start), eventsInTrace.get(currentEvent));
            int numberOfOccurrencesOfRepeatingEvent = currentEvent - start;
            for (int i = 0; i < numberOfOccurrencesOfRepeatingEvent; i++) {
                eventsInTrace.remove(start);
            }

            recordedLLOs.add(llo);
        }

    }

    private static void postProcessWF(Set<Loop> recordedLLOs, Set<Place> workflowPlaces, Set<Event> eventList) {
        Queue<Loop> lloQueue = new LinkedList<>(recordedLLOs);
        while (!lloQueue.isEmpty()) {
            Loop llo = lloQueue.poll();
            Event in = llo.getPrevEvent();
            Event out = llo.getNextEvent();
            boolean used = false;
            for (Place place : workflowPlaces) {
                if (place.getInEvents().contains(in)
                        && place.getOutEvents().contains(out)) {
                    place.addInEvent(llo.getLoopedEvent());
                    place.addOutEvent(llo.getLoopedEvent());
                    eventList.add(llo.getLoopedEvent());
                    used = true;


                    break;
                }
            }

            if (!used) {
                lloQueue.add(llo);
            }
        }
    }
}
