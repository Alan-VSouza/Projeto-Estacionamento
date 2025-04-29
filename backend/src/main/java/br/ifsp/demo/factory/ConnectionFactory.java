package br.ifsp.demo.factory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private DataSource ds;

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
