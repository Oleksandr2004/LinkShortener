package com.example.rest;

import com.example.rest.resources.MyResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * RestApplication class.
 *
 */
@ApplicationPath("api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>(1);
        classes.add(MyResource.class);
        return classes;
    }

}
