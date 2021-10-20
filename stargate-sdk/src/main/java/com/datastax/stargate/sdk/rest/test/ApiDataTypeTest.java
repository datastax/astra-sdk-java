package com.datastax.stargate.sdk.rest.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.rest.KeyspaceClient;
import com.datastax.stargate.sdk.rest.TableClient;
import com.datastax.stargate.sdk.rest.TypeClient;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.CreateType;
import com.datastax.stargate.sdk.rest.domain.TypeFieldDefinition;
import com.datastax.stargate.sdk.rest.domain.TypeFieldUpdate;
import com.datastax.stargate.sdk.rest.domain.UpdateType;

/**
 * Test resources related to DATA.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDataTypeTest implements ApiDataTest {
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    /** Tested Store. */
    protected static KeyspaceClient ksClient;
    
    @Test
    @Order(1)
    @DisplayName("01-should-create-a-udt")
    public void should_create_udt() {
        TypeClient address = ksClient.type(TEST_UDT);
        Assertions.assertFalse(address.exist());
        CreateType ct = new CreateType(TEST_UDT, true);
        ct.getFields().add(new TypeFieldDefinition("city", "text"));
        ct.getFields().add(new TypeFieldDefinition("zipcode", "int"));
        ct.getFields().add(new TypeFieldDefinition("street", "text"));
        ct.getFields().add(new TypeFieldDefinition("phone", "list<text>"));
        address.create(ct);
        Assertions.assertTrue(address.exist());
    }
    
    @Test
    @Order(2)
    @DisplayName("01-should-update-a-udt")
    public void should_update_udt() {
        // Given
        TypeClient address = ksClient.type(TEST_UDT);
        Assertions.assertTrue(address.exist());
        List<String> fields = address.find().get().getFields().stream()
               .map(TypeFieldDefinition::getName).collect(Collectors.toList());
        Assertions.assertFalse(fields.contains("country"));
        Assertions.assertFalse(fields.contains("town"));
        Assertions.assertTrue(fields.contains("city"));
        // When
        UpdateType ut= new UpdateType();
        ut.getAddFields().add(new TypeFieldDefinition("country","text" ));
        ut.getRenameFields().add(new TypeFieldUpdate("city", "town"));
        address.update(ut);
        
        // Then
        fields = address.find().get().getFields().stream()
                .map(TypeFieldDefinition::getName).collect(Collectors.toList());
         Assertions.assertTrue(fields.contains("country"));
         Assertions.assertTrue(fields.contains("town"));
         Assertions.assertFalse(fields.contains("city"));
    }
    
    @Test
    @Order(3)
    @DisplayName("03-should-delete-a-udt")
    public void should_delete_udt() {
        TypeClient address = ksClient.type(TEST_UDT);
        Assertions.assertTrue(address.exist());
        address.delete();
        Assertions.assertFalse(address.exist());
    }
    
    @Test
    @Order(4)
    @DisplayName("04-Full-flege test with udt")
    public void shoud_use_udt() {
        // (1) Create Address
        TypeClient typeAddress = ksClient.type(TEST_UDT);
        CreateType ct = new CreateType(TEST_UDT, true);
        ct.getFields().add(new TypeFieldDefinition("city", "text"));
        ct.getFields().add(new TypeFieldDefinition("zipcode", "int"));
        ct.getFields().add(new TypeFieldDefinition("street", "text"));
        typeAddress.create(ct);
        Assertions.assertTrue(typeAddress.exist());
        
        // (2) Create table person
        TableClient tablePerson = ksClient.table(TEST_TABLE_UDT);
        CreateTable tcr = new CreateTable();
        tcr.setName("person");
        tcr.setIfNotExists(true);
        tcr.getColumnDefinitions().add(new ColumnDefinition("email", "text"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("firsname", "text"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("lastname", "int"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("addr", "frozen<address>"));
        tcr.getPrimaryKey().getPartitionKey().add("email");
        tablePerson.create(tcr);
        Assertions.assertTrue(tablePerson.exist());
        
        // (3) Insert some bean
        Map<String, Object> data = new HashMap<>();
        data.put("email", "cedrick.lunven@datastax.com");
        data.put("firsname", "cedrick");
        data.put("lastname", 123);
        data.put("addr", "{ city:'PARIS', zipcode:75000, street: 'Champ' }");
        tablePerson.upsert(data);
        
        // Delete table and UDT
        tablePerson.delete();
        typeAddress.delete();
    }
    
}
