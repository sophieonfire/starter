package de.nrw.it.ignrw.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.nrw.it.ignrw.dto.SelectOption;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SelectResponse<K, O extends SelectOption<K>> {
    @JsonProperty("options")
    private List<O> options;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("defaultValue")
    private K defaultValue;

    public SelectResponse(List<O> options, K defaultValue) {
        this.options = List.copyOf(options);
        this.defaultValue = defaultValue;
    }

    public SelectResponse(List<O> options) {
        this(options, null);
    }
}
