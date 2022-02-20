package com.cdprete.phonebook.security;

import com.cdprete.phonebook.api.security.UserInfo;
import com.cdprete.phonebook.api.security.UserInfoAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.lang.ThreadLocal.withInitial;
import static java.util.Optional.ofNullable;

/**
 * @author Cosimo Damiano Prete
 * @since 11/02/2022
 */
@Component
public class SecurityContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(SecurityContextHolder.class);

    private static final ThreadLocal<UserInfo> userInfo = withInitial(UserInfoAdapter::getAnonymousUserInfoInstance);

    public static UserInfo getUserInfo() {
        return userInfo.get();
    }

    public void runWithSecurityContext(UserInfo userInfo, CheckedRunnable exec) throws Exception {
        var oldUserInfo = getUserInfo();
        try {
            if(userInfo == null) {
                logger.debug("The specified {} is null, therefore the execution will be executed as anonymous user.", UserInfo.class.getSimpleName());
            }
            setUserInfo(userInfo);
            exec.run();
        } finally {
            setUserInfo(oldUserInfo);
        }
    }

    private static void setUserInfo(UserInfo aUserInfo) {
        userInfo.set(ofNullable(aUserInfo).orElseGet(UserInfoAdapter::getAnonymousUserInfoInstance));
    }

    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Exception;
    }
}
