package interaction.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import interaction.spec.SpecificationXML;

public class TesteSpecXML {

	@Test
	public void test() {
		//fail("Not yet implemented");
		SpecificationXML xml = new SpecificationXML();
		List<PopOption> allOptionsSelected = new ArrayList<>();
		
		//[S to F + info Allow 'suppresses' on String c state: true, 
		//S to F + info Forbid 'suppresses' on String c state: false, 
		//F to W + info Allow 'requires' on String c state: false, 
		//F to W + info Forbid 'requires' on String c state: true, 
		//F to W + info Allow 'requires' on String weather state: false, 
		//F to W + info Forbid 'requires' on String weather state: false]
		PopOption pop = new PopOption("Allow 'suppresses' on String c", "S", "F");
		pop.setState(true);
		allOptionsSelected.add(pop);
		pop = new PopOption("Forbid 'suppresses' on String c", "S", "F");
		pop.setState(false);
		allOptionsSelected.add(pop);
		pop = new PopOption("'requires' on String c", "F", "W");
		pop.setState(false);
		allOptionsSelected.add(pop);
		pop = new PopOption("Forbid 'requires' on String c", "F", "W");
		pop.setState(true);
		allOptionsSelected.add(pop);
		pop = new PopOption("Allow 'requires' on String weather", "F", "W");
		pop.setState(false);
		allOptionsSelected.add(pop);
		pop = new PopOption("Forbid 'requires' on String weather", "F", "W");
		pop.setState(false);
		allOptionsSelected.add(pop);
		
		xml.create(allOptionsSelected);
		
		//assertEquals(specTry.printSpec(s),print);
	}

}
