package com.example.assignment02;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DropboxDirectory
{
	@PrimaryKey
	@Persistent
	private Key id;
	
	@Persistent
	private String dir_name;
	
	@Persistent
	private List<String> sub_dir = null;
	
	@Persistent(mappedBy="parent")
	private List<DropboxFile> dir_files;
	
	public void setID(final Key id){ this.id = id; }
	
	public Key id(){ return id; }
	
	public DropboxDirectory(Key id, String dir_name) {
		this.id = id;
		this.dir_name = dir_name;
	}
	
	public void addSubDir(String dir)
	{
		if(sub_dir == null)
			sub_dir = new ArrayList<String>();
		if(!subdirExists(dir))
			sub_dir.add(dir);
	}
	
	public boolean subdirExists(String dir)
	{
		if(sub_dir.contains(dir))
			return true;
		return false;
	}
	
	public boolean isEmpty(String dir)
	{
		if(sub_dir == null && dir_files == null)
			return true;
		else if(sub_dir.size() == 0 && dir_files.size() == 0)
			return true;
		return false;
	}
	
	public void addFile(DropboxFile file)
	{
		if(dir_files == null)
			dir_files = new ArrayList<DropboxFile>();
		dir_files.add(file);
	}
	
	public void deleteFile(DropboxFile file)
	{
		if(dir_files.contains(file))
			dir_files.remove(file);
	}
	
	public void deleteSubDir(String dir)
	{
		sub_dir.remove(dir);
	}
	
	public List<String> directories()
	{
		return sub_dir;
	}
	
	public List<DropboxFile> files()
	{
		return dir_files;
	}

}