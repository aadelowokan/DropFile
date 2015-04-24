package com.example.assignment02;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DropboxFile
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private String file_name;
	
	@Persistent
	private DropboxDirectory parent;
	
	@Persistent
	private BlobKey file_key;
	
	public void setID(final Key id){ this.id = id; }
	
	public Key id(){ return id; }
	
	public DropboxFile(String file_name, BlobKey file_key, DropboxDirectory parent) {
		this.file_name = file_name;
		this.file_key = file_key;
		this.parent = parent;
	}
	
	public String getName()
	{
		return file_name;
	}
	
	public BlobKey getBlobKey()
	{
		return file_key;
	}

}