package org.datastax.astra;

import org.datastax.astra.doc.AstraCollection;
import org.datastax.astra.doc.AstraDocument;

@AstraCollection("person")
public class DocumentPerson extends AstraDocument<Person>{

    /** Serial. */
    private static final long serialVersionUID = 4414964439713051834L;

    public DocumentPerson(Person val, Class<Person> typeParameterClass) {
        super(val, typeParameterClass);
    }

}
