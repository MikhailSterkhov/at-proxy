package net.advanceteam.proxy.common.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.Getter;
import net.advanceteam.proxy.common.mysql.handler.ResponseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnection {

    @Getter
    private Connection connection;

    @Getter
    private final MysqlDataSource mysqlDataSource;


    public MySQLConnection(String user,
                           String password,
                           String host,
                           String database,
                           int port) {

        this.mysqlDataSource = new MysqlDataSource();

        mysqlDataSource.setUser(user);
        mysqlDataSource.setPort(port);
        mysqlDataSource.setPassword(password);
        mysqlDataSource.setServerName(host);
        mysqlDataSource.setDatabaseName(database);

        mysqlDataSource.setEncoding("UTF-8");
        mysqlDataSource.setUseSSL(false);

        try {
            connection = mysqlDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выполнение запроса
     *
     * @param execute - запрос
     * @param objects - аргументы
     */
    public void execute(String execute, Object... objects) {
        try {
            PreparedStatement preparedStatement = prepareStatement(execute, objects);

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выполнение запроса с получением результатов
     *
     * @param execute - запрос
     * @param responseHandler - результаты
     * @param objects - аргументы
     */
    public void executeQuery(String execute, ResponseHandler responseHandler, Object... objects) {
        try {
            PreparedStatement preparedStatement = prepareStatement(execute, objects);

            responseHandler.executeSuccessful(preparedStatement.executeQuery());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создание запроса в базу данных
     *
     * @param execute - запрос
     * @param objects - аргументы
     */
    public PreparedStatement prepareStatement(String execute, Object... objects) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(execute);

        int number = 1;

        for (Object object : objects) {
            preparedStatement.setObject(number, object);

            number++;
        }

        return preparedStatement;
    }
}
