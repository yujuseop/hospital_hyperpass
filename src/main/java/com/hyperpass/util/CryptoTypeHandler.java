package com.hyperpass.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis TypeHandler: enc_ssn 등 암호화 컬럼에 명시적으로 지정하여 사용.
 * XML mapper에서 typeHandler="com.hyperpass.util.CryptoTypeHandler" 로 참조.
 *
 * BaseTypeHandler 대신 TypeHandler 직접 구현 → String 타입 전체 매핑 오염 방지.
 */
@Component
public class CryptoTypeHandler implements TypeHandler<String> {

    private final AesUtil aesUtil;

    public CryptoTypeHandler(AesUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter == null) {
            ps.setNull(i, java.sql.Types.VARCHAR);
        } else {
            ps.setString(i, aesUtil.encrypt(parameter));
        }
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? aesUtil.decrypt(value) : null;
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? aesUtil.decrypt(value) : null;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? aesUtil.decrypt(value) : null;
    }
}
