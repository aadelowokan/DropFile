package com.example.assignment02;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class DirectoryServlet extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException 
	{
		resp.setContentType("text/html");
		
		String directory = req.getParameter("dir");
		String action = req.getParameter("action");

		DropboxDirectory dirs = null;
		
		if(action.compareTo("Delete") == 0)
		{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			String path = req.getSession().getAttribute("path")+"";
			deleteSubDir(pm, dirs, directory, path);
		}
		else if(action.compareTo("Change") == 0)
		{
			if(directory.compareTo("..") == 0)
			{
				
				String path = req.getSession().getAttribute("path")+"";
				String back = changeBack(path);
				req.getSession().setAttribute("path", back);
			}
			else
			{
				req.getSession().setAttribute("path", req.getSession().getAttribute("path")+directory);
			}
		}
		resp.sendRedirect("/");
	}
	
	public String changeBack(String path)
	{
		String back = "";
		
		StringBuilder b = new StringBuilder(path);
		b.replace(path.lastIndexOf("/"), path.lastIndexOf("/") + 1, "");
		path = b.toString();
		
		back = b.substring(0, path.lastIndexOf("/")+1);
		return back;
	}
	
	public void deleteSubDir(PersistenceManager pm, DropboxDirectory dirs, String directory, String path)
	{
		Key dir = KeyFactory.createKey("DropboxDirectory", path);
		dirs = pm.getObjectById(DropboxDirectory.class, dir);
		
		if(dirs.subdirExists(directory))
		{
			dirs.deleteSubDir(directory);
			pm.makePersistent(dirs);
			
			dir = KeyFactory.createKey("DropboxDirectory", path+directory);
			dirs = pm.getObjectById(DropboxDirectory.class, dir);
			pm.deletePersistent(dirs);
		}
		pm.close();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		resp.setContentType("text/html");
		
		String directory = req.getParameter("directory");
		
		String path = req.getSession().getAttribute("path")+"";
		
		addSubDir(directory, path);
		
		resp.sendRedirect("/");
	}
	
	public void addSubDir(String directory, String path)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DropboxDirectory dirs = null;
		
		Key dir = KeyFactory.createKey("DropboxDirectory", path);
			
		dirs = pm.getObjectById(DropboxDirectory.class, dir);
		dirs.addSubDir(directory+"/");
			
		dir = KeyFactory.createKey("DropboxDirectory", path+directory+"/");
			
		dirs = new DropboxDirectory(dir, path+directory+"/");
		pm.makePersistent(dirs);
			
		pm.close();
	}
}