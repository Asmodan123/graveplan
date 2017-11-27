package de.hannisoft.gaveplan.properties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import de.hannisoft.gaveplan.model.ElementType;
import de.hannisoft.gaveplan.model.PlanElement;

public class ElementsReader {
    public Map<Integer, PlanElement> readElements() throws IOException {
        Map<Integer, PlanElement> elements = new HashMap<>();
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("plan/elements.properties"));
        for (Entry<Object, Object> prop : props.entrySet()) {
            try {
                int elementId = Integer.parseInt(prop.getKey().toString());
                String[] values = (prop.getValue().toString()).split("\\,");
                String type = values[0];
                PlanElement pEelement = new PlanElement(elementId, type);
                pEelement.setMinRow(Integer.parseInt(values[1]));
                pEelement.setMaxRow(Integer.parseInt(values[2]));
                pEelement.setMinPlace(Integer.parseInt(values[3]));
                pEelement.setMaxPlace(Integer.parseInt(values[4]));
                if (values.length > 5) {
                    pEelement.setName(values[5]);
                }

                elements.put(elementId, pEelement);
            } catch (Exception e) {
                System.err.println("Can't read ElementProperty: '" + prop.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        return elements;
    }

    public Map<String, PlanElement> readElements(ElementType type) throws IOException {
        Map<String, PlanElement> typeElements = new HashMap<>();
        for (PlanElement element : readElements().values()) {
            if (type.equals(element.getType())) {
                typeElements.put(element.getName(), element);
            }
        }
        return typeElements;

    }
}
