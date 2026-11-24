package de.nrw.it.ignrw.typehandler;

import de.nrw.it.ignrw.datatype.OrgTypeId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(OrgTypeId.class)
@MappedJdbcTypes(JdbcType.NUMERIC)
public class OrgTypeIdTypeHandler extends BaseTypeHandler<OrgTypeId> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, OrgTypeId parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.orgTypeId);
    }

    @Override
    public OrgTypeId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Long orgTypeId = rs.getLong(columnName);
        return rs.wasNull() ? null : OrgTypeId.fromOrgTypeId(orgTypeId);
    }

    @Override
    public OrgTypeId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Long orgTypeId = rs.getLong(columnIndex);
        return rs.wasNull() ? null : OrgTypeId.fromOrgTypeId(orgTypeId);
    }

    @Override
    public OrgTypeId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Long orgTypeId = cs.getLong(columnIndex);
        return cs.wasNull() ? null : OrgTypeId.fromOrgTypeId(orgTypeId);
    }
}
