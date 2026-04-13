package jax.rs_coursework;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")                                 // Defines the root path for all RESTful resources within the application. With this, all resource URIs will be prefixed with /api/v1 (Week 7 Tutorial)
public class MyApplication extends Application {            // The core configuration class of the REST application. Extends javax.ws.rs.core.Application, providing a place to manage the registration of resources and other configuration aspects.

    @Override
    public Set<Class<?>> getClasses() {                     // getClasses() is a crucial part of configuring a JAX-RS application. By overriding it, you specify the REST resource classes the JAX-RS runtime should manage.
        Set<Class<?>> classes = new HashSet<>();            // Creates a HashSet to store references to the resource classes. HashSets ensure uniqueness and efficient lookups.
        classes.add(DiscoveryResource.class);             // Adds the DiscoveryResource class to the set. Implies that this class contains the REST endpoint definitions.
        classes.add(SensorRoom.class);                    
        classes.add(SensorResource.class);                // SensorReadingResource is included with this too.
        classes.add(RoomNotEmptyExceptionMapper.class);   
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(ThrowableExceptionMapper.class);
        classes.add(ApiLoggingFilter.class);
        
        return classes;                                     // Returns the set of registered resources classes.
    }
}