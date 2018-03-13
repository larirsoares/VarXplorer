package interaction.spec;

import java.util.ArrayList;

import org.junit.Test;

import interaction.controlflow.ControlflowControl;

public class TestXMLReader {

	@Test
	public void test() {
		XMLReader xmlread = new XMLReader();
		ControlflowControl finder = new ControlflowControl();
		xmlread.read(finder);
		ArrayList<Specification> specs = xmlread.getSpec();
	}

}
