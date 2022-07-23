package org.processmining.plugins.gettingstarted.alphaalgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class Utils {
	//计算指数
    public static <E> Set<Set<E>> powerSet(Set<E> originalSet) {
        Set<Set<E>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }

        List<E> list = new ArrayList<>(originalSet);
        E head = list.get(0);
        Set<E> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<E> set : powerSet(rest)) {
            Set<E> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }

        return sets;
    }
    //读取CSV文件
    public static Set<Trace> readInputFromCSV(String fileName) {
        Charset charset = Charset.forName("GBK");
        Path file = FileSystems.getDefault().getPath(fileName);
        Set<Trace> toReturn = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            reader.readLine();
            List<String> eventlist=new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] events = line.split(",");
                if(events[5].equals("clickEvent0")&&!eventlist.isEmpty())
                {
                    String[] fin=new String[eventlist.size()];
                    toReturn.add(new Trace(eventlist.toArray(fin)));
                    eventlist=new ArrayList<>();
                }
                eventlist.add(events[5]);

            }
            if(!eventlist.isEmpty())
            {
                String[] fin=new String[eventlist.size()];
                toReturn.add(new Trace(eventlist.toArray(fin)));
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            return new HashSet<>();
        }

        return toReturn;
    }
}
