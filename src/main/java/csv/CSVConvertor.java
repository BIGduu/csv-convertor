package csv;

import io.vavr.control.Try;
import lombok.Data;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class CSVConvertor {
    private List<String> headers = new ArrayList<>();
    private List<Map<String, String>> objects = new ArrayList<>();


    public void parse() {
        String property = System.getProperty("user.dir") + File.separator + "test.csv";
        final var collections = readCSVLines(property);
        headers = collections.stream().findFirst().orElse(new ArrayList<>());
        collections.stream().skip(1L).forEach(this::parseObjects);

    }

    private void parseObjects(List<String> bodies) {
        for (int i = 0; i < bodies.size(); i++) {
            final var object = new HashMap<String, String>();
            final var header = headers.get(i);
            final var body = bodies.get(i);
            object.put(header, body);
            objects.add(object);
        }
    }

    private List<List<String>> readCSVLines(String csvPath) {
        final var file = new File(csvPath);
        return Try.withResources(() -> new FileInputStream(file))
                .of(this::parseCSVlines)
                .onFailure(System.out::println)
                .getOrElse(this::initEmptyCSVLines);
    }

    private ArrayList<List<String>> initEmptyCSVLines() {
        final var strings = new ArrayList<String>();
        final ArrayList<List<String>> collections = new ArrayList<>();
        collections.add(strings);
        return collections;
    }

    private ArrayList<List<String>> parseCSVlines(FileInputStream fileInputStream) throws IOException {
        var line = "";
        var lines = new ArrayList<List<String>>();
        final var inputStreamReader = new InputStreamReader(fileInputStream);
        final var bufferedReader = new BufferedReader(inputStreamReader);
        while ((line = bufferedReader.readLine()) != null) {
            final var split = line.split("\\|");
            final var lineCollection = Arrays.stream(split).collect(Collectors.toList());
            lines.add(lineCollection);
        }
        return lines;
    }
}
