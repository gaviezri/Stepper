package dto.user.system.info;

import communication.UserSystemInfo;
import dto.AbstractDTO;

import java.util.Collection;
import java.util.List;

public class UsersSystemInfoDTO extends AbstractDTO {
    private final Collection<UserSystemInfo> usersSystemInfo;

    public UsersSystemInfoDTO(Collection<UserSystemInfo> usersSystemInfo) {
        this.usersSystemInfo = usersSystemInfo;
    }

    public Collection<UserSystemInfo> getUsersSystemInfo() {
        return usersSystemInfo;
    }
}
