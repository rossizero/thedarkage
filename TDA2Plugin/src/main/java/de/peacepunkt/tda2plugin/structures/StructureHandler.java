package de.peacepunkt.tda2plugin.structures;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class StructureHandler<T extends AbstractStructure> {
    String arenaFolderPath;
    public String name;

    public StructureHandler(Class<T> clazz, String arenaFolderPath) {
        this.arenaFolderPath = arenaFolderPath;
        try {
            this.name = (String) clazz.getMethod("getStructureName").invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void saveAll(List<T> list) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        try {
            File file = new File(getPath());
            file.getParentFile().mkdirs();
            file.createNewFile();
            mapper.writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<T> loadAll(Class clazz) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        List<T> ret = null;
        try {
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            if(new File(getPath()).exists())
                ret = mapper.readValue(new File(getPath()),  type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getPath() {
        return arenaFolderPath + "/" + name + "s_new.yml";
    }
}
