import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class Main {

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson (List<Employee> list){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();

        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String path){

        try (FileWriter file = new FileWriter(path)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseXml(String path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
//        try {
//            Document doc = builder.parse(new File(path));
//            Node root = doc.getDocumentElement();
//            System.out.println("Корневой элемент: " + root.getNodeName());
//            readNode(root);
//        } catch (SAXException | IOException e) {
//            throw new RuntimeException(e);
//        }
        Document doc = builder.parse(new File(path));
        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент: " + root.getNodeName());
        readNode(root);
    }

    public static void readNode(Node node){
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                System.out.println("Текущий узел: " + node_.getNodeName());

                Element element = (Element) node_;

                NamedNodeMap map = element.getAttributes();
                for (int j = 0; j < map.getLength(); j++) {
                    String attrName = map.item(j).getNodeName();
                    String attrValue = map.item(j).getNodeValue();
                    System.out.println("Атрибут: " + attrName + "; значение: " + attrValue);
                }
                readNode(node_);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);

        writeString(json, "data.json");

        parseXml("data.xml");
    }

}
