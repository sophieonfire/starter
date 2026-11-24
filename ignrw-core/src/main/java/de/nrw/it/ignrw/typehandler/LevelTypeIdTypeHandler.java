package de.nrw.it.ignrw.typehandler;

import de.nrw.it.ignrw.datatype.LevelTypeId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(LevelTypeId.class)
@MappedJdbcTypes(JdbcType.NUMERIC)
public class LevelTypeIdTypeHandler extends BaseTypeHandler<LevelTypeId> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LevelTypeId parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.levelTypeId);
    }

    @Override
    public LevelTypeId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Long levelTypeId = rs.getLong(columnName);
        return rs.wasNull() ? null : LevelTypeId.fromLevelTypeId(levelTypeId);
    }

    @Override
    public LevelTypeId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Long levelTypeId = rs.getLong(columnIndex);
        return rs.wasNull() ? null : LevelTypeId.fromLevelTypeId(levelTypeId);
    }

    @Override
    public LevelTypeId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Long levelTypeId = cs.getLong(columnIndex);
        return cs.wasNull() ? null : LevelTypeId.fromLevelTypeId(levelTypeId);
    }
}
