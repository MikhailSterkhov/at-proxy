package net.advanceteam.proxy.common.mysql.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResponseHandler {

    void executeSuccessful(ResultSet resultSet) throws SQLException;
}
