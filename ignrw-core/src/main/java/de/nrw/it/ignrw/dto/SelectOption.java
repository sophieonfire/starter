package de.nrw.it.ignrw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SelectOption<K> {
    private K value;
    private String label;
}
