package drugiLabILJFB;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Parser {

    public static void stack(String path, String call, String tag, String br) {
        Boolean svi=false;
        Boolean jedan=false;
        int broj=1,num=1;

        if(br.equals("*")) {
            svi=true;
        } else if(Integer.parseInt(br)>0) {
            broj=Integer.parseInt(br);
            num=broj;
        } else {
            System.out.println("Number is not valid!");
            System.exit(0);
        }

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(path));

            check(call, tag, svi, jedan, num, eventReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private static void check(String call, String tag, Boolean svi, Boolean jedan, int num, XMLEventReader eventReader) throws XMLStreamException {
        if (call.equals("ATTRIBUTE")) {
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElement startElement = event.asStartElement();
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    if (attributes.hasNext() && num > 0) {
                        do {
                            String atribut = attributes.next().toString();
                            String firstatr = atribut.substring(0, atribut.indexOf("="));
                            if (firstatr.equals(tag)) {
                                jedan = true;
                                System.out.println(atribut);
                                System.out.println("");
                                if (!svi) num--;
                            }
                        } while (attributes.hasNext() && num > 0);
                    }
                }
            }
            if (!jedan) {
                System.out.println("File does not have any attribute of type: " + tag);
            }
        } else if (call.equals("ELEMENT") || call.equals("TEXT")) {
            while (eventReader.hasNext() && num > 0) {
                XMLEvent event = eventReader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();
                    if (qName.equalsIgnoreCase(tag)) {

                        while (true) {
                            if (call.equals("ELEMENT")) {
                                System.out.print(event);
                            }


                            else if (call.equals("TEXT")) {
                                System.out.print(eventReader.nextEvent());
                            }

                            if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
                                EndElement endElement = event.asEndElement();
                                if (endElement.getName().getLocalPart().equalsIgnoreCase(tag)) {
                                    if (!svi) num--;
                                    jedan=true;
                                    System.out.println("");
                                    break;
                                }
                            }
                            event = eventReader.nextEvent();
                        }
                    }
                }
            }
            if (!jedan) {
                System.out.println("File does not have any element of type: " + tag);
            }
        }
    }

    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the path to XML file: ");

        String path = sc.nextLine();
        File f = new File(path);
        if (!path.endsWith(".xml")) {
            System.out.println("File is not xml!");
            System.exit(0);
        } else if(!f.exists() || f.isDirectory()) {
            System.out.println("File does not exsist!");
            System.exit(0);
        }


        while (true) {
            System.out.println("Enter the task: ");
            String line = sc.nextLine();
            if (line.equals("EXIT")) {
                System.out.println("The program is over.");
                break;
            }

            String[] parts=line.split(" ");
            String tag=parts[1].substring(1, parts[1].length()-1);

            if (line.startsWith("ELEMENT")) stack(path, parts[0], tag, parts[2]);

            else if (line.startsWith("ATTRIBUTE")) stack(path, parts[0], parts[1], parts[2]);

            else if (line.startsWith("TEXT")) stack(path, parts[0], tag, parts[2]);

            else System.out.println("Wrongly entered command!");
        }
        sc.close();
    }
}

