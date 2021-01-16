package org.datastax.astra;

import org.datastax.astra.utils.MappingUtils;
import org.junit.jupiter.api.Test;

public class TestMapping {

    
    @Test
    public void testMap() throws ClassNotFoundException {
        
        
        String input = MappingUtils.mapToCollection(Person.class);
        System.out.println(input);
        
        Class<?> c = MappingUtils.mapToClass(input);
        System.out.println(c.getCanonicalName());
        
    }
}
