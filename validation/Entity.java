package com.houbank.mls.test.validation;

import java.util.Date;

/**
 * Created by du on 2017/2/10.
 */
public class Entity {
    private Integer id;

    @Validator(name="员工姓名",isNotNull = true)
    private String name;

    @Validator(name="入职时间" )
    private Date entryTime;

    @Validator(name="薪资")
    private Double salary;

    @Validator(name="描述",minLength = 1,maxLength = 5)
    private String desc;

    @Validator(name="级别",range = "A,B,C,100")
    private String level;

    @Validator(name="手机号",pattern = "^[1][3,4,5,7,8][0-9]{9}$")
    private String phone;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
