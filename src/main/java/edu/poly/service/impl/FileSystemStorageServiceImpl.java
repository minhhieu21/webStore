package edu.poly.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.poly.config.StorageProperties;
import edu.poly.exception.StorageException;
import edu.poly.service.StorageService;

@Service
public class FileSystemStorageServiceImpl implements StorageService{
	
	//khai báo đường dẫn gốc dùng để lưu thông tin file images
	private final Path rootLocation;
	
	//tạo file lưu trữ dựa vào id truyền vào
	@Override
	public String getStorageFilename(MultipartFile file, String id) {
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		return "p" + id + "." + ext;
	}
	
	//truyền các thông tin cấu hình lưu trữ
	public FileSystemStorageServiceImpl(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}
	
	//lưu
	@Override
	public void store(MultipartFile file, String storedFilename) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store emty File !");
			}
			
			Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename))
					.normalize().toAbsolutePath();
			
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException("Can't not storage file outside current directory !") ;
			}
			
			try(InputStream inputStream = file.getInputStream()){
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
			
			} catch (Exception e) {
				throw new StorageException("Failed to store file :",e);
		}
	}
	
	
	//Nạp nội dung của file
	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			
			Resource resource = new UrlResource(file.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			
			throw new StorageException("Could not read file: "+ filename);
			
		} catch (Exception e) {
			throw new StorageException("Could not read file: " +filename);
		}
	}
	
	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}
	
	
	//xóa file
	@Override
	public void delete(String storedFilename) throws IOException {
		//Path destinationFile = rootLocation.resolve(Paths.get(storedFilename).normalize().toAbsolutePath());
		
		Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename))
				.normalize().toAbsolutePath();
		System.out.println(rootLocation.getFileName());
		System.out.println(destinationFile.getFileName());
		Files.delete(destinationFile);
	}
	
	//khởi tạo thư mục
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
			System.out.println(rootLocation.toString());
		} catch (Exception e) {
			throw new StorageException("Could not initialize storages !");
		}
	}
	
}
