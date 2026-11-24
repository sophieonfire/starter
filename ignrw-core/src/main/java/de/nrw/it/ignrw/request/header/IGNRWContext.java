package de.nrw.it.ignrw.request.header;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class IGNRWContext {

    private UUID orgUuid;

}
