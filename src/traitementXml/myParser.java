package traitementXml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ORM.DataSource;

public class myParser extends DefaultHandler{
    DataSource ds;
	private boolean bUser;
	private boolean bpwd;
	private boolean bdriver;
	private boolean bUrl;
	
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		ds=new DataSource();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	
		super.startElement(uri, localName, qName, attributes);
		if(qName.equals("user")) bUser=true;
		if(qName.equals("pwd")) bpwd=true;
		if(qName.equals("driver")) bdriver=true;
		if(qName.equals("url")) bUrl=true;
		
	}

	

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		if(bUrl) {
			//ds.url=new String(ch);
		}
	}

	
	
}
