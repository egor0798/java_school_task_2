import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        String path;
        ObjectMapper out = new ObjectMapper();
        path = input.nextLine();
        File root = new File(path);
        File src = new File("./data.txt");
        Map<String, FSNode> nodes = new HashMap<String, FSNode>();
        Map<String, FSNode> newNodes = new  HashMap<String, FSNode>();
        if(src.exists()){
            createArray(root, newNodes);
//            TypeReference<HashMap<String, FSNode>> typeReference = new TypeReference<HashMap<String, FSNode>>() {};
            TypeFactory typeFactory = out.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, FSNode.class);
            nodes = out.readValue(src, mapType);
            check(nodes, newNodes);


        }
        else {
            createArray(root, nodes);
            out.writerWithDefaultPrettyPrinter().writeValue(src, nodes);
        }
    }

    private static void createArray(File root, Map<String, FSNode> nodes){
        String[] arr = root.list();
        for (String node : arr) {
            File f = new File(root.getPath()+ "/" + node);
            if(f.isDirectory())
                createArray(f, nodes);
            nodes.put(f.getPath(), new FSNode(f.getPath(), f.lastModified(), f.isDirectory()));
        }
    }

    public static void check(Map<String, FSNode> nodes, Map<String, FSNode> newNodes) {
        for (Map.Entry<String, FSNode> entry : nodes.entrySet()) {
            if (newNodes.get(entry.getKey()) == null){
                System.out.println("Deleted " + entry.getKey());
            } else {
                if (newNodes.get(entry.getKey()).getLast_modified() != entry.getValue().getLast_modified()) {
                    System.out.println("Modified " + entry.getKey());
                }
                newNodes.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, FSNode> entry : newNodes.entrySet()) {
            System.out.println("Created " + entry.getKey());
        }
    }

}



/*
метод, который проверяет файл это или директория.
Если это файл, то сохранить путь и время модификации
Если это директория, то сделать вышеперечисленное и ещё вызвать рекурсивно.
сразу же сериализовать в JSON и записывать в файл
Далее программа вызывается снова, проверяем, что нам передали тот же путь
Из json файла формируем массив типа File потом формируем массив типа File для текущей директории. Каким-то образом сравниваем файлы.
 */