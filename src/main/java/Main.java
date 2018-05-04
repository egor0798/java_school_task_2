import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        String path;
        ObjectMapper out = new ObjectMapper();
        path = input.nextLine();
        File root = new File(path);
        File src = new File("./data.txt");
        Map<String, FSNode> nodes = new HashMap<>();
        Map<String, FSNode> newNodes = new HashMap<>();
        // если файл с данными уже есть, значит это не первый запуск программы
        if(src.exists()){
            // получаем текущее состояние директории, сравниваем с изначальным и потом удаляем файл с данными
            createArray(root, newNodes);
            nodes = out.readValue(src, new TypeReference<HashMap<String, FSNode>>() {});
            check(nodes, newNodes, src);
            src.delete();     // стираем файл после второго запуска программы
        }
        else {
            createArray(root, nodes);
            out.writerWithDefaultPrettyPrinter().writeValue(src, nodes); // сериализуем map в json и сохраняем в data.txt
        }
    }

    private static void createArray(File root, Map<String, FSNode> nodes){
        String[] arr = root.list();
        for (String node : arr) {
            File f = new File(root.getPath()+ "/" + node);
            if(f.isDirectory())
                createArray(f, nodes);
            long ctime = Long.MAX_VALUE;    //время создания нужно для определения перемещения файла
            try {
                Path path = f.toPath();
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                ctime = attributes.creationTime().to(TimeUnit.MILLISECONDS);
            }catch (IOException e){
                System.out.println("couldn't get ctime");
            }
            nodes.put(f.getPath(), new FSNode(f.getPath(), f.lastModified(), ctime, f.isDirectory()));
        }
    }

    private static void check(Map<String, FSNode> nodes, Map<String, FSNode> newNodes, File src) {
        List<FSNode> removed = new ArrayList<>();
        for (Map.Entry<String, FSNode> entry : nodes.entrySet()) {
            if (newNodes.get(entry.getKey()) == null){
                removed.add(entry.getValue());
            } else {
                if (newNodes.get(entry.getKey()).getLast_modified() != entry.getValue().getLast_modified()) {
                    System.out.println("Modified " + entry.getKey());       // проверяю изменение времени модификации
                }
                newNodes.remove(entry.getKey());        // при удалении файлов, которые были изменены из новой мапы,
                // в ней останутся только созданные и перемещённые файлы
            }
        }

        // в листе removed лежат файлы, которых нет на прежнем месте,
        // они могут быть как удалены, так и перемещены
        // код ниже убирает из листа файлы, которые были перемещены
        List<FSNode> needToRemove = new ArrayList<>();
        for (Map.Entry<String, FSNode> entry : newNodes.entrySet()) {
            if (entry.getValue().getCreated() < src.lastModified()){
                System.out.println("Moved " + entry.getKey());
                for (FSNode node : removed)
                    if(entry.getValue().getPath().contains(node.getPath().
                            substring(node.getPath().lastIndexOf("/"), node.getPath().length())))
                        needToRemove.add(node);
            }
            else // если файл остался в мапе newNode, значит этот файл был создан между запусками программы
                System.out.println("Created " + entry.getKey());
        }
        removed.removeAll(needToRemove);
        for (FSNode node : removed)
            System.out.println("Deleted " + node.getPath());
    }

}