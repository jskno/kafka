/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.learnavro.domain.generated;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class CoffeeUpdateEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -186897242921428221L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CoffeeUpdateEvent\",\"namespace\":\"com.learnavro.domain.generated\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"logicalType\":\"uuid\"}},{\"name\":\"status\",\"type\":{\"type\":\"enum\",\"name\":\"OrderStatus\",\"symbols\":[\"PROCESSING\",\"READY_FOR_PICKUP\"]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();
static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.Conversions.UUIDConversion());
  }

  private static final BinaryMessageEncoder<CoffeeUpdateEvent> ENCODER =
      new BinaryMessageEncoder<CoffeeUpdateEvent>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<CoffeeUpdateEvent> DECODER =
      new BinaryMessageDecoder<CoffeeUpdateEvent>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<CoffeeUpdateEvent> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<CoffeeUpdateEvent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<CoffeeUpdateEvent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<CoffeeUpdateEvent>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this CoffeeUpdateEvent to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a CoffeeUpdateEvent from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a CoffeeUpdateEvent instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static CoffeeUpdateEvent fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.util.UUID id;
  @Deprecated public com.learnavro.domain.generated.OrderStatus status;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public CoffeeUpdateEvent() {}

  /**
   * All-args constructor.
   * @param id The new value for id
   * @param status The new value for status
   */
  public CoffeeUpdateEvent(java.util.UUID id, com.learnavro.domain.generated.OrderStatus status) {
    this.id = id;
    this.status = status;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return status;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  private static final org.apache.avro.Conversion<?>[] conversions =
      new org.apache.avro.Conversion<?>[] {
      new org.apache.avro.Conversions.UUIDConversion(),
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
    case 0: id = (java.util.UUID)value$; break;
    case 1: status = (com.learnavro.domain.generated.OrderStatus)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public java.util.UUID getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.util.UUID value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'status' field.
   * @return The value of the 'status' field.
   */
  public com.learnavro.domain.generated.OrderStatus getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(com.learnavro.domain.generated.OrderStatus value) {
    this.status = value;
  }

  /**
   * Creates a new CoffeeUpdateEvent RecordBuilder.
   * @return A new CoffeeUpdateEvent RecordBuilder
   */
  public static com.learnavro.domain.generated.CoffeeUpdateEvent.Builder newBuilder() {
    return new com.learnavro.domain.generated.CoffeeUpdateEvent.Builder();
  }

  /**
   * Creates a new CoffeeUpdateEvent RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new CoffeeUpdateEvent RecordBuilder
   */
  public static com.learnavro.domain.generated.CoffeeUpdateEvent.Builder newBuilder(com.learnavro.domain.generated.CoffeeUpdateEvent.Builder other) {
    if (other == null) {
      return new com.learnavro.domain.generated.CoffeeUpdateEvent.Builder();
    } else {
      return new com.learnavro.domain.generated.CoffeeUpdateEvent.Builder(other);
    }
  }

  /**
   * Creates a new CoffeeUpdateEvent RecordBuilder by copying an existing CoffeeUpdateEvent instance.
   * @param other The existing instance to copy.
   * @return A new CoffeeUpdateEvent RecordBuilder
   */
  public static com.learnavro.domain.generated.CoffeeUpdateEvent.Builder newBuilder(com.learnavro.domain.generated.CoffeeUpdateEvent other) {
    if (other == null) {
      return new com.learnavro.domain.generated.CoffeeUpdateEvent.Builder();
    } else {
      return new com.learnavro.domain.generated.CoffeeUpdateEvent.Builder(other);
    }
  }

  /**
   * RecordBuilder for CoffeeUpdateEvent instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CoffeeUpdateEvent>
    implements org.apache.avro.data.RecordBuilder<CoffeeUpdateEvent> {

    private java.util.UUID id;
    private com.learnavro.domain.generated.OrderStatus status;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.learnavro.domain.generated.CoffeeUpdateEvent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.status)) {
        this.status = data().deepCopy(fields()[1].schema(), other.status);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing CoffeeUpdateEvent instance
     * @param other The existing instance to copy.
     */
    private Builder(com.learnavro.domain.generated.CoffeeUpdateEvent other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.status)) {
        this.status = data().deepCopy(fields()[1].schema(), other.status);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public java.util.UUID getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public com.learnavro.domain.generated.CoffeeUpdateEvent.Builder setId(java.util.UUID value) {
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
    public com.learnavro.domain.generated.CoffeeUpdateEvent.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public com.learnavro.domain.generated.OrderStatus getStatus() {
      return status;
    }


    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public com.learnavro.domain.generated.CoffeeUpdateEvent.Builder setStatus(com.learnavro.domain.generated.OrderStatus value) {
      validate(fields()[1], value);
      this.status = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public com.learnavro.domain.generated.CoffeeUpdateEvent.Builder clearStatus() {
      status = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CoffeeUpdateEvent build() {
      try {
        CoffeeUpdateEvent record = new CoffeeUpdateEvent();
        record.id = fieldSetFlags()[0] ? this.id : (java.util.UUID) defaultValue(fields()[0]);
        record.status = fieldSetFlags()[1] ? this.status : (com.learnavro.domain.generated.OrderStatus) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<CoffeeUpdateEvent>
    WRITER$ = (org.apache.avro.io.DatumWriter<CoffeeUpdateEvent>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<CoffeeUpdateEvent>
    READER$ = (org.apache.avro.io.DatumReader<CoffeeUpdateEvent>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










