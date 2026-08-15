/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You can obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.config.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.config.ConfigConfiguration;
import com.truthbean.debbie.config.env.ConfigEnvironment;
import com.truthbean.debbie.config.env.ConfigPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * file system based {@link ConfigRepository}.
 * <p>
 * reads configuration from {@code {base-dir}/{label}/} directory.
 * for an application named {@code order} with profile {@code dev},
 * it loads (in order, later ones override earlier ones):
 * <ol>
 *   <li>{@code application.properties}</li>
 *   <li>{@code application-{profile}.properties}</li>
 *   <li>{@code {application}.properties}</li>
 *   <li>{@code {application}-{profile}.properties}</li>
 * </ol>
 * the same precedence applies to {@code .yml} / {@code .yaml} files.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class FileConfigRepository implements ConfigRepository {

    private final ConfigConfiguration configuration;

    public FileConfigRepository(ConfigConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String name() {
        return "file";
    }

    @Override
    public ConfigEnvironment findOne(String application, String profile, String label) {
        if (application == null || application.isBlank()) {
            application = "application";
        }
        if (label == null || label.isBlank()) {
            label = configuration.getServerDefaultLabel();
        }
        if (profile == null || profile.isBlank()) {
            profile = "default";
        }

        var profiles = new ArrayList<String>();
        for (var p : profile.split(",")) {
            if (!p.isBlank()) {
                profiles.add(p.trim());
            }
        }

        var env = new ConfigEnvironment(application, profiles, label);
        var baseDir = Paths.get(configuration.getServerBaseDir(), label).toAbsolutePath();

        if (!Files.isDirectory(baseDir)) {
            LOGGER.warn(() -> "config base dir not found: " + baseDir);
            return env;
        }

        for (var p : profiles) {
            loadPropertySource(env, baseDir, "application", null);
            loadPropertySource(env, baseDir, "application", p);
            loadPropertySource(env, baseDir, application, null);
            loadPropertySource(env, baseDir, application, p);
        }

        var appName = application;
        var appProfile = profile;
        var appLabel = label;
        LOGGER.debug(() -> "loaded " + env.getPropertySources().size() + " property sources for "
                + appName + "/" + appProfile + "/" + appLabel);
        return env;
    }

    private void loadPropertySource(ConfigEnvironment env, Path baseDir,
                                    String app, String profile) {
        for (var ext : List.of("properties", "yml", "yaml")) {
            var fileName = buildFileName(app, profile, ext);
            var filePath = baseDir.resolve(fileName);
            if (Files.isRegularFile(filePath)) {
                try {
                    var props = loadFile(filePath, ext);
                    if (!props.isEmpty()) {
                        var sourceName = fileName;
                        env.addPropertySource(new ConfigPropertySource(sourceName, props));
                        LOGGER.trace(() -> "loaded " + props.size() + " properties from " + filePath);
                    }
                } catch (Exception e) {
                    LOGGER.error("failed to load " + filePath, e);
                }
            }
        }
    }

    private String buildFileName(String app, String profile, String ext) {
        if (profile == null || profile.isBlank()) {
            return app + "." + ext;
        }
        return app + "-" + profile + "." + ext;
    }

    private Map<String, String> loadFile(Path filePath, String ext) throws IOException {
        var result = new LinkedHashMap<String, String>();
        if ("properties".equals(ext)) {
            var props = new Properties();
            try (var in = Files.newInputStream(filePath)) {
                props.load(in);
            }
            for (var e : props.entrySet()) {
                result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
        } else {
            var props = new Properties();
            try (var in = Files.newInputStream(filePath)) {
                props.load(in);
            }
            for (var e : props.entrySet()) {
                result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
        }
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileConfigRepository.class);
}