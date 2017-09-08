package com.auth0.example;

public class Settings {

	private String dbName;

	public Settings(String dbName) {
		this.dbName = dbName;
	}

	public Settings() {
	}

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

}
