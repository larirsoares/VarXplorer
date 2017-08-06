package interaction;

import java.awt.Graphics;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JPanel;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;

public class GraphPanel extends JPanel {
	
	private BufferedImage graphImage;
	
	public GraphPanel (BufferedImage image) {
		this.graphImage = image;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(graphImage, 0, 0, null);
	}

	public static BufferedImage rasterize(File svgFile) throws IOException {

	    final BufferedImage[] imagePointer = new BufferedImage[1];

	    // Rendering hints can't be set programatically, so
	    // we override defaults with a temporary stylesheet.
	    // These defaults emphasize quality and precision, and
	    // are more similar to the defaults of other SVG viewers.
	    // SVG documents can still override these defaults.
	    String css = "svg {" +
	            "shape-rendering: geometricPrecision;" +
	            "text-rendering:  geometricPrecision;" +
	            "color-rendering: optimizeQuality;" +
	            "image-rendering: optimizeQuality;" +
	            "}";
	    File cssFile = File.createTempFile("batik-default-override-", ".css");
	    
	    TranscodingHints transcoderHints = new TranscodingHints();
	    transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
	    transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
	            SVGDOMImplementation.getDOMImplementation());
	    transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
	            SVGConstants.SVG_NAMESPACE_URI);
	    transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
	    transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());

	    try {

	        TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));

	        ImageTranscoder t = new ImageTranscoder() {

	            @Override
	            public BufferedImage createImage(int w, int h) {
	                return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	            }

	            @Override
	            public void writeImage(BufferedImage image, TranscoderOutput out)
	                    throws TranscoderException {
	                imagePointer[0] = image;
	            }

	        };
	        t.setTranscodingHints(transcoderHints);
	        t.transcode(input, null);
	    }
	    catch (TranscoderException ex) {
	        // Requires Java 6
	        ex.printStackTrace();
	        throw new IOException("Couldn't convert " + svgFile);
	    }
	    finally {
	        cssFile.delete();
	    }

	    return imagePointer[0];
	}
}
