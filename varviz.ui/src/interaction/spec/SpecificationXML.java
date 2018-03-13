package interaction.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SpecificationXML {

	private static List<PopOption> allOptionsSelected = new ArrayList<>();
	
	public void create(List<PopOption> allOptionsSelected) {
		this.allOptionsSelected = allOptionsSelected;
		
		  try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("system");
				doc.appendChild(rootElement);

//				// staff elements
//				Element staff = doc.createElement("Staff");
//				rootElement.appendChild(staff);

				// set attribute to staff element
				Attr name = doc.createAttribute("name");
				name.setValue("name of the system");
				rootElement.setAttributeNode(name);

				// shorten way
				// staff.setAttribute("id", "1");

				for(PopOption specpop: allOptionsSelected){
					
					if(specpop.getState()){
						String[] info = specpop.getInfo().split(" ");
						// firstname elements
						Element spec = doc.createElement("specification");
						spec.setAttribute("type", info[0]);
						rootElement.appendChild(spec);
						Element supreq = null;
						if(info[1].contains("sup")){
							supreq = doc.createElement("suppress");
							supreq.setAttribute("from", specpop.getFrom());
							supreq.setAttribute("to", specpop.getTo());
							spec.appendChild(supreq);
						}else if(info[1].contains("requi")){
							supreq = doc.createElement("require");
							supreq.setAttribute("from", specpop.getFrom());
							supreq.setAttribute("to", specpop.getTo());
							spec.appendChild(supreq);
						}
						
						//caso tenha, colocar as var
						if(info[2].equals("on")){
							Element var = doc.createElement("var");
							var.setAttribute("name", info[3] + " " + info[4]);
							supreq.appendChild(var);
						}
					}
				}

				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("/Users/larissasoares/git/fork/varviz/varviz.ui/src/interaction/view/" + "specXML.XML"));

				// Output to console for testing
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				System.out.println("File saved!");

			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
	}
	

	public static void main(String argv[]) {


	}

}
