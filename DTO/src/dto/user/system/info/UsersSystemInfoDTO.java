package dto.user.system.info;

import communication.UserSystemInfo;

import java.util.Collection;
import java.util.List;

public class UsersSystemInfoDTO {
    private final Collection<UserSystemInfo> usersSystemInfo;

    public UsersSystemInfoDTO(Collection<UserSystemInfo> usersSystemInfo) {
        this.usersSystemInfo = usersSystemInfo;
    }

    public Collection<UserSystemInfo> getUsersSystemInfo() {
        return usersSystemInfo;
    }
}
