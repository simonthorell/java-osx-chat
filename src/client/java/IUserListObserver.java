package client.java;

import java.util.List;

public interface IUserListObserver {
    void userListUpdated(List<String> newUsers);
}