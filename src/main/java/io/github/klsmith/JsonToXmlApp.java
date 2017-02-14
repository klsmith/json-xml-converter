package io.github.klsmith;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class JsonToXmlApp {

    // constants
    public static final String DEFAULT_INPUT = "/json-to-xml/input.txt";
    public static final String DEFAULT_OUTPUT = "/json-to-xml/output.xml";
    public static final String DEFAULT_ROOT = "root";
    public static final boolean DEFAULT_CUSTOM_HANDLING = false;

    // variables
    private static String input = DEFAULT_INPUT;
    private static String output = DEFAULT_OUTPUT;
    private static String root = DEFAULT_ROOT;
    private static boolean customHandling = DEFAULT_CUSTOM_HANDLING;

    public static void main(String[] args) throws IOException {

        // args should never be null
        args = args == null ? new String[] {} : args;

        // assign and print the arguments
        System.out.println("input=" + (input = args.length >= 1 ? args[0] : input));
        System.out.println("output=" + (output = args.length >= 2 ? args[1] : output));
        System.out.println("root=" + (root = args.length >= 3 ? args[2] : root));
        System.out.println("customHandling="
                + (customHandling = args.length >= 4 ? Boolean.parseBoolean(args[3]) : customHandling));

        // read in the json from a file
        final String json = new String(Files.readAllBytes(Paths.get(input)));

        // convert the json to xml
        final String xml = convertJsonToXml(json);

        // write the xml to a file
        Files.write(Paths.get(output), xml.getBytes());

    }

    public static String convertJsonToXml(String json) {

        // if json string is null then create an empty JSONObject, else parse
        // the string
        JSONObject jsonObject = json == null ? new JSONObject() : new JSONObject(json);

        // if customHandling argument is set then do the custom handling, else
        // do nothing
        jsonObject = customHandling ? customHandling(jsonObject) : jsonObject;

        // print the handled(?) jsonObject to the console
        System.out.println("json=" + jsonObject.toString(4));

        // convert jsonObject to XML string and wrap it in the value of 'root'
        return XML.toString(jsonObject, root);

    }

    public static JSONObject customHandling(JSONObject jsonIn) {
        // This method picks apart the jsonObject, restructures it
        // and only works for 1 very specific JSON input.
        // This was made for converting a specific json file
        // into a working xml config format.
        final JSONArray arrayOut = new JSONArray();
        final JSONObject tempOut = new JSONObject()//
                .put("rule", arrayOut);
        final JSONObject jsonOut = new JSONObject()//
                .put("name", "MG way")//
                .put("language", "java")//
                .put("rules", tempOut);
        final JSONArray arrayIn = jsonIn//
                .getJSONObject("rules")//
                .getJSONArray("rule");
        for (int i = 0; i < arrayIn.length(); i++) {
            final JSONObject objIn = arrayIn.getJSONObject(i);
            if (!objIn.has("lang") || !"java".equals(objIn.getString("lang"))) {
                System.out.println(objIn + " is not a java rule");
                continue;
            }
            final JSONObject objOut = new JSONObject();
            copy(objIn, objOut, "key");
            copy(objIn, objOut, "name");
            copy(objIn, objOut, "type");
            copy(objIn, objOut, "severity");
            copy(objIn, objOut, "defaultDebtRemFnOffset");
            copy(objIn, objOut, "htmlDesc");
            arrayOut.put(objOut);
        }
        return jsonOut;
    }

    private static void copy(JSONObject a, JSONObject b, String key) {
        // if a, b, or the key are null then exit the method
        // also, if a doesn't have the key then exit the method
        if (a == null || b == null || key == null || !a.has(key)) {
            return;
        }
        // perform the actual copy from a to b
        b.put(key, a.get(key));
    }
}
