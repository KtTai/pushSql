package com.kt.pushsqlspringboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Transactional
@Service
public class UpdatePushSqlService {

    static String DRIVER_CLASS="oracle.jdbc.driver.OracleDriver"; //oracle的驱动
    private Logger logger = LoggerFactory.getLogger("pushSqlService");

    @Value("${myProp.sqlFile}")
    String SQLValue;
    public void executeSql (String tns,String userName, String password) throws SQLException {
        // 数据库链接
        StringBuilder oracleUrlStrB = new StringBuilder();
        oracleUrlStrB.append("jdbc:oracle:thin:@");
        oracleUrlStrB.append(tns);
        oracleUrlStrB.append(":1521:ORCL");

        Connection connection = this.getConnection(oracleUrlStrB.toString(), userName, password);

        String sql = SQLValue;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        boolean execute = preparedStatement.execute();
        int num = preparedStatement.executeUpdate();
        connection.close();
        logger.info("----- {} 执行完成，修改数据数量：{}",tns,num);
    }

    /**
     * 获取Connection对象
     *
     * @return
     */
    private Connection getConnection(String url, String userName, String password) {
        Connection connection;
        try {
            Class.forName(DRIVER_CLASS);
            connection = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException e) {
            logger.info("-+-+-+-+-+ {} 执行失败，找不到驱动！",url);
            throw new RuntimeException("class not find !", e);
        } catch (SQLException e) {
            logger.info("-+-+-+-+-+ {} 执行失败，检查sql语句！{}",url,e);
            throw new RuntimeException("get connection error!", e);
        }

        return connection;
    }
}
