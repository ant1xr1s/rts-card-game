package game.repositories.dao.impl;

import game.repositories.dao.AccountDao;
import game.repositories.dao.helpers.QueryHelper;
import game.repositories.entities.AccountEntity;
import game.repositories.entities.RoomEntity;
import game.repositories.entities.UserEntity;

import java.sql.*;

public class AccountDaoImpl implements AccountDao {

    @Override
    public void createAccount(AccountEntity account) {
        new QueryHelper() {
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(
                        "INSERT IGNORE INTO Account (user_id) VALUES (?);");
                pstmt.setInt(1, account.getUser().getId());
                int status = pstmt.executeUpdate();
                connection.commit();
            }
        }.run();
    }

    @Override
    public void setRoomForAccount(Integer userId, Integer roomId) {
        new QueryHelper() {
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(
                        "UPDATE Account SET room_id=? WHERE user_id=?;");
                pstmt.setInt(1, roomId);
                pstmt.setInt(2, userId);
                int status = pstmt.executeUpdate();
                connection.commit();
            }
        }.run();
    }

    @Override
    public void deleteRoomFromAccount(Integer userId) {
        new QueryHelper() {
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(
                        "UPDATE Account SET room_id=NULL WHERE user_id=?;");
                pstmt.setInt(1, userId);
                int status = pstmt.executeUpdate();
                connection.commit();
            }
        }.run();
    }

    @Override
    public Integer getAccountIdByUserId(Integer userId) {
        return new QueryHelper<Integer>() {
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(
                        "SELECT id FROM Account WHERE user_id = ?;");
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                if(rs.next()) {
                    returnResult(rs.getInt("id"));
                }
            }
        }.run();
    }

    @Override
    public AccountEntity getAccountByUser(final UserEntity user) {
        return new QueryHelper<AccountEntity>() {
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(
                        "SELECT a.id , a.room_id, r.name, r.description, r.start_game_time, r.account_1_id, r.account_2_id FROM Account a " +
                                "LEFT JOIN Room r ON r.id = a.room_id " +
                                "WHERE user_id = ?");
                pstmt.setInt(1, user.getId());
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    AccountEntity accountEntity = new AccountEntity();
                    accountEntity.setId(rs.getInt("id"));
                    if(rs.getInt("room_id") != 0) {
                        RoomEntity roomEntity = new RoomEntity();
                        roomEntity.setId(rs.getInt("room_id"));
                        roomEntity.setName(rs.getString("name"));
                        roomEntity.setDescription(rs.getString("description"));
                        roomEntity.setStart_game_time(rs.getDate("start_game_time"));

                        if(rs.getInt("account_1_id")!=0) {
                            roomEntity.setAccount1(getUserIdByAccountId(rs.getInt("account_1_id")));
                        }

                        if(rs.getInt("account_2_id")!=0){
                            roomEntity.setAccount2(getUserIdByAccountId(rs.getInt("account_2_id")));
                        }
                        accountEntity.setRoom(roomEntity);
                    }
                    accountEntity.setUser(user);
                    returnResult(accountEntity);
                }
            }
        }.run();
    }

    @Override
    public AccountEntity getUserIdByAccountId(final Integer accountId) {
        return new QueryHelper<AccountEntity>() {
            protected void executeQuery(Statement statement, Connection connection) throws SQLException {
                PreparedStatement pstmt = connection.prepareStatement(
                        "SELECT a.user_id, u.name FROM Account a " +
                                "LEFT JOIN User u on a.user_id=u.id " +
                                "where a.id=?;");
                pstmt.setInt(1, accountId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    AccountEntity accountEntity = new AccountEntity();
                    accountEntity.setId(accountId);
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(rs.getInt("user_id"));
                    userEntity.setName(rs.getString("name"));
                    accountEntity.setUser(userEntity);
                    returnResult(accountEntity);
                }
            }
        }.run();
    }



}
