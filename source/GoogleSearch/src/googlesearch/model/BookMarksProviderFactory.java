package googlesearch.model;

public class BookMarksProviderFactory {

	private static BookMarksProviderFactory instance;
	
	public static BookMarksProviderFactory getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}
	
	public BookMarksProviderFactory(IBookMarksProvider bookMarksProvider) {
		this.bookMarksProvider = bookMarksProvider;
	}
	
	private IBookMarksProvider bookMarksProvider;

	public IBookMarksProvider getBookMarksProvider() {
		return bookMarksProvider;
	}
	

	private static void init() {
		IBookMarksProvider bookMarksProvider;
		if (isWindows()) {
			bookMarksProvider = new WindowsBookMarksProvider();
		} else {
			bookMarksProvider = new DefaultBookMarksProvider();
		}
		instance = new BookMarksProviderFactory(bookMarksProvider);
	}

	
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}
 
	public static boolean isSolaris() {
		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}
	
}
