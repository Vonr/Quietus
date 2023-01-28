package dev.qther.quietus;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Config {
    public final File CONFIG_FILE;
    private final Gson GSON;

    public boolean enabled = true;
    public float percentage = 0.8f;
    public int maxLevels = -1;

    public Config() {
        CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().resolve("quietus.json").toAbsolutePath().toString().replace("/./", "/"));
        GSON = new GsonBuilder().setPrettyPrinting().create();

        load();
        save();
    }

    public void load() {
        Quietus.LOGGER.info("Reading from configuration file.");

        if (!CONFIG_FILE.exists() || CONFIG_FILE.length() == 0) save();
        try {
            JsonObject jo = GSON.fromJson(new FileReader(CONFIG_FILE), JsonObject.class);

            for (Field f : this.getClass().getDeclaredFields()) {
                try {
                    if ((f.getModifiers() & Modifier.FINAL) != 0) {
                        continue;
                    }
                    Class type = f.getType();

                    JsonElement je = jo.get(f.getName());
                    if (je == null) {
                        Quietus.LOGGER.warn("Missing configuration value for " + f.getName());
                        continue;
                    }

                    if (float.class.equals(type)) {
                        f.set(this, je.getAsFloat());
                    } else if (int.class.equals(type)) {
                        f.set(this, je.getAsInt());
                    } else if (boolean.class.equals(type)) {
                        f.set(this, je.getAsBoolean());
                    } else if (String.class.equals(type)) {
                        f.set(this, je.getAsString());
                    } else {
                        Quietus.LOGGER.warn("Unknown type " + type + " for field " + f.getName());
                    }
                } catch (IllegalAccessException e) {
                    Quietus.LOGGER.error(e.getMessage());
                }
            }
        } catch (FileNotFoundException ex) {
            Quietus.LOGGER.trace("Couldn't load configuration file", ex);
        }
        Quietus.LOGGER.info("Finished reading from configuration file");
    }

    public void save() {
        Quietus.LOGGER.info("Saving to configuration file");
        try {
            if (!CONFIG_FILE.exists()) {
                File parent = CONFIG_FILE.getParentFile();
                if (!parent.exists()) {
                    if (!CONFIG_FILE.getParentFile().mkdirs()) {
                        Quietus.LOGGER.error("Failed to create config directory");
                        return;
                    }
                }
                if (!CONFIG_FILE.createNewFile()) {
                    Quietus.LOGGER.error("Failed to create config file");
                    return;
                }
            }

            JsonObject jo = new JsonObject();

            for (Field f : this.getClass().getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.FINAL) != 0) {
                    continue;
                }
                Class type = f.getType();
                String name = f.getName();

                try {
                    if (float.class.equals(type)) {
                        jo.add(name, new JsonPrimitive(f.getFloat(this)));
                    } else if (int.class.equals(type)) {
                        jo.add(name, new JsonPrimitive(f.getInt(this)));
                    } else if (boolean.class.equals(type)) {
                        jo.add(name, new JsonPrimitive(f.getBoolean(this)));
                    } else if (String.class.equals(type)) {
                        jo.add(name, new JsonPrimitive((String) f.get(this)));
                    }
                } catch (IllegalAccessException e) {
                    Quietus.LOGGER.error(e.getMessage());
                }
            }

            PrintWriter printwriter = new PrintWriter(new FileWriter(CONFIG_FILE));
            printwriter.print(GSON.toJson(jo));
            printwriter.close();
        } catch (IOException ex) {
            Quietus.LOGGER.trace("Couldn't save to configuration file", ex);
        }
        Quietus.LOGGER.info("Saved to configuration file");
    }
}
