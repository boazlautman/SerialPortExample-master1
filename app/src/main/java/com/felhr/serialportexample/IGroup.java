package com.felhr.serialportexample;

/**
 * Created by boaz on 11/19/2015.
 */
public interface IGroup {
    String ToString();
    void FromBuffer(byte[] msg);
}
