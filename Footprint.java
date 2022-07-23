package org.processmining.plugins.gettingstarted.alphaalgorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//�����㼣����
public class Footprint {
    Relation[][] footprint;
    Map<String, Integer> eventNameToMatrixIndex = new HashMap<>();

    public Footprint(Set<Event> allEvents, Set<Trace> eventLog,
                     boolean lookForLoopsOfLengthTwo) {
        int index = 0;

        for (Event event : allEvents) {
            eventNameToMatrixIndex.put(event.getName(), index++);
        }

        int numberOfEvents = allEvents.size();

        footprint = new Relation[numberOfEvents][numberOfEvents];
        for(Relation[] row: footprint){
            Arrays.fill(row,Relation.NOT_CONNECTED);
        }

        //��ȡ���ֻ�����ϵ
        for (Trace singleTrace : eventLog) {
            List<Event> eventsList = singleTrace.getEventsList();
            for (int i = 0; i < eventsList.size() - 1; i++) {

                int currentEventNumber = eventNameToMatrixIndex
                        .get(eventsList.get(i).getName());
                int nextEventNumber = eventNameToMatrixIndex
                        .get(eventsList.get(i + 1).getName());

                if (footprint[currentEventNumber][nextEventNumber] == Relation.NOT_CONNECTED) {
                    footprint[currentEventNumber][nextEventNumber] = Relation.PRECEDES;
                    footprint[nextEventNumber][currentEventNumber] = Relation.FOLLOWS;
                } else if (footprint[currentEventNumber][nextEventNumber] == Relation.FOLLOWS) {

                    footprint[currentEventNumber][nextEventNumber] = Relation.PARALLEL;
                    footprint[nextEventNumber][currentEventNumber] = Relation.PARALLEL;
                }


            }
        }
        //ѭ������Ϊ2ʱ���⴦��
        if (lookForLoopsOfLengthTwo) {
            for (Trace singleTrace : eventLog) {
                List<Event> eventsList = singleTrace.getEventsList();
                for (int i = 0; i < eventsList.size() - 2; i++) {
                    if (eventsList.get(i).equals(eventsList.get(i + 2))) {
                        int currentEventNumber = eventNameToMatrixIndex
                                .get(eventsList.get(i).getName());
                        int nextEventNumber = eventNameToMatrixIndex
                                .get(eventsList.get(i + 1).getName());
                        footprint[currentEventNumber][nextEventNumber] = Relation.PRECEDES;
                        footprint[nextEventNumber][currentEventNumber] = Relation.PRECEDES;
                    }
                }
            }
        }
    }
    //��ȡ�㼣������������Ļ��������ϵ
    public Relation getRelationType(Event firstEvent, Event secondEvent) {
        int rowIndex = eventNameToMatrixIndex.get(firstEvent.getName());
        int colIndex = eventNameToMatrixIndex.get(secondEvent.getName());
        return footprint[rowIndex][colIndex];
    }


    public boolean areConnected(Event firstEvent, Event secondEvent) {
        return getRelationType(firstEvent, secondEvent) != Relation.NOT_CONNECTED;
    }


    public boolean isFirstFollowedBySecond(Event firstEvent, Event secondEvent) {
        return getRelationType(firstEvent, secondEvent) == Relation.PRECEDES;
    }

    public boolean areEventsConnected(Set<Event> inputEvents,
                                      Set<Event> outputEvents) {

        // ���ڼ���A�������a1,a2 => a1#a2
        boolean areInputEventsConnectedBetweenThemselves = inputEvents.stream()
                .anyMatch(inputEvent1 ->
                        inputEvents.stream()
                                .anyMatch(inputEvent2 -> areConnected(inputEvent1, inputEvent2)));

        if (areInputEventsConnectedBetweenThemselves) {
            return false;
        }

        // ���ڼ���B�������b1, b2 => b1#b2��
        boolean areOutputEventsConnectedBetweenThemselves = outputEvents.stream()
                .anyMatch(outputEvent ->
                        outputEvents.stream()
                                .anyMatch(outputEvent2 -> areConnected(outputEvent, outputEvent2)));
        if (areOutputEventsConnectedBetweenThemselves) {
            return false;
        }

        // ���ڼ���A�������a�뼯��B�������b => a > b in f
        boolean allFromBFollowAllFromA = inputEvents.stream()
                .allMatch(inputEvent ->
                        outputEvents.stream()
                                .allMatch(outputEvent -> isFirstFollowedBySecond(inputEvent, outputEvent)));
        return allFromBFollowAllFromA;
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder("  ");
        String[] tmp = new String[footprint.length];
        for (String key : eventNameToMatrixIndex.keySet()) {
            tmp[eventNameToMatrixIndex.get(key)] = key;
        }

        for (int i = 0; i < footprint.length; i++) {
            toReturn.append(tmp[i]).append(' ');
        }

        toReturn.append('\n');
        for (int i = 0; i < footprint.length; i++) {
            toReturn.append(tmp[i]).append(' ');
            for (int j = 0; j < footprint.length; j++) {
                toReturn.append(footprint[i][j].symbol()).append(' ');
            }

            toReturn.append('\n');
        }

        return toReturn.toString();
    }

}
