package interaction.spec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmu.conditional.Conditional;
import de.fosd.typechef.featureexpr.SingleFeatureExpr;
import interaction.controlflow.ControlflowControl;

public class XMLReader {

	SpecControl specControl;
	ArrayList<Specification> specList;

	public XMLReader() {
		super();
		this.specControl = new SpecControl();
		specList = new ArrayList<>();
	}

	public void read(ControlflowControl finder) {
		/*
		 * SpecControl specControl = new SpecControl();
		
		SingleFeatureExpr[] a = getFeaturesSpec(finder, s1, s2);
		Specification spec = specControl.createAllowReq(a[0], a[1], var)
		 */
		int t = 0;
		File fXmlFile = new File("/Users/larissasoares/git/fork/varviz/varviz.ui/src/interaction/spec/specXML.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
	    Document doc = null;
		
		try {			
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);								
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
		NodeList nList = doc.getElementsByTagName("specification");
	
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
							
				String specType = eElement.getAttributes().item(0).getTextContent();
				String relation = eElement.getFirstChild().getNodeName();
				String from = eElement.getFirstChild().getAttributes().item(0).getTextContent();
				String to = eElement.getFirstChild().getAttributes().item(1).getTextContent();
				
				//Node relationNode = eElement.getFirstChild().getNextSibling();
				Node var = eElement.getFirstChild().getFirstChild();
				
//				//if it has 1 var
//				String vAttributes = var.getAttributes().item(0).getTextContent();//<var name="String c"/>				
//				//if it has 2 vars
//				Node nextvar = var.getNextSibling().getNextSibling();
//				String attributesVar2 = nextvar.getAttributes().item(0).getTextContent();			
//				//if it has 3 vars
//				Node nextvar3 = nextvar.getNextSibling().getNextSibling();
//				String attributesVar3 = nextvar3.getAttributes().item(0).getTextContent();				
				
				Specification spec;
				SingleFeatureExpr[] a = getFeaturesSpec(finder, from, to);		
				
				if(specType.equals("Allow")){										
					if(relation==null){					
						spec = specControl.createAllow(a[0], a[1]);		
						specList.add(spec);
						
					}else if(relation.equals("require")){
						if(var!=null){//has 1 var
							String varName = var.getAttributes().item(0).getTextContent();//<var name="String c"/>						
							spec = specControl.createAllowReq(a[0], a[1], varName);							
						}else{							
							spec = specControl.createAllowReq(a[0], a[1]);							
						}
						specList.add(spec);
						
					}else if(relation.equals("suppress")){
						if(var!=null){//has 1 var
							String varName = var.getAttributes().item(0).getTextContent();//<var name="String c"/>						
							spec = specControl.createAllowSup(a[0], a[1], varName);							
						}else{							
							spec = specControl.createAllowSup(a[0], a[1]);							
						}
						specList.add(spec);
					}
					
					
				}else if(specType.equals("Forbid")){
					if(relation==null){					
						spec = specControl.createForbid(a[0], a[1]);		
						specList.add(spec);
						
					}else if(relation.equals("require")){
						if(var!=null){//has 1 var
							String varName = var.getAttributes().item(0).getTextContent();//<var name="String c"/>						
							spec = specControl.createForbidReq(a[0], a[1], varName);							
						}else{							
							spec = specControl.createForbidReq(a[0], a[1]);							
						}
						specList.add(spec);
						
					}else if(relation.equals("suppress")){
						if(var!=null){//has 1 var
							String varName = var.getAttributes().item(0).getTextContent();//<var name="String c"/>						
							spec = specControl.createForbidSup(a[0], a[1], varName);							
						}else{							
							spec = specControl.createForbidSup(a[0], a[1]);							
						}
						specList.add(spec);
					}
				}

			}
		}
		
	}

	private SingleFeatureExpr[] getFeaturesSpec(ControlflowControl finder, String s1, String s2) {
		Collection<SingleFeatureExpr> feat = finder.getFeatures();
		
		SingleFeatureExpr[] a = new SingleFeatureExpr[2];
		int i = 0;
		for (SingleFeatureExpr feature1 : feat) {
			
			if(Conditional.getCTXString(feature1).equals(s1)){
				a[i] = feature1;
				i++;
			}
			else if(Conditional.getCTXString(feature1).equals(s2)){
				a[i] = feature1;
				i++;
			}
		}
		return a;
		
	}
	
	public ArrayList<Specification> getSpec() {
		return specList;
	}

}
