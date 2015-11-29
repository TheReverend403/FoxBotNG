package co.foxdev.foxbotng.api;

import co.foxdev.foxbotng.FoxBotNG;
import co.foxdev.foxbotng.utils.JarClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.io.File;
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

/**
 * Created by zack6849 on 11/29/2015.
 */
@Slf4j
public class PluginManager {

    static JarClassLoader classLoader = new JarClassLoader(new URL[] {});

    public PluginManager(){
        loadClasses();
    }

    public Map<String, Plugin> plugins;

    public HashSet<String> getClasses(File file){
        HashSet<String> found = new HashSet<>();
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file.getAbsoluteFile()));
            for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()){
                if(!entry.isDirectory() && entry.getName().endsWith(".class")){
                    String name = entry.getName().replace('/', '.');
                    found.add(name.substring(0, name.lastIndexOf(".")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return found;
    }

    public void loadClasses() {
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
        for(Map.Entry<File, HashSet<String>> entry : jardata.entrySet()){

                HashSet<String> classnames = entry.getValue();
                for(String classname : classnames){
                    Class c = null;
                    try {
                        c = this.getClass().getClassLoader().loadClass(classname);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(c.isAnnotationPresent(Plugin.class)){
                        log.info("Found main class " + c.getName() + " for jar " + entry.getKey().getName());
                    }
                }

        }
    }

    private static void loadFile(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }


    public static JarClassLoader getClassLoader(){
        return classLoader;
    }
}
