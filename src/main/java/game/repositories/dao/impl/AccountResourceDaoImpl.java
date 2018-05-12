package game.repositories.dao.impl;

import game.repositories.dao.AccountResourceDao;
import game.repositories.dao.helpers.QueryHelper;
import game.repositories.entities.AccountResourceEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class AccountResourceDaoImpl implements AccountResourceDao {

    @Override
    public List<AccountResourceEntity> getListOfAccountResources(Integer accountId) {
        return new QueryHelper<List<AccountResourceEntity>>(){
            @Override
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                List<AccountResourceEntity> accountResources = new LinkedList<>();
                ResultSet rs = statement.executeQuery(
                        "SELECT * FROM Account_Resource WHERE account_id = " + accountId);
                while(rs.next()) {
                    AccountResourceEntity accountResource = new AccountResourceEntity(
                            rs.getInt("account_id"),
                            rs.getInt("resource_id"),
                            rs.getInt("number")
                    );
                    accountResources.add(accountResource);
                }
                returnResult(accountResources);
            }
        }.run();
    }

}
