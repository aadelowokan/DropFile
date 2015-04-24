package com.example.assignment02;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class FilesServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException 
	{
		String action = req.getParameter("action");
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		DropboxDirectory dir = null;
		
		Key dir_key = KeyFactory.createKey("DropboxDirectory", req.getSession().getAttribute("path")+"");
		
		
		
		int index = Integer.parseInt(req.getParameter("key_value"));
		
		if(action.compareTo("Delete") == 0)
		{
			delete(dir_key, dir, pm, index);	
			resp.sendRedirect("/");
		}
		else if(action.compareTo("Download") == 0)
		{
			dir = pm.getObjectById(DropboxDirectory.class, dir_key);
			BlobKey file_blobKey = download(dir, index);
			BlobstoreServiceFactory.getBlobstoreService().serve(file_blobKey, resp);
			pm.close();
		}
		
	}
	
	public void delete(Key dir_key, DropboxDirectory dir, PersistenceManager pm, int index)
	{
		DropboxFile file = null;
		dir = pm.getObjectById(DropboxDirectory.class, dir_key);
		BlobKey file_blobKey = dir.files().get(index).getBlobKey();
		Key file_id = dir.files().get(index).id();
		file = pm.getObjectById(DropboxFile.class, file_id);
		dir.deleteFile(file);
		pm.deletePersistent(file);
		BlobstoreServiceFactory.getBlobstoreService().delete(file_blobKey);
		pm.close();
	}
	
	public BlobKey download(DropboxDirectory dir, int index)
	{
		BlobKey file_blobKey = dir.files().get(index).getBlobKey();
		return file_blobKey;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		resp.setContentType("text/html");
		
		DropboxFile file = null;
		DropboxDirectory dir = null;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key dir_key = KeyFactory.createKey("DropboxDirectory", req.getSession().getAttribute("path")+"");
		
		Map<String, List<BlobKey>> files_sent = BlobstoreServiceFactory.getBlobstoreService().getUploads(req);
		BlobKey file_blobKey = files_sent.get("file").get(0);
		BlobInfoFactory blobInfo = new BlobInfoFactory();
		String file_name = blobInfo.loadBlobInfo(file_blobKey).getFilename();
		
		dir = pm.getObjectById(DropboxDirectory.class, dir_key);

		file = new DropboxFile(file_name, file_blobKey, dir);
		pm.makePersistent(file);
		dir.addFile(file);
		pm.makePersistent(dir);
		
		pm.close();
		
		resp.sendRedirect("/");
	}
}
