package client.java.net;

import java.util.List;

public interface IUserListObserver {
    void userListUpdated(List<String> newUsers);
}