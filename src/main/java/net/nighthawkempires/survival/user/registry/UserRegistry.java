package net.nighthawkempires.survival.user.registry;

import com.google.common.collect.ImmutableList;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.survival.user.UserModel;

import java.util.Map;
import java.util.UUID;

public interface UserRegistry extends Registry<UserModel> {

    String NAME = "users";

    default UserModel fromDataSection(String stringKey, DataSection data) {
        return new UserModel(stringKey, data);
    }

    default UserModel getUser(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return fromKey(uuid.toString()).orElseGet(() -> register(new UserModel(uuid)));
    }

    @Deprecated
    Map<String, UserModel> getRegisteredData();

    default ImmutableList<UserModel> getUsers() {
        return ImmutableList.copyOf(loadAllFromDb().values());
    }

    default boolean userExists(UUID uuid) {
        return fromKey(uuid.toString()).isPresent();
    }
}
