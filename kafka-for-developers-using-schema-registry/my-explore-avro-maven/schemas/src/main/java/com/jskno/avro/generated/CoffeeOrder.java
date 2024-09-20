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
public class CoffeeOrder extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -8128335015421143336L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CoffeeOrder\",\"namespace\":\"com.jskno.avro.generated\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"},{\"name\":\"UUID\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"}},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"nickname\",\"type\":\"string\",\"doc\":\"Optional field represents the nickname of the customer\",\"default\":\"\"},{\"name\":\"store\",\"type\":{\"type\":\"record\",\"name\":\"Store\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"address\",\"type\":{\"type\":\"record\",\"name\":\"Address\",\"fields\":[{\"name\":\"addressLine1\",\"type\":\"string\"},{\"name\":\"city\",\"type\":\"string\"},{\"name\":\"stateProvince\",\"type\":\"string\"},{\"name\":\"country\",\"type\":\"string\"},{\"name\":\"zip\",\"type\":\"string\"}]}}]}},{\"name\":\"orderLineItems\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"OrderLineItem\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"size\",\"type\":{\"type\":\"enum\",\"name\":\"Size\",\"symbols\":[\"SMALL\",\"MEDIUM\",\"LARGE\"]}},{\"name\":\"quantity\",\"type\":\"int\"},{\"name\":\"cost\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":3,\"scale\":2}}]}}},{\"name\":\"orderTime\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"orderDate\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"status\",\"type\":\"string\",\"default\":\"NEW\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();
static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.UUIDConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.DecimalConversion());
  }

  private static final BinaryMessageEncoder<CoffeeOrder> ENCODER =
      new BinaryMessageEncoder<CoffeeOrder>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<CoffeeOrder> DECODER =
      new BinaryMessageDecoder<CoffeeOrder>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<CoffeeOrder> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<CoffeeOrder> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<CoffeeOrder> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<CoffeeOrder>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this CoffeeOrder to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a CoffeeOrder from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a CoffeeOrder instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static CoffeeOrder fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public long id;
  @Deprecated public java.util.UUID UUID;
  @Deprecated public java.lang.CharSequence name;
  /** Optional field represents the nickname of the customer */
  @Deprecated public java.lang.CharSequence nickname;
  @Deprecated public com.jskno.avro.generated.Store store;
  @Deprecated public java.util.List<com.jskno.avro.generated.OrderLineItem> orderLineItems;
  @Deprecated public java.time.Instant orderTime;
  @Deprecated public java.time.LocalDate orderDate;
  @Deprecated public java.lang.CharSequence status;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public CoffeeOrder() {}

  /**
   * All-args constructor.
   * @param id The new value for id
   * @param UUID The new value for UUID
   * @param name The new value for name
   * @param nickname Optional field represents the nickname of the customer
   * @param store The new value for store
   * @param orderLineItems The new value for orderLineItems
   * @param orderTime The new value for orderTime
   * @param orderDate The new value for orderDate
   * @param status The new value for status
   */
  public CoffeeOrder(java.lang.Long id, java.util.UUID UUID, java.lang.CharSequence name, java.lang.CharSequence nickname, com.jskno.avro.generated.Store store, java.util.List<com.jskno.avro.generated.OrderLineItem> orderLineItems, java.time.Instant orderTime, java.time.LocalDate orderDate, java.lang.CharSequence status) {
    this.id = id;
    this.UUID = UUID;
    this.name = name;
    this.nickname = nickname;
    this.store = store;
    this.orderLineItems = orderLineItems;
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
    case 2: return name;
    case 3: return nickname;
    case 4: return store;
    case 5: return orderLineItems;
    case 6: return orderTime;
    case 7: return orderDate;
    case 8: return status;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
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
    case 0: id = (java.lang.Long)value$; break;
    case 1: UUID = (java.util.UUID)value$; break;
    case 2: name = (java.lang.CharSequence)value$; break;
    case 3: nickname = (java.lang.CharSequence)value$; break;
    case 4: store = (com.jskno.avro.generated.Store)value$; break;
    case 5: orderLineItems = (java.util.List<com.jskno.avro.generated.OrderLineItem>)value$; break;
    case 6: orderTime = (java.time.Instant)value$; break;
    case 7: orderDate = (java.time.LocalDate)value$; break;
    case 8: status = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public long getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(long value) {
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
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'nickname' field.
   * @return Optional field represents the nickname of the customer
   */
  public java.lang.CharSequence getNickname() {
    return nickname;
  }


  /**
   * Sets the value of the 'nickname' field.
   * Optional field represents the nickname of the customer
   * @param value the value to set.
   */
  public void setNickname(java.lang.CharSequence value) {
    this.nickname = value;
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
  public java.lang.CharSequence getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(java.lang.CharSequence value) {
    this.status = value;
  }

  /**
   * Creates a new CoffeeOrder RecordBuilder.
   * @return A new CoffeeOrder RecordBuilder
   */
  public static com.jskno.avro.generated.CoffeeOrder.Builder newBuilder() {
    return new com.jskno.avro.generated.CoffeeOrder.Builder();
  }

  /**
   * Creates a new CoffeeOrder RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new CoffeeOrder RecordBuilder
   */
  public static com.jskno.avro.generated.CoffeeOrder.Builder newBuilder(com.jskno.avro.generated.CoffeeOrder.Builder other) {
    if (other == null) {
      return new com.jskno.avro.generated.CoffeeOrder.Builder();
    } else {
      return new com.jskno.avro.generated.CoffeeOrder.Builder(other);
    }
  }

  /**
   * Creates a new CoffeeOrder RecordBuilder by copying an existing CoffeeOrder instance.
   * @param other The existing instance to copy.
   * @return A new CoffeeOrder RecordBuilder
   */
  public static com.jskno.avro.generated.CoffeeOrder.Builder newBuilder(com.jskno.avro.generated.CoffeeOrder other) {
    if (other == null) {
      return new com.jskno.avro.generated.CoffeeOrder.Builder();
    } else {
      return new com.jskno.avro.generated.CoffeeOrder.Builder(other);
    }
  }

  /**
   * RecordBuilder for CoffeeOrder instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CoffeeOrder>
    implements org.apache.avro.data.RecordBuilder<CoffeeOrder> {

    private long id;
    private java.util.UUID UUID;
    private java.lang.CharSequence name;
    /** Optional field represents the nickname of the customer */
    private java.lang.CharSequence nickname;
    private com.jskno.avro.generated.Store store;
    private com.jskno.avro.generated.Store.Builder storeBuilder;
    private java.util.List<com.jskno.avro.generated.OrderLineItem> orderLineItems;
    private java.time.Instant orderTime;
    private java.time.LocalDate orderDate;
    private java.lang.CharSequence status;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.jskno.avro.generated.CoffeeOrder.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.UUID)) {
        this.UUID = data().deepCopy(fields()[1].schema(), other.UUID);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.nickname)) {
        this.nickname = data().deepCopy(fields()[3].schema(), other.nickname);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.store)) {
        this.store = data().deepCopy(fields()[4].schema(), other.store);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (other.hasStoreBuilder()) {
        this.storeBuilder = com.jskno.avro.generated.Store.newBuilder(other.getStoreBuilder());
      }
      if (isValidValue(fields()[5], other.orderLineItems)) {
        this.orderLineItems = data().deepCopy(fields()[5].schema(), other.orderLineItems);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.orderTime)) {
        this.orderTime = data().deepCopy(fields()[6].schema(), other.orderTime);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.orderDate)) {
        this.orderDate = data().deepCopy(fields()[7].schema(), other.orderDate);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.status)) {
        this.status = data().deepCopy(fields()[8].schema(), other.status);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
    }

    /**
     * Creates a Builder by copying an existing CoffeeOrder instance
     * @param other The existing instance to copy.
     */
    private Builder(com.jskno.avro.generated.CoffeeOrder other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.UUID)) {
        this.UUID = data().deepCopy(fields()[1].schema(), other.UUID);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.nickname)) {
        this.nickname = data().deepCopy(fields()[3].schema(), other.nickname);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.store)) {
        this.store = data().deepCopy(fields()[4].schema(), other.store);
        fieldSetFlags()[4] = true;
      }
      this.storeBuilder = null;
      if (isValidValue(fields()[5], other.orderLineItems)) {
        this.orderLineItems = data().deepCopy(fields()[5].schema(), other.orderLineItems);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.orderTime)) {
        this.orderTime = data().deepCopy(fields()[6].schema(), other.orderTime);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.orderDate)) {
        this.orderDate = data().deepCopy(fields()[7].schema(), other.orderDate);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.status)) {
        this.status = data().deepCopy(fields()[8].schema(), other.status);
        fieldSetFlags()[8] = true;
      }
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public long getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder setId(long value) {
      validate(fields()[0], value);
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
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder clearId() {
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
    public com.jskno.avro.generated.CoffeeOrder.Builder setUUID(java.util.UUID value) {
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
    public com.jskno.avro.generated.CoffeeOrder.Builder clearUUID() {
      UUID = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder setName(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.name = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder clearName() {
      name = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'nickname' field.
      * Optional field represents the nickname of the customer
      * @return The value.
      */
    public java.lang.CharSequence getNickname() {
      return nickname;
    }


    /**
      * Sets the value of the 'nickname' field.
      * Optional field represents the nickname of the customer
      * @param value The value of 'nickname'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder setNickname(java.lang.CharSequence value) {
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
    public com.jskno.avro.generated.CoffeeOrder.Builder clearNickname() {
      nickname = null;
      fieldSetFlags()[3] = false;
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
    public com.jskno.avro.generated.CoffeeOrder.Builder setStore(com.jskno.avro.generated.Store value) {
      validate(fields()[4], value);
      this.storeBuilder = null;
      this.store = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'store' field has been set.
      * @return True if the 'store' field has been set, false otherwise.
      */
    public boolean hasStore() {
      return fieldSetFlags()[4];
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
    public com.jskno.avro.generated.CoffeeOrder.Builder setStoreBuilder(com.jskno.avro.generated.Store.Builder value) {
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
    public com.jskno.avro.generated.CoffeeOrder.Builder clearStore() {
      store = null;
      storeBuilder = null;
      fieldSetFlags()[4] = false;
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
    public com.jskno.avro.generated.CoffeeOrder.Builder setOrderLineItems(java.util.List<com.jskno.avro.generated.OrderLineItem> value) {
      validate(fields()[5], value);
      this.orderLineItems = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'orderLineItems' field has been set.
      * @return True if the 'orderLineItems' field has been set, false otherwise.
      */
    public boolean hasOrderLineItems() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'orderLineItems' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder clearOrderLineItems() {
      orderLineItems = null;
      fieldSetFlags()[5] = false;
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
    public com.jskno.avro.generated.CoffeeOrder.Builder setOrderTime(java.time.Instant value) {
      validate(fields()[6], value);
      this.orderTime = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'orderTime' field has been set.
      * @return True if the 'orderTime' field has been set, false otherwise.
      */
    public boolean hasOrderTime() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'orderTime' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder clearOrderTime() {
      fieldSetFlags()[6] = false;
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
    public com.jskno.avro.generated.CoffeeOrder.Builder setOrderDate(java.time.LocalDate value) {
      validate(fields()[7], value);
      this.orderDate = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'orderDate' field has been set.
      * @return True if the 'orderDate' field has been set, false otherwise.
      */
    public boolean hasOrderDate() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'orderDate' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder clearOrderDate() {
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public java.lang.CharSequence getStatus() {
      return status;
    }


    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder setStatus(java.lang.CharSequence value) {
      validate(fields()[8], value);
      this.status = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public com.jskno.avro.generated.CoffeeOrder.Builder clearStatus() {
      status = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CoffeeOrder build() {
      try {
        CoffeeOrder record = new CoffeeOrder();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Long) defaultValue(fields()[0]);
        record.UUID = fieldSetFlags()[1] ? this.UUID : (java.util.UUID) defaultValue(fields()[1]);
        record.name = fieldSetFlags()[2] ? this.name : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.nickname = fieldSetFlags()[3] ? this.nickname : (java.lang.CharSequence) defaultValue(fields()[3]);
        if (storeBuilder != null) {
          try {
            record.store = this.storeBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("store"));
            throw e;
          }
        } else {
          record.store = fieldSetFlags()[4] ? this.store : (com.jskno.avro.generated.Store) defaultValue(fields()[4]);
        }
        record.orderLineItems = fieldSetFlags()[5] ? this.orderLineItems : (java.util.List<com.jskno.avro.generated.OrderLineItem>) defaultValue(fields()[5]);
        record.orderTime = fieldSetFlags()[6] ? this.orderTime : (java.time.Instant) defaultValue(fields()[6]);
        record.orderDate = fieldSetFlags()[7] ? this.orderDate : (java.time.LocalDate) defaultValue(fields()[7]);
        record.status = fieldSetFlags()[8] ? this.status : (java.lang.CharSequence) defaultValue(fields()[8]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<CoffeeOrder>
    WRITER$ = (org.apache.avro.io.DatumWriter<CoffeeOrder>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<CoffeeOrder>
    READER$ = (org.apache.avro.io.DatumReader<CoffeeOrder>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










