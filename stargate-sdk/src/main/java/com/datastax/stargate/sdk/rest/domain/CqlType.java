/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.rest.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.datastax.oss.driver.api.core.data.CqlDuration;
import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.data.UdtValue;

/**
 * Mapper from Bean to CQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum CqlType {
    Ascii(String.class, "US-ASCII characters"),
    Bigint(Long.class, "64-bit signed integer"),
    Blob(ByteBuffer.class, "Arbitrary bytes"),
    Boolean(Boolean.class, "True or false"),
    Counter(Long.class, "64-bit signed integer"),
    Date(LocalDate.class, "32-bit unsigned integer representing the number of days since epoch"),
    Decimal(BigDecimal.class, "Variable-precision decimal, supports integers and floats"),
    Double(Double.class, "64-bit IEEE-754 floating point"),
    Duration(CqlDuration.class, "128 bit encoded duration with nanosecond precision"),
    Float(Float.class, "32-bit IEEE-754 floating point"),
    Inet(InetAddress.class, "IP address string in IPv4 or IPv6 format"),
    Int(Integer.class, "32-bit signed integer"),
    List(List.class, "A typed list of values"),
    Map(Map.class, "A typed map of key value pairs"),
    Set(Set.class, "A typed set of values"),
    Smallint(Short.class, "16-bit signed integer"),
    Text(String.class, "UTF-8 encoded string"),
    Time(LocalTime.class,"Encoded 64-bit signed integers representing the number of nanoseconds since midnight with no corresponding date value"),
    Timestamp(Instant.class,"64-bit signed integer representing the date and time since epoch (January 1 1970 at 00:00:00 GMT) in milliseconds"),
    Timeuuid(UUID.class,"Version 1 UUID; unique identifier that includes a 'conflict-free' timestamp"),
    Tinyint(Byte.class, "8-bit signed integer"),
    Tuple(TupleValue.class,"Fixed length sequence of elements of different types"),
    Uuid(UUID.class, "128 bit universally unique identifier (UUID)"),
    Varchar(String.class, "UTF-8 encoded string"),
    Varint(BigInteger.class,"Arbitrary-precision integer"),
    UDT(UdtValue.class,"A user defined type that has been set up previously via 'schema.type(<type_name>).property(<prop_name>, <type>)...create()'");
    //Point(0,io.stargate.db.schema.Point.class,"org.apache.cassandra.db.marshal.PointType",true,null,"Contains two coordinate values for latitude and longitude"),
    //Polygon(0,io.stargate.db.schema.Polygon.class,"org.apache.cassandra.db.marshal.PolygonType",true,null,"Contains three or more point values forming a polygon"),
    //LineString(0,io.stargate.db.schema.LineString.class,"org.apache.cassandra.db.marshal.LineStringType",true,null,"Contains two or more point values forming a line"),
    
    private final Class<?> javaType;
    
    private final String description;

    private CqlType(Class<?> javaType, String description) {
        this.javaType = javaType;
        this.description = description;
    }
    
    public Class<?> javaType() {
      return javaType;
    }

    public String description() {
      return description;
    }
    
}