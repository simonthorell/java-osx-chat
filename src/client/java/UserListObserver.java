package client.java;

import java.util.List;

public interface UserListObserver {
    void userListUpdated(List<String> newUsers);
}