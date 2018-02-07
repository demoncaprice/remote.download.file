package org.caprice.remote.download.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;

public class DownloadFile {
	
	private final String urlPath;
	
	private final String savedPath;
	
	private final int threadCount = 10;
	
	public DownloadFile(String urlPath, String savedPath) {
		this.urlPath = urlPath;
		this.savedPath = savedPath;
	}
	
	public void download() {
		RandomAccessFile randomAccessFile = null;
		try {
			System.out.println("开始连接URL【"+this.urlPath+"】");
			URL url = new URL(this.urlPath);
			
			URLConnection openConnection = url.openConnection();
			if(openConnection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) openConnection;
				
				httpConnection.setRequestMethod("GET");
				httpConnection.setConnectTimeout(10 * 1000);
				
				int responseCode = httpConnection.getResponseCode();
				if(responseCode == 200) {
					int contentLength = httpConnection.getContentLength();
					System.out.println("需要下载的文件大小【"+contentLength+"】b");
					
					boolean downloaded = false;
					String urlFileName = this.getUrlFileName(url);
					File file = new File(this.savedPath, urlFileName);
					if(file.exists()) {
						if(contentLength == file.length()) {
							downloaded = true;
						}
					}
					
					if(!downloaded) {
						randomAccessFile = new RandomAccessFile(file, "rw");
						randomAccessFile.close();
						randomAccessFile = null;
						
						int blockSize = contentLength / this.threadCount;
						for(int i=0; i<this.threadCount; i++) {
							int startIndex = i * blockSize;
							int endIndex = (i+1) * blockSize -1;
							
							if(i == (this.threadCount - 1)) {
								endIndex = contentLength - 1;
							}
							
							new Thread(new DownloadRunnabler(i, startIndex, endIndex)).start();
						}
					} else {
						System.out.println("需要下载的文件已经全部下载完毕");
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
				}
				randomAccessFile = null;
			}
		}
	}
	
	private String getUrlFileName(URL url) {
		String urlFilePath = url.getFile();
		return StringUtils.substringAfterLast(urlFilePath, "/");
	}
	
	private class DownloadRunnabler implements Runnable {
		
		private final int threadIndex;
		
        private int startIndex;
        
        private final int endIndex;
        
        public DownloadRunnabler(int threadIndex, int startIndex, int endIndex) {
        	this.threadIndex = threadIndex;
        	this.startIndex = startIndex;
        	this.endIndex = endIndex;
        }

		public void run() {
			RandomAccessFile tempAccessFile = null;
			InputStream inputStream = null;
			RandomAccessFile randomAccessFile = null;
			try {
				System.out.println("线程【"+this.threadIndex+"】-【"+Thread.currentThread().getName()+"】开始下载");
				
				URL url = new URL(urlPath);
				
				String urlFileName = getUrlFileName(url);
				
				File tempFile = new File(savedPath, urlFileName + "_" + this.threadIndex + ".tt");
				tempAccessFile = new RandomAccessFile(tempFile, "rwd");
				if(tempFile.exists()) {
					tempAccessFile.seek(this.threadIndex);
					String readLine = tempAccessFile.readLine();
					System.out.println("线程【"+this.threadIndex+"】-【"+Thread.currentThread().getName()+"】readLine【"+readLine+"】");
					if(StringUtils.isNotBlank(readLine) && StringUtils.isNumeric(readLine)) {
						this.startIndex = Integer.parseInt(readLine) - 1;
					}
				}
				
				URLConnection openConnection = url.openConnection();
				if(openConnection instanceof HttpURLConnection) {
					HttpURLConnection httpConnection = (HttpURLConnection) openConnection;
					
					httpConnection.setRequestMethod("GET");
					httpConnection.setConnectTimeout(10 * 1000);
					httpConnection.setRequestProperty("Range", "bytes=" + this.startIndex + "-" + this.endIndex);
					
					System.out.println("线程【"+this.threadIndex+"】-【"+Thread.currentThread().getName()+"】的下载起点【" + startIndex + "】下载终点【" + endIndex + "】");
					
					if(206 == httpConnection.getResponseCode()) {
						inputStream = httpConnection.getInputStream();
						
						File file = new File(savedPath, urlFileName);
						randomAccessFile = new RandomAccessFile(file, "rw");
						randomAccessFile.seek(this.startIndex);
						
						byte[] buffer = new byte[1024];
						int length = -1;
						int total = 0;
						while((length = inputStream.read(buffer)) > 0){
							randomAccessFile.write(buffer, 0, length);
							total += length;
							
							tempAccessFile.seek(0);
							String pos = startIndex + total + "";
							tempAccessFile.write(pos.getBytes("UTF-8"));
						}
						
						tempAccessFile.close();
						tempAccessFile = null;
						tempFile.delete();
						
					} else {
						System.out.println("响应码是【" +httpConnection.getResponseCode() + "】服务器不支持多线程下载");
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(tempAccessFile != null) {
					try {
						tempAccessFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					tempAccessFile = null;
				}
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					inputStream = null;
				}
				if(randomAccessFile != null) {
					try {
						randomAccessFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					randomAccessFile = null;
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		String urlPath = "https://steamcdn-a.akamaihd.net/client/installer/SteamSetup.exe";
		String savedPath = "D:\\tools\\test";
		DownloadFile dFile = new DownloadFile(urlPath, savedPath);
		dFile.download();
	}
	
}
