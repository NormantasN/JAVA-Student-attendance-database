package com.example.laboras4;

import java.time.LocalDate;

public class Student {
    private final int id;
    private final int groupId;
    private final LocalDate date;
    private final char presence;

    public Student(int id, int groupId, LocalDate date, char presence) {
        this.id = id;
        this.groupId = groupId;
        this.date = date;
        this.presence = presence;
    }

    public int getId() { return id; }
    public int getGroupId() {
        return groupId;
    }
    public LocalDate getDate() {
        return date;
    }
    public char getPresence() {
        return presence;
    }

}
