package model;

import annotation.CSVField;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChildNode implements Serializable {
    @CSVField(header = "bt")
    private String bet;
    @CSVField(header = "rro")
    private Double value;
}
