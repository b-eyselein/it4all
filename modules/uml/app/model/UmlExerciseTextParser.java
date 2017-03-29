package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import play.Logger;

public class UmlExerciseTextParser {

  // Java char classes do not contain german Umlauts
  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöü]*");

  private UmlExerciseTextParser() {

  }

  public static String parseTextForClassSel(String text, Path baseFormsPath, List<String> toIgnore) {
    // <span id="obj1" class="nomen" onclick="mark(this)">Krankenhaus</span>
    Map<String, String> baseforms = readBaseforms(baseFormsPath);
    List<String> toReplace = readSimpleReplacements(text, toIgnore, baseforms);

    String newText = text;
    for(String toRep: toReplace)
      newText = newText.replaceAll(toRep, "<span class=\"nomen\" onclick=\"mark(this)\">" + toRep + "</span>");

    // FIXME: bug with Station/Station(en)!

    for(Map.Entry<String, String> repWithBaseform: baseforms.entrySet())
      newText = newText.replaceAll(repWithBaseform.getKey(),
          "<span class=\"nomen\" onclick=\"mark(this)\" data-baseform=\"" + repWithBaseform.getValue() + "\">"
              + repWithBaseform.getKey() + "</span>");

    return newText;
  }

  public static List<String> readSimpleReplacements(String content, List<String> toIgnore,
      Map<String, String> baseforms) {
    Matcher matcher = CAP_WORDS.matcher(content);

    List<String> capitalizedWords = new LinkedList<>();
    while(matcher.find())
      capitalizedWords.add(content.substring(matcher.start(), matcher.end()));

    // @formatter:off
    return capitalizedWords.stream()
        .distinct()
        .filter(word -> !toIgnore.contains(word) && !baseforms.containsKey(word))
        .sorted()
        .collect(Collectors.toList());
    // @formatter:on
  }

  private static Map<String, String> readBaseforms(Path baseFormsPath) {
    Map<String, String> baseforms = new HashMap<>();
    try {
      // TODO: Ignore first line!
      Files.readAllLines(baseFormsPath).forEach(m -> {
        String[] mapping = m.split(",");
        baseforms.put(mapping[0], mapping[1]);
      });
    } catch (IOException e) {
      Logger.error("There has been an error reading the baseform mappings:", e);
    }

    return baseforms;
  }

}
