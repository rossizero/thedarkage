package de.peacepunkt.tda2plugin.team;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TeamHandler{
    String arenaFolderPath;
    public String name;

    public TeamHandler(String arenaFolderPath) {
        this.arenaFolderPath = arenaFolderPath;
        this.name = "team";
    }

    public void saveAll(List<Teem> list) {
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

    public List<Teem> loadAll() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        List<Teem> ret = null;
        try {
            File file = new File(getPath());
            System.out.println("Loading teams from file " + getPath());
            if(file.exists()) {
                ret = mapper.readValue(file, new TypeReference<List<Teem>>() {});
                for(Teem teem: ret) {
                    teem.setup();
                }
                return ret;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getPath() {
        return arenaFolderPath + "/" + name + "s_new.yml";
    }
}
