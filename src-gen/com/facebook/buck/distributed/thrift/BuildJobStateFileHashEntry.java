/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.facebook.buck.distributed.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-06-13")
public class BuildJobStateFileHashEntry implements org.apache.thrift.TBase<BuildJobStateFileHashEntry, BuildJobStateFileHashEntry._Fields>, java.io.Serializable, Cloneable, Comparable<BuildJobStateFileHashEntry> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("BuildJobStateFileHashEntry");

  private static final org.apache.thrift.protocol.TField PATH_FIELD_DESC = new org.apache.thrift.protocol.TField("path", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField ARCHIVE_MEMBER_PATH_FIELD_DESC = new org.apache.thrift.protocol.TField("archiveMemberPath", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField HASH_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("hashCode", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField IS_DIRECTORY_FIELD_DESC = new org.apache.thrift.protocol.TField("isDirectory", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField PATH_IS_ABSOLUTE_FIELD_DESC = new org.apache.thrift.protocol.TField("pathIsAbsolute", org.apache.thrift.protocol.TType.BOOL, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new BuildJobStateFileHashEntryStandardSchemeFactory());
    schemes.put(TupleScheme.class, new BuildJobStateFileHashEntryTupleSchemeFactory());
  }

  public PathWithUnixSeparators path; // optional
  public String archiveMemberPath; // optional
  public String hashCode; // optional
  public boolean isDirectory; // optional
  public boolean pathIsAbsolute; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PATH((short)1, "path"),
    ARCHIVE_MEMBER_PATH((short)2, "archiveMemberPath"),
    HASH_CODE((short)3, "hashCode"),
    IS_DIRECTORY((short)4, "isDirectory"),
    PATH_IS_ABSOLUTE((short)5, "pathIsAbsolute");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // PATH
          return PATH;
        case 2: // ARCHIVE_MEMBER_PATH
          return ARCHIVE_MEMBER_PATH;
        case 3: // HASH_CODE
          return HASH_CODE;
        case 4: // IS_DIRECTORY
          return IS_DIRECTORY;
        case 5: // PATH_IS_ABSOLUTE
          return PATH_IS_ABSOLUTE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ISDIRECTORY_ISSET_ID = 0;
  private static final int __PATHISABSOLUTE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.PATH,_Fields.ARCHIVE_MEMBER_PATH,_Fields.HASH_CODE,_Fields.IS_DIRECTORY,_Fields.PATH_IS_ABSOLUTE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PATH, new org.apache.thrift.meta_data.FieldMetaData("path", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PathWithUnixSeparators.class)));
    tmpMap.put(_Fields.ARCHIVE_MEMBER_PATH, new org.apache.thrift.meta_data.FieldMetaData("archiveMemberPath", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.HASH_CODE, new org.apache.thrift.meta_data.FieldMetaData("hashCode", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.IS_DIRECTORY, new org.apache.thrift.meta_data.FieldMetaData("isDirectory", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.PATH_IS_ABSOLUTE, new org.apache.thrift.meta_data.FieldMetaData("pathIsAbsolute", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(BuildJobStateFileHashEntry.class, metaDataMap);
  }

  public BuildJobStateFileHashEntry() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public BuildJobStateFileHashEntry(BuildJobStateFileHashEntry other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetPath()) {
      this.path = new PathWithUnixSeparators(other.path);
    }
    if (other.isSetArchiveMemberPath()) {
      this.archiveMemberPath = other.archiveMemberPath;
    }
    if (other.isSetHashCode()) {
      this.hashCode = other.hashCode;
    }
    this.isDirectory = other.isDirectory;
    this.pathIsAbsolute = other.pathIsAbsolute;
  }

  public BuildJobStateFileHashEntry deepCopy() {
    return new BuildJobStateFileHashEntry(this);
  }

  @Override
  public void clear() {
    this.path = null;
    this.archiveMemberPath = null;
    this.hashCode = null;
    setIsDirectoryIsSet(false);
    this.isDirectory = false;
    setPathIsAbsoluteIsSet(false);
    this.pathIsAbsolute = false;
  }

  public PathWithUnixSeparators getPath() {
    return this.path;
  }

  public BuildJobStateFileHashEntry setPath(PathWithUnixSeparators path) {
    this.path = path;
    return this;
  }

  public void unsetPath() {
    this.path = null;
  }

  /** Returns true if field path is set (has been assigned a value) and false otherwise */
  public boolean isSetPath() {
    return this.path != null;
  }

  public void setPathIsSet(boolean value) {
    if (!value) {
      this.path = null;
    }
  }

  public String getArchiveMemberPath() {
    return this.archiveMemberPath;
  }

  public BuildJobStateFileHashEntry setArchiveMemberPath(String archiveMemberPath) {
    this.archiveMemberPath = archiveMemberPath;
    return this;
  }

  public void unsetArchiveMemberPath() {
    this.archiveMemberPath = null;
  }

  /** Returns true if field archiveMemberPath is set (has been assigned a value) and false otherwise */
  public boolean isSetArchiveMemberPath() {
    return this.archiveMemberPath != null;
  }

  public void setArchiveMemberPathIsSet(boolean value) {
    if (!value) {
      this.archiveMemberPath = null;
    }
  }

  public String getHashCode() {
    return this.hashCode;
  }

  public BuildJobStateFileHashEntry setHashCode(String hashCode) {
    this.hashCode = hashCode;
    return this;
  }

  public void unsetHashCode() {
    this.hashCode = null;
  }

  /** Returns true if field hashCode is set (has been assigned a value) and false otherwise */
  public boolean isSetHashCode() {
    return this.hashCode != null;
  }

  public void setHashCodeIsSet(boolean value) {
    if (!value) {
      this.hashCode = null;
    }
  }

  public boolean isIsDirectory() {
    return this.isDirectory;
  }

  public BuildJobStateFileHashEntry setIsDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
    setIsDirectoryIsSet(true);
    return this;
  }

  public void unsetIsDirectory() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ISDIRECTORY_ISSET_ID);
  }

  /** Returns true if field isDirectory is set (has been assigned a value) and false otherwise */
  public boolean isSetIsDirectory() {
    return EncodingUtils.testBit(__isset_bitfield, __ISDIRECTORY_ISSET_ID);
  }

  public void setIsDirectoryIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ISDIRECTORY_ISSET_ID, value);
  }

  public boolean isPathIsAbsolute() {
    return this.pathIsAbsolute;
  }

  public BuildJobStateFileHashEntry setPathIsAbsolute(boolean pathIsAbsolute) {
    this.pathIsAbsolute = pathIsAbsolute;
    setPathIsAbsoluteIsSet(true);
    return this;
  }

  public void unsetPathIsAbsolute() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PATHISABSOLUTE_ISSET_ID);
  }

  /** Returns true if field pathIsAbsolute is set (has been assigned a value) and false otherwise */
  public boolean isSetPathIsAbsolute() {
    return EncodingUtils.testBit(__isset_bitfield, __PATHISABSOLUTE_ISSET_ID);
  }

  public void setPathIsAbsoluteIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PATHISABSOLUTE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PATH:
      if (value == null) {
        unsetPath();
      } else {
        setPath((PathWithUnixSeparators)value);
      }
      break;

    case ARCHIVE_MEMBER_PATH:
      if (value == null) {
        unsetArchiveMemberPath();
      } else {
        setArchiveMemberPath((String)value);
      }
      break;

    case HASH_CODE:
      if (value == null) {
        unsetHashCode();
      } else {
        setHashCode((String)value);
      }
      break;

    case IS_DIRECTORY:
      if (value == null) {
        unsetIsDirectory();
      } else {
        setIsDirectory((Boolean)value);
      }
      break;

    case PATH_IS_ABSOLUTE:
      if (value == null) {
        unsetPathIsAbsolute();
      } else {
        setPathIsAbsolute((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PATH:
      return getPath();

    case ARCHIVE_MEMBER_PATH:
      return getArchiveMemberPath();

    case HASH_CODE:
      return getHashCode();

    case IS_DIRECTORY:
      return isIsDirectory();

    case PATH_IS_ABSOLUTE:
      return isPathIsAbsolute();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PATH:
      return isSetPath();
    case ARCHIVE_MEMBER_PATH:
      return isSetArchiveMemberPath();
    case HASH_CODE:
      return isSetHashCode();
    case IS_DIRECTORY:
      return isSetIsDirectory();
    case PATH_IS_ABSOLUTE:
      return isSetPathIsAbsolute();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof BuildJobStateFileHashEntry)
      return this.equals((BuildJobStateFileHashEntry)that);
    return false;
  }

  public boolean equals(BuildJobStateFileHashEntry that) {
    if (that == null)
      return false;

    boolean this_present_path = true && this.isSetPath();
    boolean that_present_path = true && that.isSetPath();
    if (this_present_path || that_present_path) {
      if (!(this_present_path && that_present_path))
        return false;
      if (!this.path.equals(that.path))
        return false;
    }

    boolean this_present_archiveMemberPath = true && this.isSetArchiveMemberPath();
    boolean that_present_archiveMemberPath = true && that.isSetArchiveMemberPath();
    if (this_present_archiveMemberPath || that_present_archiveMemberPath) {
      if (!(this_present_archiveMemberPath && that_present_archiveMemberPath))
        return false;
      if (!this.archiveMemberPath.equals(that.archiveMemberPath))
        return false;
    }

    boolean this_present_hashCode = true && this.isSetHashCode();
    boolean that_present_hashCode = true && that.isSetHashCode();
    if (this_present_hashCode || that_present_hashCode) {
      if (!(this_present_hashCode && that_present_hashCode))
        return false;
      if (!this.hashCode.equals(that.hashCode))
        return false;
    }

    boolean this_present_isDirectory = true && this.isSetIsDirectory();
    boolean that_present_isDirectory = true && that.isSetIsDirectory();
    if (this_present_isDirectory || that_present_isDirectory) {
      if (!(this_present_isDirectory && that_present_isDirectory))
        return false;
      if (this.isDirectory != that.isDirectory)
        return false;
    }

    boolean this_present_pathIsAbsolute = true && this.isSetPathIsAbsolute();
    boolean that_present_pathIsAbsolute = true && that.isSetPathIsAbsolute();
    if (this_present_pathIsAbsolute || that_present_pathIsAbsolute) {
      if (!(this_present_pathIsAbsolute && that_present_pathIsAbsolute))
        return false;
      if (this.pathIsAbsolute != that.pathIsAbsolute)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_path = true && (isSetPath());
    list.add(present_path);
    if (present_path)
      list.add(path);

    boolean present_archiveMemberPath = true && (isSetArchiveMemberPath());
    list.add(present_archiveMemberPath);
    if (present_archiveMemberPath)
      list.add(archiveMemberPath);

    boolean present_hashCode = true && (isSetHashCode());
    list.add(present_hashCode);
    if (present_hashCode)
      list.add(hashCode);

    boolean present_isDirectory = true && (isSetIsDirectory());
    list.add(present_isDirectory);
    if (present_isDirectory)
      list.add(isDirectory);

    boolean present_pathIsAbsolute = true && (isSetPathIsAbsolute());
    list.add(present_pathIsAbsolute);
    if (present_pathIsAbsolute)
      list.add(pathIsAbsolute);

    return list.hashCode();
  }

  @Override
  public int compareTo(BuildJobStateFileHashEntry other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetPath()).compareTo(other.isSetPath());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPath()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.path, other.path);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetArchiveMemberPath()).compareTo(other.isSetArchiveMemberPath());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetArchiveMemberPath()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.archiveMemberPath, other.archiveMemberPath);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetHashCode()).compareTo(other.isSetHashCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHashCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hashCode, other.hashCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIsDirectory()).compareTo(other.isSetIsDirectory());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIsDirectory()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.isDirectory, other.isDirectory);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPathIsAbsolute()).compareTo(other.isSetPathIsAbsolute());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPathIsAbsolute()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.pathIsAbsolute, other.pathIsAbsolute);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("BuildJobStateFileHashEntry(");
    boolean first = true;

    if (isSetPath()) {
      sb.append("path:");
      if (this.path == null) {
        sb.append("null");
      } else {
        sb.append(this.path);
      }
      first = false;
    }
    if (isSetArchiveMemberPath()) {
      if (!first) sb.append(", ");
      sb.append("archiveMemberPath:");
      if (this.archiveMemberPath == null) {
        sb.append("null");
      } else {
        sb.append(this.archiveMemberPath);
      }
      first = false;
    }
    if (isSetHashCode()) {
      if (!first) sb.append(", ");
      sb.append("hashCode:");
      if (this.hashCode == null) {
        sb.append("null");
      } else {
        sb.append(this.hashCode);
      }
      first = false;
    }
    if (isSetIsDirectory()) {
      if (!first) sb.append(", ");
      sb.append("isDirectory:");
      sb.append(this.isDirectory);
      first = false;
    }
    if (isSetPathIsAbsolute()) {
      if (!first) sb.append(", ");
      sb.append("pathIsAbsolute:");
      sb.append(this.pathIsAbsolute);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (path != null) {
      path.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class BuildJobStateFileHashEntryStandardSchemeFactory implements SchemeFactory {
    public BuildJobStateFileHashEntryStandardScheme getScheme() {
      return new BuildJobStateFileHashEntryStandardScheme();
    }
  }

  private static class BuildJobStateFileHashEntryStandardScheme extends StandardScheme<BuildJobStateFileHashEntry> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, BuildJobStateFileHashEntry struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PATH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.path = new PathWithUnixSeparators();
              struct.path.read(iprot);
              struct.setPathIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ARCHIVE_MEMBER_PATH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.archiveMemberPath = iprot.readString();
              struct.setArchiveMemberPathIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // HASH_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.hashCode = iprot.readString();
              struct.setHashCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // IS_DIRECTORY
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.isDirectory = iprot.readBool();
              struct.setIsDirectoryIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // PATH_IS_ABSOLUTE
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.pathIsAbsolute = iprot.readBool();
              struct.setPathIsAbsoluteIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, BuildJobStateFileHashEntry struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.path != null) {
        if (struct.isSetPath()) {
          oprot.writeFieldBegin(PATH_FIELD_DESC);
          struct.path.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.archiveMemberPath != null) {
        if (struct.isSetArchiveMemberPath()) {
          oprot.writeFieldBegin(ARCHIVE_MEMBER_PATH_FIELD_DESC);
          oprot.writeString(struct.archiveMemberPath);
          oprot.writeFieldEnd();
        }
      }
      if (struct.hashCode != null) {
        if (struct.isSetHashCode()) {
          oprot.writeFieldBegin(HASH_CODE_FIELD_DESC);
          oprot.writeString(struct.hashCode);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetIsDirectory()) {
        oprot.writeFieldBegin(IS_DIRECTORY_FIELD_DESC);
        oprot.writeBool(struct.isDirectory);
        oprot.writeFieldEnd();
      }
      if (struct.isSetPathIsAbsolute()) {
        oprot.writeFieldBegin(PATH_IS_ABSOLUTE_FIELD_DESC);
        oprot.writeBool(struct.pathIsAbsolute);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class BuildJobStateFileHashEntryTupleSchemeFactory implements SchemeFactory {
    public BuildJobStateFileHashEntryTupleScheme getScheme() {
      return new BuildJobStateFileHashEntryTupleScheme();
    }
  }

  private static class BuildJobStateFileHashEntryTupleScheme extends TupleScheme<BuildJobStateFileHashEntry> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, BuildJobStateFileHashEntry struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetPath()) {
        optionals.set(0);
      }
      if (struct.isSetArchiveMemberPath()) {
        optionals.set(1);
      }
      if (struct.isSetHashCode()) {
        optionals.set(2);
      }
      if (struct.isSetIsDirectory()) {
        optionals.set(3);
      }
      if (struct.isSetPathIsAbsolute()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetPath()) {
        struct.path.write(oprot);
      }
      if (struct.isSetArchiveMemberPath()) {
        oprot.writeString(struct.archiveMemberPath);
      }
      if (struct.isSetHashCode()) {
        oprot.writeString(struct.hashCode);
      }
      if (struct.isSetIsDirectory()) {
        oprot.writeBool(struct.isDirectory);
      }
      if (struct.isSetPathIsAbsolute()) {
        oprot.writeBool(struct.pathIsAbsolute);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, BuildJobStateFileHashEntry struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.path = new PathWithUnixSeparators();
        struct.path.read(iprot);
        struct.setPathIsSet(true);
      }
      if (incoming.get(1)) {
        struct.archiveMemberPath = iprot.readString();
        struct.setArchiveMemberPathIsSet(true);
      }
      if (incoming.get(2)) {
        struct.hashCode = iprot.readString();
        struct.setHashCodeIsSet(true);
      }
      if (incoming.get(3)) {
        struct.isDirectory = iprot.readBool();
        struct.setIsDirectoryIsSet(true);
      }
      if (incoming.get(4)) {
        struct.pathIsAbsolute = iprot.readBool();
        struct.setPathIsAbsoluteIsSet(true);
      }
    }
  }

}

