/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.facebook.buck.distributed.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum BuildStatus implements org.apache.thrift.TEnum {
  UNKNOWN(0),
  QUEUED(1),
  BUILDING(2),
  FINISHED_SUCCESSFULLY(3),
  FAILED(4),
  CREATED(5);

  private final int value;

  private BuildStatus(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static BuildStatus findByValue(int value) { 
    switch (value) {
      case 0:
        return UNKNOWN;
      case 1:
        return QUEUED;
      case 2:
        return BUILDING;
      case 3:
        return FINISHED_SUCCESSFULLY;
      case 4:
        return FAILED;
      case 5:
        return CREATED;
      default:
        return null;
    }
  }
}
