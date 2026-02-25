package me.zerog.tets2huclicker.utils;

public interface Executable <In, Out> {
    void execute();
    Out execute(In in);
}
