package sh.pancake.launcher.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * ClassLoader that filters resources matching keywords starting with 
 */
public class FilteredClassLoader extends ClassLoader {

    private final Set<String> ignores;

    public FilteredClassLoader(ClassLoader loader) {
        super(loader);

        this.ignores = new HashSet<>();
    }
    
    public Set<String> getIgnores() {
        return ignores;
    }

    private boolean shouldIgnore(String name) {
        for (String keyword : ignores) {
            if (name.startsWith(keyword)) return true;
        }

        return false;
    }

    @Override
    public URL getResource(String name) {
        if (shouldIgnore(name)) return null;

        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (shouldIgnore(name)) Collections.emptyEnumeration();

        return super.getResources(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (shouldIgnore(name)) throw new ClassNotFoundException("Class is filtered and not visible");

        return super.loadClass(name, resolve);
    }
    
}
