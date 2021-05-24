import annotation.CSVArrayField;
import annotation.CSVField;
import annotation.CSVObjectField;
import io.vavr.control.Try;
import lombok.Data;
import model.Pojo;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.util.*;

public class Main {
    private Class<?> type;
    private String value;
    private Map<String, String> objects = new HashMap<>();

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException {
        final var main = new Main();
        main.objects.put("fid", "1");
        main.objects.put("fCode", "2");
        main.objects.put("iCode", "3");
        main.objects.put("cur", "string");
        main.objects.put("bt", "123,456");
        main.objects.put("rro", "123.01,456.012");

        final Pojo extracted = main.extracted(Pojo.class, "", 0);
        System.out.println(extracted);
    }

    private <T> T extracted(Class<T> clazz, String delimiter, Integer index) throws ClassNotFoundException, IllegalAccessException {
        final var t = Try.of(() -> clazz.getConstructor().newInstance())
                .onFailure(throwable -> System.out.println("need public no args constructor"))
                .getOrElseThrow(throwable -> new RuntimeException("need public no args constructor"));

        final var declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (Collection.class.isAssignableFrom(declaredField.getType())) {
                final var annotation = declaredField.getAnnotation(CSVArrayField.class);
                if (annotation == null) {
                    continue;
                }
                Type genericType = declaredField.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    final var type = Arrays.stream(parameterizedType.getActualTypeArguments()).findFirst().orElseThrow(() -> new RuntimeException("error to get parameterizedType with " + declaredField));
                    final var typeName = type.getTypeName();
                    final Class<?> aClass = Class.forName(typeName);
                    final var inDelimiter = annotation.delimiter();
                    final var length = extractedCollectionLength(aClass, inDelimiter);
                    final Collection<?> objects = extractedCollection(aClass, inDelimiter, length);
                    declaredField.set(t, objects);
                }
            }
            if (declaredField.getAnnotation(CSVObjectField.class) != null) {
                final Object extracted = extracted(declaredField.getType(), delimiter, index);
                declaredField.set(t, extracted);
            }
            final var annotation = declaredField.getAnnotation(CSVField.class);
            if (annotation != null) {
                final var header = annotation.header();
                String result;
                if (StringUtils.isNotBlank(delimiter)) {
                    result = objects.get(header).split(delimiter)[index];
                } else {
                    result = objects.get(header);
                }
                if (Boolean.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, Boolean.valueOf(result));
                }
                if (String.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, result);
                }
                if (Integer.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, Integer.valueOf(result));
                }
                if (Long.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, Long.valueOf(result));
                }
                if (Double.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, Double.valueOf(result));
                }
            }
        }
        return t;
    }

    private Integer extractedCollectionLength(Class clazz, String delimiter) {
        final var declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            final var annotation = declaredField.getAnnotation(CSVField.class);
            if (annotation == null) {
                continue;
            }
            final var header = annotation.header();
            return objects.get(header).split(delimiter).length;
        }
        return 0;
    }

    private <T> Collection<T> extractedCollection(Class<T> clazz, String delimiter, Integer length) throws ClassNotFoundException, IllegalAccessException {
        final var ts = new ArrayList<T>(length);
        for (Integer i = 0; i < length; i++) {
            final var extracted = extracted(clazz, delimiter, i);
            ts.add(extracted);
        }
        return ts;
    }
}

@Data
class ReflectModel {

}
