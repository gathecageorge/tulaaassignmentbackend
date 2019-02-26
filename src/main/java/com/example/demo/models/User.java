package com.example.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "tblusers")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

    @Column(nullable = false)
    private String date_of_birth;

    private String postal_address;

    @Column(unique = true, nullable = false)
    private String national_id;

    @Column(nullable = false)
    private String gender;

    @Transient
    private List<String> errors = new ArrayList<>();

    public boolean isValid() {
        errors = new ArrayList<>();

        if(first_name.isEmpty()){
            errors.add("first_name cannot be empty");
        }
        if(last_name.isEmpty()){
            errors.add("last_name cannot be empty");
        }
        if(date_of_birth.isEmpty()){
            errors.add("date_of_birth cannot be empty");
        }
        if(national_id.isEmpty()){
            errors.add("national_id cannot be empty");
        }
        if(gender.isEmpty()){
            errors.add("gender cannot be empty");
        }

        return  errors.size() == 0;
    }

    public String getErrors(){
        return errors.toString();
    }
}
