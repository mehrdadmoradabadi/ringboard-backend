package com.wallboard.wallboard.pbx.adapters;

//@Service("cisco")
public class CiscoService implements PBXAdapter{
    @Override
    public String connect() {
        return ("Connected to cisco");
    }

    @Override
    public String disconnect() {
        return ("Disconnected from cisco");
    }

    @Override
    public String fetchData() {
        return ("Fetching data from cisco");
    }
}
