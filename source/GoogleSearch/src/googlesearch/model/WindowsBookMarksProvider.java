package googlesearch.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class WindowsBookMarksProvider implements IBookMarksProvider {

	private List<String> empty = new ArrayList<String>();

	
	public List<String> getFavorites() {
		String currentPCFavorites = getCurrentPCFavoritesLocation();
		File linksFolder = new File(new File(currentPCFavorites).getParentFile(), "Links");
		return getFiles(linksFolder);
	}

	private List<String> getFiles(File linksFolder) {
		List<String> favorites = new ArrayList<String>();
		for (File file : linksFolder.listFiles()) {
			try {
				if (!file.isDirectory()) {
					favorites.add(new LnkParser(file).getRealFilename());
				}
			} catch (IOException e) {
				System.out.println("Failed to parse file: " + file);
			}
		}
		return favorites;
	}

	
	public List<String> getRecentDocuments() {
		String recentDocsFile = getRcentDocsFolderLocation();
		File recentDocsFolder = new File(recentDocsFile);
		return getFiles(recentDocsFolder);
	}

	
	public List<String> getBrowserFavorites() {
		return empty;
	}

	private static final String REGQUERY_UTIL = "reg query ";
	private static final String REGSTR_TOKEN = "REG_SZ";
	private static final String COMPUTER_WINDOWS_FAVORITES_FOLDER = REGQUERY_UTIL
			+ "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v Favorites";
	private static final String COMPUTER_WINDOWS_FAVORITES_RECENT = REGQUERY_UTIL
			+ "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v Recent";

	public static String getCurrentPCFavoritesLocation() {
		String cmdExec = COMPUTER_WINDOWS_FAVORITES_FOLDER;
		return getCommandValue(cmdExec);
	}

	public static String getRcentDocsFolderLocation() {
		String cmdExec = COMPUTER_WINDOWS_FAVORITES_RECENT;
		return getCommandValue(cmdExec);
	}

	private static String getCommandValue(String cmdExec) {
		try {
			Process process = Runtime.getRuntime().exec(cmdExec);
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);
			if (p == -1)
				return null;
			return result.substring(p + REGSTR_TOKEN.length()).trim();
		} catch (Exception e) {
			return null;
		}
	}

	private static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw;

		StreamReader(InputStream is) {
			this.is = is;
			sw = new StringWriter();
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
			}
		}

		String getResult() {
			return sw.toString();
		}
	}

	public static void main(String s[]) {
		String currentPCFavorites = getCurrentPCFavoritesLocation();
		String recentDocsFile = getRcentDocsFolderLocation();

		for (File file : new File(recentDocsFile).listFiles()) {
			try {
				System.out.println(new LnkParser(file).getRealFilename() + "----" + file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("----------------------------------Links-------------------------------------" + currentPCFavorites);
		File parentFile = new File(currentPCFavorites).getParentFile();
		File linksFile = new File(parentFile, "Links");
		for (File file : linksFile.listFiles()) {
			try {
				System.out.println(new LnkParser(file).getRealFilename() + "----" + file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

class LnkParser {

	public LnkParser(File f) throws IOException {
		parse(f);
	}

	private boolean isDirectory;
	private boolean isLocal;

	public boolean isDirectory() {
		return isDirectory;
	}

	private String real_file;

	public String getRealFilename() {
		return real_file;
	}

	private void parse(File f) throws IOException {
		// read the entire file into a byte buffer
		FileInputStream fin = new FileInputStream(f);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buff = new byte[256];
		while (true) {
			int n = fin.read(buff);
			if (n == -1) {
				break;
			}
			bout.write(buff, 0, n);
		}
		fin.close();
		byte[] link = bout.toByteArray();

		parseLink(link);
	}

	private void parseLink(byte[] link) {
		// get the flags byte
		byte flags = link[0x14];

		// get the file attributes byte
		final int file_atts_offset = 0x18;
		byte file_atts = link[file_atts_offset];
		byte is_dir_mask = (byte) 0x10;
		if ((file_atts & is_dir_mask) > 0) {
			isDirectory = true;
		} else {
			isDirectory = false;
		}

		// if the shell settings are present, skip them
		final int shell_offset = 0x4c;
		final byte has_shell_mask = (byte) 0x01;
		int shell_len = 0;
		if ((flags & has_shell_mask) > 0) {
			// the plus 2 accounts for the length marker itself
			shell_len = bytes2short(link, shell_offset) + 2;
		}

		// get to the file settings
		int file_start = 0x4c + shell_len;

		final int file_location_info_flag_offset_offset = 0x08;
		int file_location_info_flag = link[file_start + file_location_info_flag_offset_offset];
		isLocal = (file_location_info_flag & 2) == 0;
		// get the local volume and local system values
		//final int localVolumeTable_offset_offset = 0x0C;
		final int basename_offset_offset = 0x10;
		final int networkVolumeTable_offset_offset = 0x14;
		final int finalname_offset_offset = 0x18;
		int finalname_offset = link[file_start + finalname_offset_offset] + file_start;
		String finalname = getNullDelimitedString(link, finalname_offset);
		if (isLocal) {
			int basename_offset = link[file_start + basename_offset_offset] + file_start;
			String basename = getNullDelimitedString(link, basename_offset);
			real_file = basename + finalname;
		} else {
			int networkVolumeTable_offset = link[file_start + networkVolumeTable_offset_offset] + file_start;
			int shareName_offset_offset = 0x08;
			int shareName_offset = link[networkVolumeTable_offset + shareName_offset_offset] + networkVolumeTable_offset;
			String shareName = getNullDelimitedString(link, shareName_offset);
			real_file = shareName + "\\" + finalname;
		}
	}

	private static String getNullDelimitedString(byte[] bytes, int off) {
		int len = 0;
		// count bytes until the null character (0)
		while (true) {
			if (bytes[off + len] == 0) {
				break;
			}
			len++;
		}
		return new String(bytes, off, len);
	}

	/*
	 * convert two bytes into a short note, this is little endian because it's
	 * for an Intel only OS.
	 */
	private static int bytes2short(byte[] bytes, int off) {
		return ((bytes[off + 1] & 0xff) << 8) | (bytes[off] & 0xff);
	}

	/**
	 * Returns the value of the instance variable 'isLocal'.
	 * 
	 * @return Returns the isLocal.
	 */
	public boolean isLocal() {
		return isLocal;
	}
}
