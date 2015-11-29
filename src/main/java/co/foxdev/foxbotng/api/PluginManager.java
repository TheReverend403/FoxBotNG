package co.foxdev.foxbotng.api;

import co.foxdev.foxbotng.FoxBotNG;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by zack6849 on 11/29/2015.
 */
public class PluginManager {

    public Map<String, Plugin> plugins;

    public HashSet<String> getClasses(JarFile jar){
        HashSet<String> found = new HashSet<>();
        Enumeration<JarEntry> classes = jar.entries();
        while(classes.hasMoreElements()){
            JarEntry entry = classes.nextElement();
            if(entry.getName().endsWith(".class")){
                String name = jar.getName().replace("/", ".");
                String clasname = name.substring(0, name.lastIndexOf("."));
                found.add(clasname);
            }
        }
        return found;
    }

    public void loadClasses(HashSet<String> classes) throws IOException, ClassNotFoundException {
        FoxBotNG bot = FoxBotNG.getInstance();
        File pluginsdir = new File(bot.getConfigManager().getConfigDir(), "plugins/");
        if(!pluginsdir.exists()){
            pluginsdir.mkdirs();
        }
        HashSet<URL> urls = new HashSet<>();
        HashSet<JarFile> jarfiles = new HashSet<>();
        for(File file : pluginsdir.listFiles()){
            if(file.getName().endsWith(".jar")){
                jarfiles.add(new JarFile(file));
                urls.add(new URL("jar:file:" + file.getAbsolutePath() + "!/"));
            }
        }
        URL[] urlsa = new URL[urls.size()];
        urls.addAll(urls);
        URLClassLoader loader = URLClassLoader.newInstance(urlsa);
        for(JarFile file : jarfiles){
            HashSet<String> jarclasses = getClasses(file);
            for(String jarclass : jarclasses){
                Class c = loader.loadClass(jarclass);
                if(c.isAnnotationPresent(Plugin.class)){
                    //found our main!!!
                }
            }
        }
    }

}
