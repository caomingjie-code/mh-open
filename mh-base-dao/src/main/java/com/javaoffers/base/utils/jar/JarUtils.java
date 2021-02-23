package com.javaoffers.base.utils.jar;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * jar 包工具
 */
public class JarUtils {

    public static Set<ClassFormJar> getClassFromJar(String jarName) throws Exception{
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/");
        LinkedHashSet<ClassFormJar> className = new LinkedHashSet<>();
        for(Resource resource : resources){
            String description = resource.getDescription();
            if(description.contains(jarName)){
                URL url = resource.getURL();
                String path = url.getPath();
                if(StringUtils.isNotBlank(path)&&path.contains("file:")&&path.substring(0,5).equalsIgnoreCase("file:")){
                    path = path.substring(5, path.length());
                    if(StringUtils.isNotBlank(path)&&path.contains("!/")&&path.substring(path.length()-2,path.length()).equalsIgnoreCase("!/")){
                        path = path.substring(0,path.length()-2);
                    }
                }
                JarFile jarFile = new JarFile(path);
                Enumeration enu = jarFile.entries();
                while (enu.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) enu.nextElement();
                    String name = jarEntry.getName();
                    File file = new File(name);
                    if (name.endsWith(".class")) {
                        String pkName = name.split(".class")[0].replaceAll("/", ".");
                        className.add(new ClassFormJar(pkName,file)); //收集指定包下面的所有class name
                    }
                }

            }
        }
        return className;
    }

    public static class ClassFormJar{
        private String name;
        private File classFile;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public File getClassFile() {
            return classFile;
        }

        public void setClassFile(File classFile) {
            this.classFile = classFile;
        }

        public ClassFormJar(String name, File classFile) {
            this.name = name;
            this.classFile = classFile;
        }
    }
}
