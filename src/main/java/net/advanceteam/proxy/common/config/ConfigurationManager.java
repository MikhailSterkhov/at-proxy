package net.advanceteam.proxy.common.config;

import com.google.common.base.Charsets;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigurationManager {


    private final ThreadLocal<Yaml> yaml = ThreadLocal.withInitial(() -> {
        Representer representer = new Representer() {
            { representers.put(FileConfiguration.class,
                        data -> represent( ((FileConfiguration) data).getSelf()) ); }
        };

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return new Yaml(new Constructor(), representer, options);
    });


    public void save(FileConfiguration config, File file)
            throws IOException {

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            save(config, writer);
        }
    }


    public void save(FileConfiguration config, Writer writer) {
        yaml.get().dump(config.getSelf(), writer);
    }


    public FileConfiguration load(File file) throws IOException {
        return load(file, null);
    }


    public FileConfiguration load(File file, FileConfiguration defaults)
            throws IOException {

        try (FileInputStream is = new FileInputStream(file)) {
            return load(is, defaults);
        }
    }


    public FileConfiguration load(Reader reader) {
        return load(reader, null);
    }


    public FileConfiguration load(Reader reader, FileConfiguration defaults) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);

        if (map == null) {
            map = new LinkedHashMap<>();
        }

        return new FileConfiguration(map, defaults);
    }


    public FileConfiguration load(InputStream is) {
        return load(is, null);
    }


    public FileConfiguration load(InputStream is, FileConfiguration defaults) {
        Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);

        if (map == null) {
            map = new LinkedHashMap<>();
        }

        return new FileConfiguration(map, defaults);
    }


    public FileConfiguration load(String string) {
        return load(string, null);
    }


    public FileConfiguration load(String string, FileConfiguration defaults) {
        Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);

        if (map == null) {
            map = new LinkedHashMap<>();
        }

        return new FileConfiguration(map, defaults);
    }
}
