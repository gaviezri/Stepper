package dto.user.system.info;

import communication.UserSystemInfo;

import java.util.List;

public class UsersSystemInfoDTO {
    private List<UserSystemInfo> usersSystemInfo;

    public UsersSystemInfoDTO(List<UserSystemInfo> usersSystemInfo) {
        this.usersSystemInfo = usersSystemInfo;
    }

    public List<UserSystemInfo> getUsersSystemInfo() {
        return usersSystemInfo;
    }
}
