package de.nrw.it.ignrw.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * UUIDTypeHandler für MyBatis
 * =========================
 *
 * Diese Klasse ermöglicht die bidirektionale Konvertierung zwischen Java UUID-Objekten
 * und deren Datenbankrepräsentationen. Der Handler ist essenziell für die korrekte
 * Verarbeitung des userUid-Feldes in der User-Klasse.
 *
 * Der Handler unterstützt verschiedene Datenbanktypen:
 * - VARCHAR: UUID wird als String gespeichert (für alle Datenbanken kompatibel)
 * - OTHER: Für native UUID-Unterstützung in PostgreSQL
 * - BINARY: UUID wird als 16-Byte-Array gespeichert
 *
 * Dieser Handler ist in mybatis-config.xml registriert und wird automatisch
 * von MyBatis verwendet, wenn UUID-Objekte persistiert oder geladen werden.
 */
@MappedTypes(UUID.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {
	
	/**
	 * Setzt einen nicht-null UUID-Parameter in der PreparedStatement.
	 *
	 * @param ps PreparedStatement, in dem der Parameter gesetzt wird
	 * @param i Index-Position des Parameters
	 * @param parameter Der UUID-Wert, der gesetzt werden soll
	 * @param jdbcType Der JDBC-Typ, der für die Parametereinstellung verwendet werden soll
	 * @throws SQLException wenn ein Datenbank-Fehler auftritt
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
		if (jdbcType == JdbcType.VARCHAR) {
			// Als String speichern
			ps.setString(i, parameter.toString());
		} else if (jdbcType == JdbcType.OTHER || jdbcType == null) {
			// PostgreSQL unterstützt den nativen UUID-Typ (als OTHER)
			ps.setObject(i, parameter);
		} else if (jdbcType == JdbcType.BINARY) {
			// Binäre Darstellung des UUIDs (für Datenbanken, die BINARY unterstützen)
			ps.setBytes(i, uuidToBytes(parameter));
		} else {
			throw new SQLException("UUIDTypeHandler kann nicht mit JdbcType " + jdbcType + " verwendet werden");
		}
	}
	
	/**
	 * Liest einen UUID-Wert aus einem ResultSet anhand des Spaltennamens.
	 *
	 * @param rs Das ResultSet, aus dem der Wert gelesen werden soll
	 * @param columnName Der Name der Spalte, die den UUID-Wert enthält
	 * @return Das geparste UUID-Objekt oder null, wenn der Wert NULL ist
	 * @throws SQLException wenn ein Datenbank-Fehler auftritt
	 */
	@Override
	public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Object uuid = rs.getObject(columnName);
		if (rs.wasNull()) {
			return null;
		}
		return parseUUID(uuid);
	}
	
	/**
	 * Liest einen UUID-Wert aus einem ResultSet anhand des Spaltenindex.
	 *
	 * @param rs Das ResultSet, aus dem der Wert gelesen werden soll
	 * @param columnIndex Der Index der Spalte, die den UUID-Wert enthält
	 * @return Das geparste UUID-Objekt oder null, wenn der Wert NULL ist
	 * @throws SQLException wenn ein Datenbank-Fehler auftritt
	 */
	@Override
	public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Object uuid = rs.getObject(columnIndex);
		if (rs.wasNull()) {
			return null;
		}
		return parseUUID(uuid);
	}
	
	/**
	 * Liest einen UUID-Wert aus einem CallableStatement anhand des Spaltenindex.
	 *
	 * @param cs Das CallableStatement, aus dem der Wert gelesen werden soll
	 * @param columnIndex Der Index der Spalte, die den UUID-Wert enthält
	 * @return Das geparste UUID-Objekt oder null, wenn der Wert NULL ist
	 * @throws SQLException wenn ein Datenbank-Fehler auftritt
	 */
	@Override
	public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Object uuid = cs.getObject(columnIndex);
		if (cs.wasNull()) {
			return null;
		}
		return parseUUID(uuid);
	}
	
	/**
	 * Zerlegt ein Objekt zu einem UUID.
	 * Unterstützt verschiedene Eingabetypen für maximale Flexibilität:
	 * - UUID-Objekte werden direkt zurückgegeben
	 * - Strings werden als UUID zerlegt
	 * - Byte-Arrays werden in UUIDs konvertiert
	 * - Andere Objekte werden über toString() versucht zu konvertieren
	 *
	 * @param uuid Das zu zerteilende Objekt
	 * @return Das zerteilte UUID oder null bei Fehlern oder null-Eingabe
	 */
	private UUID parseUUID(Object uuid) {
		if (uuid == null) {
			return null;
		}
		
		// Wenn es bereits ein UUID ist
		if (uuid instanceof UUID) {
			return (UUID) uuid;
		}
		
		// Wenn es ein String ist
		if (uuid instanceof String) {
			try {
				return UUID.fromString((String) uuid);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		
		// Wenn es ein byte-Array ist
		if (uuid instanceof byte[]) {
			return bytesToUUID((byte[]) uuid);
		}
		
		// Fallback: toString() versuchen
		try {
			return UUID.fromString(uuid.toString());
		} catch (IllegalArgumentException e) {
			// Wenn das Format nicht korrekt ist, geben wir null zurück
			return null;
		}
	}
	
	/**
	 * Konvertiert ein UUID in ein Byte-Array (16 Byte).
	 * Die Implementierung verwendet MSB (Most Significant Bits) und
	 * LSB (Least Significant Bits) für eine effiziente binäre Darstellung.
	 *
	 * @param uuid Das zu konvertierende UUID
	 * @return Ein 16-Byte-Array, das das UUID repräsentiert
	 */
	private byte[] uuidToBytes(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];
		
		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> ((7 - i) * 8));
			buffer[8 + i] = (byte) (lsb >>> ((7 - i) * 8));
		}
		
		return buffer;
	}
	
	/**
	 * Konvertiert ein Byte-Array zurück zu einem UUID.
	 * Diese Methode ist das Gegenstück zu uuidToBytes und
	 * rekonstruiert das originale UUID aus seiner binären Darstellung.
	 *
	 * @param bytes Das zu konvertierende Byte-Array (muss 16 Bytes lang sein)
	 * @return Das rekonstruierte UUID oder null bei ungültiger Eingabe
	 */
	private UUID bytesToUUID(byte[] bytes) {
		if (bytes == null || bytes.length != 16) {
			return null;
		}
		
		long msb = 0;
		long lsb = 0;
		
		for (int i = 0; i < 8; i++) {
			msb = (msb << 8) | (bytes[i] & 0xff);
			lsb = (lsb << 8) | (bytes[i + 8] & 0xff);
		}
		
		return new UUID(msb, lsb);
	}
}