/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.jskno.avro.generated;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class CoffeeOrderComplexIdV3 extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -8742126234853854957L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CoffeeOrderComplexIdV3\",\"namespace\":\"com.jskno.avro.generated\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"record\",\"name\":\"OrderId\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"}]}},{\"name\":\"UUID\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"}},{\"name\":\"fullName\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"nickname\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"Optional field represents the nickname of the customer\",\"default\":\"\"},{\"name\":\"additionalNickname\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"Optional field represents the nickname of the customer\",\"default\":\"\"},{\"name\":\"store\",\"type\":{\"type\":\"record\",\"name\":\"Store\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"address\",\"type\":{\"type\":\"record\",\"name\":\"Address\",\"fields\":[{\"name\":\"addressLine1\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"city\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"stateProvince\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"country\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"zip\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}}]}},{\"name\":\"orderLineItems\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"OrderLineItem\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"size\",\"type\":{\"type\":\"enum\",\"name\":\"Size\",\"symbols\":[\"SMALL\",\"MEDIUM\",\"LARGE\"]}},{\"name\":\"quantity\",\"type\":\"int\"},{\"name\":\"cost\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":3,\"scale\":2}}]}}},{\"name\":\"pickUp\",\"type\":{\"type\":\"enum\",\"name\":\"PickUp\",\"symbols\":[\"IN_STORE\",\"CURBSIDE\"]}},{\"name\":\"orderTime\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"orderDate\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"status\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":\"NEW\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();
static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.UUIDConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.DecimalConversion());
  }

  private static final BinaryMessageEncoder<CoffeeOrderComplexIdV3> ENCODER =
      new BinaryMessageEncoder<CoffeeOrderComplexIdV3>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<CoffeeOrderComplexIdV3> DECODER =
      new BinaryMessageDecoder<CoffeeOrderComplexIdV3>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<CoffeeOrderComplexIdV3> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<CoffeeOrderComplexIdV3> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<CoffeeOrderComplexIdV3> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<CoffeeOrderComplexIdV3>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this CoffeeOrderComplexIdV3 to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a CoffeeOrderComplexIdV3 from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a CoffeeOrderComplexIdV3 instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static CoffeeOrderComplexIdV3 fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public com.jskno.avro.generated.OrderId id;
  @Deprecated public java.util.UUID UUID;
  @Deprecated public java.lang.String fullName;
  /** Optional field represents the nickname of the customer */
  @Deprecated public java.lang.String nickname;
  /** Optional field represents the nickname of the customer */
  @Deprecated public java.lang.String additionalNickname;
  @Deprecated public com.jskno.avro.generated.Store store;
  @Deprecated public java.util.List<com.jskno.avro.generated.OrderLineItem> orderLineItems;
  @Deprecated public com.jskno.avro.generated.PickUp pickUp;
  @Deprecated public java.time.Instant orderTime;
  @Deprecated public java.time.LocalDate orderDate;
  @Deprecated public java.lang.String status;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public CoffeeOrderComplexIdV3() {}

  /**
   * All-args constructor.
   * @param id The new value for id
   * @param UUID The new value for UUID
   * @param fullName The new value for fullName
   * @param nickname Optional field represents the nickname of the customer
   * @param additionalNickname Optional field represents the nickname of the customer
   * @param store The new value for store
   * @param orderLineItems The new value for orderLineItems
   * @param pickUp The new value for pickUp
   * @param orderTime The new value for orderTime
   * @param orderDate The new value for orderDate
   * @param status The new value for status
   */
  public CoffeeOrderComplexIdV3(com.jskno.avro.generated.OrderId id, java.util.UUID UUID, java.lang.String fullName, java.lang.String nickname, java.lang.String additionalNickname, com.jskno.avro.generated.Store store, java.util.List<com.jskno.avro.generated.OrderLineItem> orderLineItems, com.jskno.avro.generated.PickUp pickUp, java.time.Instant orderTime, java.time.LocalDate orderDate, java.lang.String status) {
    this.id = id;
    this.UUID = UUID;
    this.fullName = fullName;
    this.nickname = nickname;
    this.additionalNickname = additionalNickname;
    this.store = store;
    this.orderLineItems = orderLineItems;
    this.pickUp = pickUp;
    this.orderTime = orderTime.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
    this.orderDate = orderDate;
    this.status = status;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return UUID;
    case 2: return fullName;
    case 3: return nickname;
    case 4: return additionalNickname;
    case 5: return store;
    case 6: return orderLineItems;
    case 7: return pickUp;
    case 8: return orderTime;
    case 9: return orderDate;
    case 10: return status;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  private static final org.apache.avro.Conversion<?>[] conversions =
      new org.apache.avro.Conversion<?>[] {
      null,
      new org.apache.avro.Conversions.UUIDConversion(),
      null,
      null,
      null,
      null,
      null,
      null,
      new org.apache.avro.data.TimeConversions.TimestampMillisConversion(),
      new org.apache.avro.data.TimeConversions.DateConversion(),
      null,
      null
  };

  @Override
  public org.apache.avro.Conversion<?> getConversion(int field) {
    return conversions[field];
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (com.jskno.avro.generated.OrderId)value$; break;
    case 1: UUID = (java.util.UUID)value$; break;
    case 2: fullName = value$ != null ? value$.toString() : null; break;
    case 3: nickname = value$ != null ? value$.toString() : null; break;
    case 4: additionalNickname = value$ != null ? value$.toString() : null; break;
    case 5: store = (com.jskno.avro.generated.Store)value$; break;
    case 6: orderLineItems = (java.util.List<com.jskno.avro.generated.OrderLineItem>)value$; break;
    case 7: pickUp = (com.jskno.avro.generated.PickUp)value$; break;
    case 8: orderTime = (java.time.Instant)value$; break;
    case 9: orderDate = (java.time.LocalDate)value$; break;
    case 10: status = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public com.jskno.avro.generated.OrderId getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(com.jskno.avro.generated.OrderId value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'UUID' field.
   * @return The value of the 'UUID' field.
   */
  public java.util.UUID getUUID() {
    return UUID;
  }


  /**
   * Sets the value of the 'UUID' field.
   * @param value the value to set.
   */
  public void setUUID(java.util.UUID value) {
    this.UUID = value;
  }

  /**
   * Gets the value of the 'fullName' field.
   * @return The value of the 'fullName' field.
   */
  public java.lang.String getFullName() {
    return fullName;
  }


  /**
   * Sets the value of the 'fullName' field.
   * @param value the value to set.
   */
  public void setFullName(java.lang.String value) {
    this.fullName = value;
  }

  /**
   * Gets the value of the 'nickname' field.
   * @return Optional field represents the nickname of the customer
   */
  public java.lang.String getNickname() {
    return nickname;
  }


  /**
   * Sets the value of the 'nickname' field.
   * Optional field represents the nickname of the customer
   * @param value the value to set.
   */
  public void setNickname(java.lang.String value) {
    this.nickname = value;
  }

  /**
   * Gets the value of the 'additionalNickname' field.
   * @return Optional field represents the nickname of the customer
   */
  public java.lang.String getAdditionalNickname() {
    return additionalNickname;
  }


  /**
   * Sets the value of the 'additionalNickname' field.
   * Optional field represents the nickname of the customer
   * @param value the value to set.
   */
  public void setAdditionalNickname(java.lang.String value) {
    this.additionalNickname = value;
  }

  /**
   * Gets the value of the 'store' field.
   * @return The value of the 'store' field.
   */
  public com.jskno.avro.generated.Store getStore() {
    return store;
  }


  /**
   * Sets the value of the 'store' field.
   * @param value the value to set.
   */
  public void setStore(com.jskno.avro.generated.Store value) {
    this.store = value;
  }

  /**
   * Gets the value of the 'orderLineItems' field.
   * @return The value of the 'orderLineItems' field.
   */
  public java.util.List<com.jskno.avro.generated.OrderLineItem> getOrderLineItems() {
    return orderLineItems;
  }


  /**
   * Sets the value of the 'orderLineItems' field.
   * @param value the value to set.
   */
  public void setOrderLineItems(java.util.List<com.jskno.avro.generated.OrderLineItem> value) {
    this.orderLineItems = value;
  }

  /**
   * Gets the value of the 'pickUp' field.
   * @return The value of the 'pickUp' field.
   */
  public com.jskno.avro.generated.PickUp getPickUp() {
    return pickUp;
  }


  /**
   * Sets the value of the 'pickUp' field.
   * @param value the value to set.
   */
  public void setPickUp(com.jskno.avro.generated.PickUp value) {
    this.pickUp = value;
  }

  /**
   * Gets the value of the 'orderTime' field.
   * @return The value of the 'orderTime' field.
   */
  public java.time.Instant getOrderTime() {
    return orderTime;
  }


  /**
   * Sets the value of the 'orderTime' field.
   * @param value the value to set.
   */
  public void setOrderTime(java.time.Instant value) {
    this.orderTime = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
  }

  /**
   * Gets the value of the 'orderDate' field.
   * @return The value of the 'orderDate' field.
   */
  public java.time.LocalDate getOrderDate() {
    return orderDate;
  }


  /**
   * Sets the value of the 'orderDate' field.
   * @param value the value to set.
   */
  public void setOrderDate(java.time.LocalDate value) {
    this.orderDate = value;
  }

  /**
   * Gets the value of the 'status' field.
   * @return The value of the 'status' field.
   */
  public java.lang.String getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(java.lang.String value) {
    this.status = value;
  }

  /**
   * Creates a new CoffeeOrderComplexIdV3 RecordBuilder.
   * @return A new CoffeeOrderComplexIdV3 RecordBuilder
   */
  public static com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder newBuilder() {
    return new com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder();
  }

  /**
   * Creates a new CoffeeOrderComplexIdV3 RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new CoffeeOrderComplexIdV3 RecordBuilder
   */
  public static com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder newBuilder(com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder other) {
    if (other == null) {
      return new com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder();
    } else {
      return new com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder(other);
    }
  }

  /**
   * Creates a new CoffeeOrderComplexIdV3 RecordBuilder by copying an existing CoffeeOrderComplexIdV3 instance.
   * @param other The existing instance to copy.
   * @return A new CoffeeOrderComplexIdV3 RecordBuilder
   */
  public static com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder newBuilder(com.jskno.avro.generated.CoffeeOrderComplexIdV3 other) {
    if (other == null) {
      return new com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder();
    } else {
      return new com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder(other);
    }
  }

  /**
   * RecordBuilder for CoffeeOrderComplexIdV3 instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CoffeeOrderComplexIdV3>
    implements org.apache.avro.data.RecordBuilder<CoffeeOrderComplexIdV3> {

    private com.jskno.avro.generated.OrderId id;
    private com.jskno.avro.generated.OrderId.Builder idBuilder;
    private java.util.UUID UUID;
    private java.lang.String fullName;
    /** Optional field represents the nickname of the customer */
    private java.lang.String nickname;
    /** Optional field represents the nickname of the customer */
    private java.lang.String additionalNickname;
    private com.jskno.avro.generated.Store store;
    private com.jskno.avro.generated.Store.Builder storeBuilder;
    private java.util.List<com.jskno.avro.generated.OrderLineItem> orderLineItems;
    private com.jskno.avro.generated.PickUp pickUp;
    private java.time.Instant orderTime;
    private java.time.LocalDate orderDate;
    private java.lang.String status;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (other.hasIdBuilder()) {
        this.idBuilder = com.jskno.avro.generated.OrderId.newBuilder(other.getIdBuilder());
      }
      if (isValidValue(fields()[1], other.UUID)) {
        this.UUID = data().deepCopy(fields()[1].schema(), other.UUID);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.fullName)) {
        this.fullName = data().deepCopy(fields()[2].schema(), other.fullName);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.nickname)) {
        this.nickname = data().deepCopy(fields()[3].schema(), other.nickname);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.additionalNickname)) {
        this.additionalNickname = data().deepCopy(fields()[4].schema(), other.additionalNickname);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.store)) {
        this.store = data().deepCopy(fields()[5].schema(), other.store);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (other.hasStoreBuilder()) {
        this.storeBuilder = com.jskno.avro.generated.Store.newBuilder(other.getStoreBuilder());
      }
      if (isValidValue(fields()[6], other.orderLineItems)) {
        this.orderLineItems = data().deepCopy(fields()[6].schema(), other.orderLineItems);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.pickUp)) {
        this.pickUp = data().deepCopy(fields()[7].schema(), other.pickUp);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.orderTime)) {
        this.orderTime = data().deepCopy(fields()[8].schema(), other.orderTime);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
      if (isValidValue(fields()[9], other.orderDate)) {
        this.orderDate = data().deepCopy(fields()[9].schema(), other.orderDate);
        fieldSetFlags()[9] = other.fieldSetFlags()[9];
      }
      if (isValidValue(fields()[10], other.status)) {
        this.status = data().deepCopy(fields()[10].schema(), other.status);
        fieldSetFlags()[10] = other.fieldSetFlags()[10];
      }
    }

    /**
     * Creates a Builder by copying an existing CoffeeOrderComplexIdV3 instance
     * @param other The existing instance to copy.
     */
    private Builder(com.jskno.avro.generated.CoffeeOrderComplexIdV3 other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      this.idBuilder = null;
      if (isValidValue(fields()[1], other.UUID)) {
        this.UUID = data().deepCopy(fields()[1].schema(), other.UUID);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.fullName)) {
        this.fullName = data().deepCopy(fields()[2].schema(), other.fullName);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.nickname)) {
        this.nickname = data().deepCopy(fields()[3].schema(), other.nickname);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.additionalNickname)) {
        this.additionalNickname = data().deepCopy(fields()[4].schema(), other.additionalNickname);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.store)) {
        this.store = data().deepCopy(fields()[5].schema(), other.store);
        fieldSetFlags()[5] = true;
      }
      this.storeBuilder = null;
      if (isValidValue(fields()[6], other.orderLineItems)) {
        this.orderLineItems = data().deepCopy(fields()[6].schema(), other.orderLineItems);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.pickUp)) {
        this.pickUp = data().deepCopy(fields()[7].schema(), other.pickUp);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.orderTime)) {
        this.orderTime = data().deepCopy(fields()[8].schema(), other.orderTime);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.orderDate)) {
        this.orderDate = data().deepCopy(fields()[9].schema(), other.orderDate);
        fieldSetFlags()[9] = true;
      }
      if (isValidValue(fields()[10], other.status)) {
        this.status = data().deepCopy(fields()[10].schema(), other.status);
        fieldSetFlags()[10] = true;
      }
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public com.jskno.avro.generated.OrderId getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setId(com.jskno.avro.generated.OrderId value) {
      validate(fields()[0], value);
      this.idBuilder = null;
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }

    /**
     * Gets the Builder instance for the 'id' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public com.jskno.avro.generated.OrderId.Builder getIdBuilder() {
      if (idBuilder == null) {
        if (hasId()) {
          setIdBuilder(com.jskno.avro.generated.OrderId.newBuilder(id));
        } else {
          setIdBuilder(com.jskno.avro.generated.OrderId.newBuilder());
        }
      }
      return idBuilder;
    }

    /**
     * Sets the Builder instance for the 'id' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setIdBuilder(com.jskno.avro.generated.OrderId.Builder value) {
      clearId();
      idBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'id' field has an active Builder instance
     * @return True if the 'id' field has an active Builder instance
     */
    public boolean hasIdBuilder() {
      return idBuilder != null;
    }

    /**
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearId() {
      id = null;
      idBuilder = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'UUID' field.
      * @return The value.
      */
    public java.util.UUID getUUID() {
      return UUID;
    }


    /**
      * Sets the value of the 'UUID' field.
      * @param value The value of 'UUID'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setUUID(java.util.UUID value) {
      validate(fields()[1], value);
      this.UUID = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'UUID' field has been set.
      * @return True if the 'UUID' field has been set, false otherwise.
      */
    public boolean hasUUID() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'UUID' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearUUID() {
      UUID = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'fullName' field.
      * @return The value.
      */
    public java.lang.String getFullName() {
      return fullName;
    }


    /**
      * Sets the value of the 'fullName' field.
      * @param value The value of 'fullName'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setFullName(java.lang.String value) {
      validate(fields()[2], value);
      this.fullName = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'fullName' field has been set.
      * @return True if the 'fullName' field has been set, false otherwise.
      */
    public boolean hasFullName() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'fullName' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearFullName() {
      fullName = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'nickname' field.
      * Optional field represents the nickname of the customer
      * @return The value.
      */
    public java.lang.String getNickname() {
      return nickname;
    }


    /**
      * Sets the value of the 'nickname' field.
      * Optional field represents the nickname of the customer
      * @param value The value of 'nickname'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setNickname(java.lang.String value) {
      validate(fields()[3], value);
      this.nickname = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'nickname' field has been set.
      * Optional field represents the nickname of the customer
      * @return True if the 'nickname' field has been set, false otherwise.
      */
    public boolean hasNickname() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'nickname' field.
      * Optional field represents the nickname of the customer
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearNickname() {
      nickname = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'additionalNickname' field.
      * Optional field represents the nickname of the customer
      * @return The value.
      */
    public java.lang.String getAdditionalNickname() {
      return additionalNickname;
    }


    /**
      * Sets the value of the 'additionalNickname' field.
      * Optional field represents the nickname of the customer
      * @param value The value of 'additionalNickname'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setAdditionalNickname(java.lang.String value) {
      validate(fields()[4], value);
      this.additionalNickname = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'additionalNickname' field has been set.
      * Optional field represents the nickname of the customer
      * @return True if the 'additionalNickname' field has been set, false otherwise.
      */
    public boolean hasAdditionalNickname() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'additionalNickname' field.
      * Optional field represents the nickname of the customer
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearAdditionalNickname() {
      additionalNickname = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'store' field.
      * @return The value.
      */
    public com.jskno.avro.generated.Store getStore() {
      return store;
    }


    /**
      * Sets the value of the 'store' field.
      * @param value The value of 'store'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setStore(com.jskno.avro.generated.Store value) {
      validate(fields()[5], value);
      this.storeBuilder = null;
      this.store = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'store' field has been set.
      * @return True if the 'store' field has been set, false otherwise.
      */
    public boolean hasStore() {
      return fieldSetFlags()[5];
    }

    /**
     * Gets the Builder instance for the 'store' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public com.jskno.avro.generated.Store.Builder getStoreBuilder() {
      if (storeBuilder == null) {
        if (hasStore()) {
          setStoreBuilder(com.jskno.avro.generated.Store.newBuilder(store));
        } else {
          setStoreBuilder(com.jskno.avro.generated.Store.newBuilder());
        }
      }
      return storeBuilder;
    }

    /**
     * Sets the Builder instance for the 'store' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setStoreBuilder(com.jskno.avro.generated.Store.Builder value) {
      clearStore();
      storeBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'store' field has an active Builder instance
     * @return True if the 'store' field has an active Builder instance
     */
    public boolean hasStoreBuilder() {
      return storeBuilder != null;
    }

    /**
      * Clears the value of the 'store' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearStore() {
      store = null;
      storeBuilder = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'orderLineItems' field.
      * @return The value.
      */
    public java.util.List<com.jskno.avro.generated.OrderLineItem> getOrderLineItems() {
      return orderLineItems;
    }


    /**
      * Sets the value of the 'orderLineItems' field.
      * @param value The value of 'orderLineItems'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setOrderLineItems(java.util.List<com.jskno.avro.generated.OrderLineItem> value) {
      validate(fields()[6], value);
      this.orderLineItems = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'orderLineItems' field has been set.
      * @return True if the 'orderLineItems' field has been set, false otherwise.
      */
    public boolean hasOrderLineItems() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'orderLineItems' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearOrderLineItems() {
      orderLineItems = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'pickUp' field.
      * @return The value.
      */
    public com.jskno.avro.generated.PickUp getPickUp() {
      return pickUp;
    }


    /**
      * Sets the value of the 'pickUp' field.
      * @param value The value of 'pickUp'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setPickUp(com.jskno.avro.generated.PickUp value) {
      validate(fields()[7], value);
      this.pickUp = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'pickUp' field has been set.
      * @return True if the 'pickUp' field has been set, false otherwise.
      */
    public boolean hasPickUp() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'pickUp' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearPickUp() {
      pickUp = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'orderTime' field.
      * @return The value.
      */
    public java.time.Instant getOrderTime() {
      return orderTime;
    }


    /**
      * Sets the value of the 'orderTime' field.
      * @param value The value of 'orderTime'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setOrderTime(java.time.Instant value) {
      validate(fields()[8], value);
      this.orderTime = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'orderTime' field has been set.
      * @return True if the 'orderTime' field has been set, false otherwise.
      */
    public boolean hasOrderTime() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'orderTime' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearOrderTime() {
      fieldSetFlags()[8] = false;
      return this;
    }

    /**
      * Gets the value of the 'orderDate' field.
      * @return The value.
      */
    public java.time.LocalDate getOrderDate() {
      return orderDate;
    }


    /**
      * Sets the value of the 'orderDate' field.
      * @param value The value of 'orderDate'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setOrderDate(java.time.LocalDate value) {
      validate(fields()[9], value);
      this.orderDate = value;
      fieldSetFlags()[9] = true;
      return this;
    }

    /**
      * Checks whether the 'orderDate' field has been set.
      * @return True if the 'orderDate' field has been set, false otherwise.
      */
    public boolean hasOrderDate() {
      return fieldSetFlags()[9];
    }


    /**
      * Clears the value of the 'orderDate' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearOrderDate() {
      fieldSetFlags()[9] = false;
      return this;
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public java.lang.String getStatus() {
      return status;
    }


    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder setStatus(java.lang.String value) {
      validate(fields()[10], value);
      this.status = value;
      fieldSetFlags()[10] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[10];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrderComplexIdV3.Builder clearStatus() {
      status = null;
      fieldSetFlags()[10] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CoffeeOrderComplexIdV3 build() {
      try {
        CoffeeOrderComplexIdV3 record = new CoffeeOrderComplexIdV3();
        if (idBuilder != null) {
          try {
            record.id = this.idBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("id"));
            throw e;
          }
        } else {
          record.id = fieldSetFlags()[0] ? this.id : (com.jskno.avro.generated.OrderId) defaultValue(fields()[0]);
        }
        record.UUID = fieldSetFlags()[1] ? this.UUID : (java.util.UUID) defaultValue(fields()[1]);
        record.fullName = fieldSetFlags()[2] ? this.fullName : (java.lang.String) defaultValue(fields()[2]);
        record.nickname = fieldSetFlags()[3] ? this.nickname : (java.lang.String) defaultValue(fields()[3]);
        record.additionalNickname = fieldSetFlags()[4] ? this.additionalNickname : (java.lang.String) defaultValue(fields()[4]);
        if (storeBuilder != null) {
          try {
            record.store = this.storeBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("store"));
            throw e;
          }
        } else {
          record.store = fieldSetFlags()[5] ? this.store : (com.jskno.avro.generated.Store) defaultValue(fields()[5]);
        }
        record.orderLineItems = fieldSetFlags()[6] ? this.orderLineItems : (java.util.List<com.jskno.avro.generated.OrderLineItem>) defaultValue(fields()[6]);
        record.pickUp = fieldSetFlags()[7] ? this.pickUp : (com.jskno.avro.generated.PickUp) defaultValue(fields()[7]);
        record.orderTime = fieldSetFlags()[8] ? this.orderTime : (java.time.Instant) defaultValue(fields()[8]);
        record.orderDate = fieldSetFlags()[9] ? this.orderDate : (java.time.LocalDate) defaultValue(fields()[9]);
        record.status = fieldSetFlags()[10] ? this.status : (java.lang.String) defaultValue(fields()[10]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<CoffeeOrderComplexIdV3>
    WRITER$ = (org.apache.avro.io.DatumWriter<CoffeeOrderComplexIdV3>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<CoffeeOrderComplexIdV3>
    READER$ = (org.apache.avro.io.DatumReader<CoffeeOrderComplexIdV3>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










