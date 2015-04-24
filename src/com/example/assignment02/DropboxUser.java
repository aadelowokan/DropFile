package com.example.assignment02;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DropboxUser
{
	@PrimaryKey
	@Persistent
	private Key id;
	
	public void setID(final Key id){ this.id = id; }
	
	public Key id(){ return id; }
	

}