package de.nrw.it.ignrw.typehandler;

import de.nrw.it.ignrw.datatype.RoleId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(RoleId.class)
@MappedJdbcTypes(JdbcType.NUMERIC)
public class RoleIdTypeHandler extends BaseTypeHandler<RoleId> {
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, RoleId parameter, JdbcType jdbcType) throws SQLException {
		ps.setLong(i, parameter.roleId);
	}
	
	@Override
	public RoleId getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Long roleId = rs.getLong(columnName);
		return rs.wasNull() ? null : RoleId.fromRoleId(roleId);
	}
	
	@Override
	public RoleId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Long roleId = rs.getLong(columnIndex);
		return rs.wasNull() ? null : RoleId.fromRoleId(roleId);
	}
	
	@Override
	public RoleId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Long roleId = cs.getLong(columnIndex);
		return cs.wasNull() ? null : RoleId.fromRoleId(roleId);
	}
}
