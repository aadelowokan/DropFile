package com.example.assignment02;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.example.assignment02.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class Assignment02_2819750Servlet extends HttpServlet 
{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException 
	{
		resp.setContentType("text/html");
		
		UserService us = UserServiceFactory.getUserService();
		User u = us.getCurrentUser();
		String login = us.createLoginURL("/");
		String logout = us.createLogoutURL("/");
		
		PersistenceManager pm = null;
		DropboxUser user = null;
		DropboxDirectory dirs = null;
		
		List <String> subdirs = null;
		List<String> list = new ArrayList<String>();
		
		String result = "";
		if(u != null)
		{
			Key user_key = KeyFactory.createKey("DropboxUser", u.getUserId());
			if(req.getSession().getAttribute("path") == null)
			{
				req.getSession().setAttribute("path", u.getUserId()+"/");

				pm = PMF.get().getPersistenceManager();
				
				Key root = KeyFactory.createKey("DropboxDirectory", u.getUserId()+"/");
				
				try
				{
					user = pm.getObjectById(DropboxUser.class, user_key);
					dirs = pm.getObjectById(DropboxDirectory.class, root);
				}
				catch (Exception e)
				{
					user = addUser(user_key);
					pm.makePersistent(user);
					dirs = new DropboxDirectory(root,  u.getUserId()+"/");
					pm.makePersistent(dirs);
				}
				subdirs = subDirs(dirs);
				list = files(dirs);
				
				pm.close();
			}
			else
			{
				Key key = KeyFactory.createKey("DropboxDirectory", req.getSession().getAttribute("path")+"");
				pm = PMF.get().getPersistenceManager();
				try
				{
					user = pm.getObjectById(DropboxUser.class, user_key);
					dirs = pm.getObjectById(DropboxDirectory.class, key);
				}
				catch (Exception e)
				{
					
				}
				subdirs = subDirs(dirs);
				list = files(dirs);

				pm.close();
			}
			
			String path = req.getSession().getAttribute("path")+"";
			path = path.substring(path.indexOf('/'));
			
			result = "<strong>Contents of: "+ path +"</strong> <br/>";
			
			req.setAttribute("result", result);
			req.setAttribute("subdirs", subdirs);
			req.setAttribute("files", list);
			
			if(req.getSession().getAttribute("path").equals(u.getUserId()+"/"))
				req.setAttribute("root", "change");
		}
		
		req.setAttribute("user", u);
		req.setAttribute("login", login);
		req.setAttribute("logout", logout);
		
		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/root.jsp");
		rd.forward(req, resp);
	}
	
	public DropboxUser addUser(Key user_key)
	{
		DropboxUser user = new DropboxUser();
		user.setID(user_key);
		return user;
	}
	
	public List<String> subDirs(DropboxDirectory dirs)
	{
		if(dirs.directories() != null)
			return dirs.directories();
		else
			return null;
	}
	
	public List<String> files(DropboxDirectory dirs)
	{
		List<DropboxFile> files = null;
		List<String> list = new ArrayList<>();
		if(dirs.files() != null)
		{
			files = dirs.files();
			for(DropboxFile f : files)
				list.add(f.getName());
			return list;
		}
		return null;
	}
}
