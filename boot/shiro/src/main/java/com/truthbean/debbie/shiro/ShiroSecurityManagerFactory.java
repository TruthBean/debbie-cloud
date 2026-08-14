/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.shiro;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class ShiroSecurityManagerFactory {

    private final ShiroConfiguration configuration;
    private volatile DefaultSecurityManager securityManager;

    public ShiroSecurityManagerFactory(ShiroConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get or create the default SecurityManager.
     */
    public DefaultSecurityManager getSecurityManager() {
        if (securityManager == null) {
            synchronized (this) {
                if (securityManager == null) {
                    securityManager = new DefaultSecurityManager();

                    // Configure session manager
                    DefaultSessionManager sessionManager = new DefaultSessionManager();
                    sessionManager.setGlobalSessionTimeout(configuration.getSessionTimeout());
                    securityManager.setSessionManager(sessionManager);

                    // Configure realm from INI config
                    String iniPath = configuration.getIniConfigPath();
                    if (iniPath.startsWith("classpath:")) {
                        IniRealm iniRealm = new IniRealm(iniPath.substring("classpath:".length()));
                        securityManager.setRealm(iniRealm);
                    } else {
                        IniRealm iniRealm = new IniRealm(iniPath);
                        securityManager.setRealm(iniRealm);
                    }

                    // Set as VM singleton
                    SecurityUtils.setSecurityManager(securityManager);
                    LOGGER.info("Shiro SecurityManager initialized");
                }
            }
        }
        return securityManager;
    }

    public ShiroConfiguration getConfiguration() {
        return configuration;
    }

    public void close() {
        if (securityManager != null) {
            securityManager.destroy();
            LOGGER.info("Shiro SecurityManager destroyed");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroSecurityManagerFactory.class);
}