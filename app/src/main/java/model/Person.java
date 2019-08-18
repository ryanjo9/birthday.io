package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Person {
    private String name;
    private String mobile;
    private String work;
    private String home;
    private String other;

    public Person(String name) {
        this.name = name;
        this.mobile = "";
        this.work = "";
        this.home = "";
        this.other = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getOther() {
        return other;
    }

    public String getFirstName() {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(this.name.split(" ")));
        // This probably isn't the best way to handle this... what if you meet a Mary Alice or somethings...
        return parts.get(0);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Name: " + this.name);
        builder.append("\nMobile: " + this.mobile);
        builder.append("\nWork: " + this.work);
        builder.append("\nHome: " + this.home);
        builder.append("\nOther: " + this.other);
//        System.out.println("Builder: " + builder.toString());
        return builder.toString();
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }
}
