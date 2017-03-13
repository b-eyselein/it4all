package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import play.Logger;

public class ExerciseTextReader {
  
  // Java char classes do not contain german Umlauts
  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöü]*");
  
  private static final Path BASE_PATH = Paths.get("conf", "resources");
  private static final Path STOP_WORDS_PATH = Paths.get(BASE_PATH.toString(), "stopwords.txt");
  private static final Path MAPPINGS_PATH = Paths.get(BASE_PATH.toString(), "mappings.csv");
  
  private ExerciseTextReader() {
    
  }
  
  private static String capWord(String word) {
    return word.substring(0, 1).toUpperCase() + word.substring(1);
  }
  
  public static List<String> parseExText() {
    String content = readExerciseTextFromFile();
    
    Matcher matcher = CAP_WORDS.matcher(content);
    
    List<String> capitalizedWords = new LinkedList<>();
    while(matcher.find())
      capitalizedWords.add(content.substring(matcher.start(), matcher.end()));
    
    List<String> wordsToIgnore = readStopWords();
    Map<String, String> baseforms = readBaseforms();
    
    // @formatter:off
    return capitalizedWords.stream()
        // Map words to baseforms
        .map(word -> baseforms.getOrDefault(word, word))
        // Filter out doubles
        .distinct()
        // Filter out stopwords like "Der"
        .filter(word -> !wordsToIgnore.contains(word))
        .sorted()
        .collect(Collectors.toList());
    // @formatter:on
  }
  
  private static Map<String, String> readBaseforms() {
    Map<String, String> baseforms = new HashMap<>();
    try {
      // TODO: Ignore first line!
      Files.readAllLines(MAPPINGS_PATH).forEach(m -> {
        String[] mapping = m.split(",");
        baseforms.put(mapping[0], mapping[1]);
      });
    } catch (IOException e) {
      Logger.error("There has been an error reading the baseform mappings:", e);
    }
    
    return baseforms;
  }
  
  private static String readExerciseTextFromFile() {
    try {
      Path pathToParse = Paths.get(BASE_PATH.toString(), "exerciseText.txt");
      return String.join("\n", Files.readAllLines(pathToParse));
    } catch (IOException e) {
      Logger.error("There has been an error reading the exercise text: ", e);
      return "";
    }
  }
  
  private static List<String> readStopWords() {
    try {
      return Files.readAllLines(STOP_WORDS_PATH).stream().map(ExerciseTextReader::capWord).collect(Collectors.toList());
    } catch (IOException e) { // NOSONAR
      return Collections.emptyList();
    }
  }
}
