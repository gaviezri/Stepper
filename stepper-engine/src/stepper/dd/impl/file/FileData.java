package stepper.dd.impl.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileData extends FileDataDefinition{
    File file;
    String filePath;

    public FileData(String path) {
        file = new File(path);
        filePath = path;
    }
    public boolean exists(){
        return file.exists();
    }
    public String getName(){
        return file.getName();
    }
    public String getPath(){
        return file.getPath();
    }
    public String getAbsolutePath(){
        return file.getAbsolutePath();
    }
    public String getExtension(){
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
    public String getContent(){

         try (BufferedReader br = new BufferedReader(new FileReader(file))) {

             StringBuilder sb = new StringBuilder();
             String line;
             do {
                 line = br.readLine();
                 if (line == null) break;
                 sb.append(line);
                 sb.append(System.lineSeparator());
             } while (line != null);
            return sb.toString();
        }
         catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
