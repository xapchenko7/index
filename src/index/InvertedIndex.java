package index;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class InvertedIndex {

    public final static int NUMBER_OF_THREADS = 10;

    public static void main(String[] args) {
        long m = System.currentTimeMillis();

        File directory = new File("C://Users//xapch//Desktop//MainDir");
        Map<String, List<String>> map = processDirectory(directory);

        Map<String, List<String>> sortedMap = getSortedMap(invertedIndex(map));
        System.out.println((double) (System.currentTimeMillis() - m));
        sortedMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue() + "\n");
        });
    }

    public static Map<String, List<String>> invertedIndex(Map<String, List<String>> map) {
        Map<String, List<String>> indexses = new ConcurrentHashMap<>();
        List<String> filesPath = getAllFilesPath(map);
        String[] allWords = getAllWords(map);
        for (int i = 0; i < allWords.length; i++) {
            List<String> files = new ArrayList<>();
            String word = allWords[i];

            for (int j = 0; j < filesPath.size(); j++) {
                if (getWordsOfCurrentFile(map, filesPath.get(j)).get(0).contains(word)) {
                    files.add(filesPath.get(j));
                }
                indexses.put(word, files);
            }
        }

        return indexses;
    }

    public static Map<String, List<String>> processDirectory(File directory) {
        List<File> directories = getListOfDir(directory);
        Map<String, List<String>> filePathAndWordsInFile = new ConcurrentHashMap<>();

        ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 0; i < directories.size(); i++) {
            File[] files = directories.get(i).listFiles();
            for (final File f : files) {
                service.execute(new Runnable() {
                    List<String> words = null;

                    @Override
                    public void run() {
                        StringBuilder sb = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                            words = getWordsList(String.valueOf(sb));
                            filePathAndWordsInFile.put(f.getPath(), words);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        service.shutdown();
        return filePathAndWordsInFile;
    }

    public static String[] getWords(String text) {
        String[] subStr;
        String delimeter = "\\W+";
        subStr = text.split(delimeter);

        return subStr;
    }

    public static List<String> getWordsList(String text) {
        String[] subStr;
        String delimeter = "\\W+";
        subStr = text.split(delimeter);

        List<String> words = Arrays.asList(subStr);

        return words;
    }

    public static Map getSortedMap(Map<String, List<String>> map) {
        Map<String, List<String>> sortedMap = new TreeMap<>(map);
        return sortedMap;
    }

    public static List<File> getListOfDir(File mainDirectory) {
        File[] files = mainDirectory.listFiles();
        List<File> dirs = new ArrayList<>();
        for (final File f : files) {
            if (!f.isFile()) {
                dirs.add(f);
            }
        }
        return dirs;
    }

    public static List<String> getWordsOfCurrentFile(Map<String, List<String>> map, String filePath) {
        List<String> wordInFile = new ArrayList<>();
        map.entrySet().forEach(entry -> {
            if (entry.getKey().equals(filePath))
                wordInFile.add(String.valueOf(entry.getValue()));
        });

        return wordInFile;
    }

    public static List<String> getAllFilesPath(Map<String, List<String>> map) {
        List<String> filesPath = new ArrayList<>();
        map.entrySet().forEach(entry -> {
            filesPath.add(entry.getKey());
        });

        return filesPath;
    }

    public static String[] getAllWords(Map<String, List<String>> map) {
        List<String> words = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        map.entrySet().forEach(entry -> {
            words.add(String.valueOf(entry.getValue()));
        });

        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
        }
        return getWords(sb.toString());
    }
}