package interaction.spec;

import java.io.File;
import java.io.IOException;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import interaction.view.PopOption;

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

				// set attribute to staff element
				Attr name = doc.createAttribute("name");
				name.setValue("name of the system");
				rootElement.setAttributeNode(name);

				addNewEntrytoXML(doc, rootElement, allOptionsSelected);

				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("/Users/larirocha/git/VarXplorer/varviz.ui/src/interaction/spec/" + "specXML.xml"));

				transformer.transform(source, result);
				
				int apagar = 1;
				System.out.println("File saved!");

			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
	}
	
	public void update(List<PopOption> allOptionsSelected) {
		this.allOptionsSelected = allOptionsSelected;
		  
		  try {
				String filepath = "/Users/larirocha/git/VarXplorer/varviz.ui/src/interaction/spec/specXML.xml";
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(filepath);
				
				Element rootElement =  (Element) doc.getFirstChild();
				rootElement = (Element) doc.getElementsByTagName("system").item(0);

				List<PopOption> allOptionsSelectedCopy = new ArrayList<>();
				allOptionsSelectedCopy.addAll(allOptionsSelected);
				
				for(PopOption specpop: allOptionsSelected){
					if(specpop.getState()){
						String[] info = specpop.getInfo().split(" ");
						String popType = info[0];
						String popSupORreq = info[1];
						String popFrom = specpop.getFrom();
						String popTo = specpop.getTo();
						String popVar="";//in case there is no var
						if(info.length>4) {
							popVar = info[3] + " " + info[4];
						}
						
						//uma vez que peguei toda info do pop eu tenho que saber se já tem 
						NodeList nList = doc.getElementsByTagName("specification");
											
						for (int temp = 0; temp < nList.getLength(); temp++) {

							Node nNode = nList.item(temp);
							if (nNode.getNodeType() == Node.ELEMENT_NODE) {

								Element eElement = (Element) nNode;
								
								String specType = eElement.getAttributes().item(0).getTextContent();//Forbid or Allow
								String relation = eElement.getFirstChild().getNodeName();//suppress or require
								String from = eElement.getFirstChild().getAttributes().item(0).getTextContent();
								String to = eElement.getFirstChild().getAttributes().item(1).getTextContent();
								String var="";
								if(eElement.getFirstChild().hasChildNodes()) {
									var = eElement.getFirstChild().getFirstChild().getAttributes().item(0).getTextContent();
								}
								
								//if just the type is different, such is allow, then update to forbid
 								if(popFrom.equals(from) && popTo.equals(to) && popSupORreq.contains(relation) && popVar.equals(var)
 										&& !popType.equals(specType)){
 									allOptionsSelectedCopy.remove(specpop);
 									
 									// update spec attribute
 									NamedNodeMap attr = eElement.getAttributes();
 									Node nodeAttr = attr.getNamedItem("type");
 									nodeAttr.setTextContent(popType);
 									break;
								
 								// é tudo igual, aí nao adiciona de novo
								}else if(popFrom.equals(from) && popTo.equals(to) && popSupORreq.contains(relation) && popVar.equals(var)
 										&& popType.equals(specType)){
									allOptionsSelectedCopy.remove(specpop);
									break;
									
								//if the relation is different, means that it is a different edge, than add it as new
								}else if(popFrom.equals(from) && popTo.equals(to) && !popSupORreq.contains(relation)){
									//edge as a new entry in the file
									allOptionsSelectedCopy.remove(specpop);
									Boolean hasIt = checkifExists(nList, popFrom, popTo, popSupORreq, popVar, popType);
									if(!hasIt){
										//add new
										add1linetoXML(doc, rootElement, specpop);
										break;
									}
									
															
								//same relation and the var is different
								}else if(popFrom.equals(from) && popTo.equals(to) && !popVar.equals(var)){
									//edge as a new entry in the file
									allOptionsSelectedCopy.remove(specpop);
									int t = 1;
									Boolean hasIt = checkifExists(nList, popFrom, popTo, popSupORreq, popVar, popType);
									if(!hasIt){
										//add new
										add1linetoXML(doc, rootElement, specpop);
										break;
									}
								
								} 								
							}
						}											
					}
				}
				
				addNewEntrytoXML(doc, rootElement, allOptionsSelectedCopy);


				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(filepath));
				transformer.transform(source, result);

				System.out.println("Done");

			   } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			   } catch (TransformerException tfe) {
				tfe.printStackTrace();
			   } catch (IOException ioe) {
				ioe.printStackTrace();
			   } catch (SAXException sae) {
				sae.printStackTrace();
			   }
	}
	

	private Boolean checkifExists(NodeList nList, String popFrom, String popTo, String popSupORreq, String popVar,
			String popType) {
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				
				String specType = eElement.getAttributes().item(0).getTextContent();//Forbid or Allow
				String relation = eElement.getFirstChild().getNodeName();//suppress or require
				String from = eElement.getFirstChild().getAttributes().item(0).getTextContent();
				String to = eElement.getFirstChild().getAttributes().item(1).getTextContent();
				System.out.println(specType + " " + relation + " "  +from + " "  + to);
				String var = eElement.getFirstChild().getFirstChild().getAttributes().item(0).getTextContent();
				
				
				if(popFrom.equals(from) && popTo.equals(to) && popSupORreq.contains(relation) && popVar.equals(var)){
					return true;
				}
			}
		}
		return false;
	}

	private void addNewEntrytoXML(Document doc, Element rootElement, List<PopOption> allOptionsSelected2) {
		for(PopOption specpop: allOptionsSelected2){
			
			if(specpop.getState()){
				
				add1linetoXML(doc, rootElement, specpop);				

			}
		}
		
	}

	private void add1linetoXML(Document doc, Element rootElement, PopOption specpop) {
		
		String[] info = specpop.getInfo().split(" ");
		// firstname elements
		Element spec = doc.createElement("specification");
		
		char digit = info[0].charAt(0);//testing fist argument of the popup or second, one has a number
		int infoPosition = 0;
		if(Character.isDigit(digit)) {
			infoPosition = 1;
		}else {
			infoPosition = 0;
		}
		
		
		spec.setAttribute("type", info[infoPosition]);
		rootElement.appendChild(spec);
		Element supreq = null;
		if(info[infoPosition+1].contains("sup")){
			supreq = doc.createElement("suppress");
			supreq.setAttribute("from", specpop.getFrom());
			supreq.setAttribute("to", specpop.getTo());
			spec.appendChild(supreq);
		}else if(info[infoPosition+1].contains("requi")){
			supreq = doc.createElement("require");
			supreq.setAttribute("from", specpop.getFrom());
			supreq.setAttribute("to", specpop.getTo());
			spec.appendChild(supreq);
		}
		
		//caso tenha, colocar as var
		if(info[infoPosition+2].equals("on")){
			if(info.length>4) {//which means if there is any variable, the variable would be on info[4] and info[5]
				Element var = doc.createElement("var");
				var.setAttribute("name", info[infoPosition+3] + " " + info[infoPosition+4]);
				supreq.appendChild(var);
			}
			
		}		
	}

	public static void main(String argv[]) {


	}

}
