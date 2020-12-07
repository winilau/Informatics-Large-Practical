package uk.ac.ed.inf.aqmaps;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	public AppTest(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}
	
	public void testApp() throws IOException, InterruptedException{
		var input = new String[] {"01","02","03","04","05","06","07","08","09","10","11","12"};
		for (int i = 0; i < input.length; i++) {
			App.main(new String[] {input[i],input[i],"2020", "55.9444","-3.1878","5678","80"});
		}
	}
}
