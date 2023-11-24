package com.printserver.models;

import java.io.Serial;
import java.io.Serializable;

public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public int role_id;
    public String role_name;

    @Override
    public String toString() {
        return role_name;
    }
}
