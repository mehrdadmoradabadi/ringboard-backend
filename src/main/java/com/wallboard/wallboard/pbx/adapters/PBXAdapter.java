package com.wallboard.wallboard.pbx.adapters;

public interface PBXAdapter {
    String connect();
    String disconnect();
    String fetchData();
}
