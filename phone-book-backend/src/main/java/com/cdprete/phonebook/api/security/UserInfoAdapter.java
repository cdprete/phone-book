package com.cdprete.phonebook.api.security;

/**
 * @author Cosimo Damiano Prete
 * @since 11/02/2022
 */
public abstract class UserInfoAdapter implements UserInfo {
    public static UserInfoAdapter getAnonymousUserInfoInstance() {
        return AnonymousUserInfo.instance;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public final boolean isAnonymous() {
        return this instanceof AnonymousUserInfo;
    }

    private static class AnonymousUserInfo extends UserInfoAdapter {
        private static final AnonymousUserInfo instance = new AnonymousUserInfo();

        private AnonymousUserInfo() {}

        @Override
        public String getUsername() {
            return null;
        }
    }
}
