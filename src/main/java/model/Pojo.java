package model;

import annotation.CSVArrayField;
import annotation.CSVField;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

@Data
public class Pojo implements Serializable {
    @CSVField(header = "fid")
    private Long id;
    @CSVField(header = "fCode")
    private Integer fCode;
    @CSVField(header = "iCode")
    private Integer iCode;
    @CSVField(header = "cur")
    private String cur;
    @CSVArrayField(delimiter = ",")
    private Collection<ChildNode> child;
}
