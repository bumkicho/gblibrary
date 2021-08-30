package com.bkc.gblibrary.utility;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;

import java.time.Instant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.CatalogRepository;



/**
 * 
 * @author bumki
 *
 */

@Component
public class FileUtilities {

	@Autowired
	CatalogRepository catalogRepository;
	
	public void downloadFile(String urlString, String fileName, String dest, Boolean cleanYN, Boolean catalogYN) throws IOException {
		
		if(cleanYN) {
			FileUtils.cleanDirectory(new File(dest));
		}
		
		URL url = new URL(urlString+"/"+fileName);
		File file = new File(dest+File.separatorChar+fileName);
		FileUtils.copyURLToFile(url, file);
        
        if(catalogYN) {
            String destDir = file.getParent();
	        extractFile(destDir, fileName, destDir);
	
	        Optional<Catalog> existingCatalog = catalogRepository.findByName(fileName);
	        
			/*
			 * existingCatalog.ifPresentOrElse((cat)->{ cat.setLastRefreshDt(Instant.now());
			 * }, ()->{ Catalog catalog = getCatalog(); catalog.setName(fileName);
			 * catalog.setUrl(urlString); catalog.setLastRefreshDt(Instant.now());
			 * catalogRepository.save(catalog); });
			 */
	        
	        if(existingCatalog!=null) {
	        	existingCatalog.get().setLastRefreshDt(Instant.now());
			} else {
				Catalog catalog = getCatalog(); catalog.setName(fileName);
				catalog.setUrl(urlString); catalog.setLastRefreshDt(Instant.now());
				catalogRepository.save(catalog); 
			}
        }
    }

	@Bean
	private Catalog getCatalog() {
		return new Catalog();
	}

	public void extractFile(String filePath, String zipFileName, String destDir) throws IOException {
		
		byte[] buffer = new byte[2048];
		
		String fileZip = filePath+File.separatorChar+zipFileName;
		FileInputStream fis = new FileInputStream(fileZip);
		
		if(fileZip.endsWith("bz2")) {

			BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(fis);
			TarArchiveInputStream tarIn = new TarArchiveInputStream(bzIn);
			ArchiveEntry entry = tarIn.getNextEntry();
			
			while(entry!=null) {
				File destPath = new File(destDir, entry.getName());
				
				if (entry.isDirectory()) {
		            destPath.mkdirs();
		        } else {
		        	Path parent = Paths.get(destPath.getParent());
		        	Files.createDirectories(parent);
		            destPath.createNewFile();

		            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
		            int len = 0;

		            while((len = tarIn.read(buffer)) != -1)
		            {
		                bout.write(buffer,0,len);
		            }

		            bout.close();

		        }
		        entry = tarIn.getNextTarEntry();
		    }
		    tarIn.close();
			bzIn.close();
			
		} else {
		
	        ZipInputStream zis = new ZipInputStream(fis);
	        ZipEntry entry = zis.getNextEntry();
	
	        while (entry != null) {

	        	File destPath = new File(destDir, entry.getName());
	        	
	            if (entry.isDirectory()) {
	            	destPath.mkdirs();
	            } else {
	            	Path parent = Paths.get(destPath.getParent());
		        	Files.createDirectories(parent);
	            	destPath.createNewFile();
	            	
	                // write file content
	                FileOutputStream fos = new FileOutputStream(destPath);
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                    fos.write(buffer, 0, len);
	                }
	                fos.close();
	            }
	            entry = zis.getNextEntry();
	        }
	        zis.closeEntry();
	        zis.close();
	        
		}
	}

	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}

}
