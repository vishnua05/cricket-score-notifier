package scorenotifierplugin.provider;

public class EndOfScoreException extends Exception {
	private static final long serialVersionUID = -6375503433879271015L;

	public EndOfScoreException() {
	}
	
	public EndOfScoreException(Exception e) {
		super(e);
	}

}
