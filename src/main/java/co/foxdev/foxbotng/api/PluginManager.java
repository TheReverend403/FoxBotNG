package co.foxdev.foxbotng.api;

import co.foxdev.foxbotng.FoxBotNG;
import lombok.extern.slf4j.Slf4j;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class PluginManager {
    public Map<String, Plugin> plugins;

    public void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        loadClasses();
    }

    private static void loadFile(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
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

    public void loadClasses() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        FoxBotNG bot = FoxBotNG.getInstance();
        File pluginsdir = new File(bot.getConfigManager().getConfigDir(), "plugins/");
        if (!pluginsdir.exists()) {
            pluginsdir.mkdirs();
        }

        HashMap<File, HashSet<String>> jardata = new HashMap<>();
        for (File file : pluginsdir.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                log.info("Scanning file " + file.getName() + " for classes!");
                HashSet<String> classes = getClasses(file);
                jardata.put(file, classes);
                log.info("Found " + classes.size() + " classes in file!");
                try {
                    loadFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        for (Map.Entry<File, HashSet<String>> entry : jardata.entrySet()) {
            HashSet<String> classnames = entry.getValue();
            for (String classname : classnames) {
                Class c = this.getClass().getClassLoader().loadClass(classname);

                if (c.isAnnotationPresent(Plugin.class)) {
                    log.info("Main class: " + c.getName() + " found!");
                    Object plugin = c.newInstance();
                    Annotation[] annotations = plugin.getClass().getAnnotations();
                    for(Annotation annotation : annotations){
                       for(Class<?> interfaces : annotation.getClass().getInterfaces()){
                          log.info(interfaces.getName());
                       }
                    }
                    for(Annotation annotation : plugin.getClass().getDeclaredAnnotations()){
                        log.info("Annotated with: - " + annotation.getClass().getName());
                    }
                    Plugin pl = c.getClass().getAnnotation(Plugin.class);
                    //log.info(String.format("Loading plugin %s ", pl.name()));
                }
            }
        }
    }

}
