package com.javaoffers.test.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/16 02:56
 */
@Entity(name = "test")
public class EntryTest {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "value")
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
