package scorenotifierplugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTester {
   
	public static void main(String[] args) {
		String input= "<![CDATA[SL 147/12 (32.5 ovs) ]]";
		String input2 = "Test 2/0 (0.4)";
		input = input2;
        String regex = ".* ([0-9]{1,3})/([0-9]{1,2}) .*\\(([0-9]*\\.[0-6]).*";
        System.out.println(Pattern.matches(regex, input));
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        int i = 1;
        matcher.find();
        while(i <= matcher.groupCount()) {
        	System.out.println(matcher.group(i));
        	i++;
        }
        System.out.println(matcher.groupCount());
        
        String matchString = "<matches><match><header><![CDATA[IRE]]></header><description><![CDATA[132/4 (30.1 ov)]]></description><url-text><![CDATA[Scorecard]]></url-text><url-link>http://live.cricbuzz.com/live/scorecard/2188/India-vs-Ireland-22nd-Match,-Group-B</url-link></match><match><header><![CDATA[RSA]]></header><description><![CDATA[127/7 (37.0 ov)]]></description><url-text><![CDATA[ need 45 ]]></url-text><url-link>http://live.cricbuzz.com/live/scorecard/2187/England-vs-South-Africa-21st-Match,-Group-B</url-link></match></matches>";
        
	}
	
}
