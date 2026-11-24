package de.nrw.it.ignrw.mapper;

import de.nrw.it.ignrw.datatype.LevelTypeId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class LevelTypeIdMapper  {

    public Long map(LevelTypeId levelTypeId) {
        return levelTypeId != null ? levelTypeId.getLevelTypeId() : null;
    }

    public LevelTypeId map(Long levelTypeId) {
        return levelTypeId != null ? LevelTypeId.fromLevelTypeId(levelTypeId) : null;
    }
}
