package com.printserver.models;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public int id;
    public String username;
    public String password;
    public String dob;
    public String role;
}
