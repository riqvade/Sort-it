package com.riqvade;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class SortManager<T extends Comparable> {
    private boolean sortMode;
    private Consumer<T> resultConsumer;
    private final List<Iterator<T>> inputSuppliers = new ArrayList<>();
    private final Function<String, T> valueMapper;
    Callable<?> finalizer;

    class BufferedReaderWrapper implements Iterator<T> {
        private BufferedReader reader;
        private Function<String, T> mapper;

        public BufferedReaderWrapper(InputStream inputStream, Function<String, T> mapper) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            try {
                return reader.ready();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public T next() {
            try {
                String raw = reader.readLine();
                if (raw != null) {
                    return mapper.apply(raw);
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class ReverseIterator implements Iterator<T> {
        List<T> list;
        int index;

        public ReverseIterator(List<T> list) {
            this.list = list;
            index = list.size();
        }

        @Override
        public boolean hasNext() {
            return index > 0;
        }

        @Override
        public T next() {
            --index;
            return list.get(index);
        }
    }

    public SortManager(boolean sortMode, Function<String, T> mapper) {
        this.sortMode = sortMode;
        this.valueMapper = mapper;
    }

    public void ofFiles(Collection<String> filenames, String outFilename) throws IOException {
        Collection<InputStream> streams = new ArrayList<>();
        for (String filename : filenames) {
            InputStream stream = sortMode ? new ReversedInputFileStream(new File(filename)) :
                    new FileInputStream(filename);
            inputSuppliers.add(new BufferedReaderWrapper(stream, valueMapper));
            streams.add(stream);
        }
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outFilename));
        resultConsumer = (s)-> {
            try {
                bufferedWriter.write(s+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        finalizer = ()-> {
            try {
                bufferedWriter.close();
                for (InputStream stream : streams) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        };
    }

    public void ofCollections(Collection<List<T>> inputs, List<T> out) {
        for (List<T> input : inputs) {
            inputSuppliers.add(sortMode ? new ReverseIterator(input) : input.iterator());
        }
        resultConsumer = (s)->out.add(s);
    }

    public void sortData() throws Exception {
        TreeMap<Integer, T> values = new TreeMap<>();
        for (int i = 0; i < inputSuppliers.size(); i++) {
            values.put(i, inputSuppliers.get(i).next());
        }
        do {
            Integer preferredIndex = -1;
            for (Map.Entry<Integer, T> e : values.entrySet()) {
                if (e.getValue() != null) {
                    preferredIndex = e.getKey();
                    break;
                }
            }
            if (preferredIndex == -1) {
                break;
            }
            for (Map.Entry<Integer, T> entry : values.entrySet()) {
                if (entry.getValue() != null && (entry.getValue().compareTo(values.get(preferredIndex)) > 0 == sortMode)) {
                    preferredIndex = entry.getKey();
                }
            }
            resultConsumer.accept(values.get(preferredIndex));
            T newValue;
            if (inputSuppliers.get(preferredIndex).hasNext()) {
                newValue = inputSuppliers.get(preferredIndex).next();
            } else {
                newValue = null;
            }
            if (newValue != null && (newValue.compareTo(values.get(preferredIndex)) > 0 == sortMode)) {
                System.out.println("Warning: file "+preferredIndex+" has incorrect sorting.");
                values.put(preferredIndex, null);
            } else {
                values.put(preferredIndex, newValue);
            }
        } while (true);
        if (finalizer != null) {
            finalizer.call();
        }
    }
}
