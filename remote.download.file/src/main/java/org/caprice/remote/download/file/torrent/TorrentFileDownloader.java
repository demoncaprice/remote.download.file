package org.caprice.remote.download.file.torrent;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.yaircc.torrent.bencoding.BEncodedInputStream;
import org.yaircc.torrent.bencoding.MetaInfoFileIO;
import org.yaircc.torrent.data.MIFileInfo;
import org.yaircc.torrent.data.MIInfoSection;
import org.yaircc.torrent.data.MetaInfoFile;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;

public class TorrentFileDownloader {

	private final String torrentPath;
	
	private final String downloadDir;

	public TorrentFileDownloader(String torrentPath, String downloadDir) {
		this.torrentPath = torrentPath;
		this.downloadDir = downloadDir;
	}

	// public void analysis() {
	//// OutputStream os = null;
	// InputStream is = null;
	// try {
	// is = new FileInputStream(new File(this.filePath));
	// MetaInfoFile infoFile = MetaInfoFileIO.load(is);
	//
	// MIInfoSection dataInfo = infoFile.getDataInfo();
	// if(dataInfo != null) {
	// List<MIFileInfo> files = dataInfo.getFiles();
	// if(files != null) {
	// for(MIFileInfo fileInfo : files) {
	// System.out.println("fileName【"+fileInfo.getFileName()+"】fileSize【"+fileInfo.getLength()+"】");
	// }
	// }
	// }
	//
	//// File file = new File("D:\\tools\\test\\test.torrent");
	//// os = new FileOutputStream(file);
	//// MetaInfoFileIO.save(infoFile, os);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	//// if (os != null) {
	//// try {
	//// os.close();
	//// } catch (IOException e) {
	//// e.printStackTrace();
	//// }
	//// os = null;
	//// }
	// if (is != null) {
	// try {
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// is = null;
	// }
	// }
	// }

	public void download() {
		
		try {
			File torrentFile = new File(this.torrentPath);
			byte[] data = FileUtils.readFileToByteArray(torrentFile);
			Torrent torrent = new Torrent(data, false);
			
			
			
//			Client client = new Client(InetAddress.getLocalHost(),
//					SharedTorrent.fromFile(new File(this.filePath), new File("")));
//			client.setMaxDownloadRate(12.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
//			client.setMaxUploadRate(2.0);
//
//			client.addObserver(new Observer() {
//				public void update(Observable observable, Object data) {
//					Client client = (Client) observable;
//					SharedTorrent torrent = client.getTorrent();
//					System.out.println("---------------------------------");
//					System.out.println("已完成：" + torrent.getCompletion() + "%");
//					System.out.println("---------------------------------");
//				}
//			});
//
//			client.download();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String torrentPath = "D:\\tools\\test\\3DMGAME-The.Witcher.3.Game.of.the.Year.Edition.v1.31.GOG-3DM.torrent";
		String downloadDir = "D:\\\\tools\\\\test\\\\download";
		new TorrentFileDownloader(torrentPath, downloadDir).download();
	}

}
