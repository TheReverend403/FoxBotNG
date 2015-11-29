/*
 * This file is part of FoxBotNG.
 *
 * FoxBotNG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBotNG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.foxdev.foxbotng.api;

import co.foxdev.foxbotng.FoxBotNG;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class PluginManager {
    private static final FileFilter JAR_FILE_FILTER = pathname -> pathname.getAbsolutePath().endsWith(".jar");
    @Getter
    public Map<String, Plugin> plugins;

    public PluginManager() throws IOException {
        loadClasses();
    }

    private static void loadFile(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }

    public HashSet<String> getClasses(File file) {
        HashSet<String> found = new HashSet<>();
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file.getAbsoluteFile()));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String name = entry.getName().replace('/', '.');
                    found.add(name.substring(0, name.lastIndexOf(".")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return found;
    }

    public void loadClasses() throws IOException {
        FoxBotNG bot = FoxBotNG.getInstance();
        File pluginsDir = new File(bot.getConfigManager().getConfigDir(), "plugins");
        if (!pluginsDir.exists() && !pluginsDir.mkdirs()) {
            throw new IOException("Could not create plugin directory.");
        }

        HashMap<File, HashSet<String>> jarData = new HashMap<>();
        for (File file : pluginsDir.listFiles(JAR_FILE_FILTER)) {
            log.debug("Scanning jar {} for classes.", file.getName());
            HashSet<String> classes = getClasses(file);
            jarData.put(file, classes);
            log.debug("Found {} class files in jar.", classes.size());
            try {
                loadFile(file);
            } catch (Exception e) {
                log.error("Error loading {}", file.getAbsolutePath());
            }
        }

        for (Map.Entry<File, HashSet<String>> entry : jarData.entrySet()) {
            HashSet<String> classNames = entry.getValue();
            for (String className : classNames) {
                Class c;
                try {
                    c = this.getClass().getClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (c.isAnnotationPresent(Plugin.class)) {
                    log.debug("Main class: {}", c.getName());
                    Object plugin;
                    try {
                        plugin = c.newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        log.error("Error while loading class {}, not loading plugin.", c.getName(), ex);
                        continue;
                    }
                    Plugin pl = plugin.getClass().getAnnotation(Plugin.class);
                    log.info("Loaded plugin {}", pl.name());
                }
            }
        }
    }

}
