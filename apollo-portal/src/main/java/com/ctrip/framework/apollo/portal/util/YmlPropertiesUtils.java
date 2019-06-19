package com.ctrip.framework.apollo.portal.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TJ on 2019/6/19.
 */
public class YmlPropertiesUtils {

//    public static void main(String[] args) {
//        YmlPropertiesUtils.properties2YmlByContent("app.id=100003173\n" +
//                "jdkVersion=1.8\n" +
//                "apollo.meta=http://47.98.55.111:8080/");
//    }

    private static final String ENCODING = "utf-8";

    public static void yml2Properties(String path) {
        final String DOT = ".";
        List<String> lines = new LinkedList<>();
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLParser parser = yamlFactory.createParser(new InputStreamReader(new FileInputStream(path), Charset.forName(ENCODING)));

            String key = "";
            String value = null;
            JsonToken token = parser.nextToken();
            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    // do nothing
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    if (key.length() > 0) {
                        key = key + DOT;
                    }
                    key = key + parser.getCurrentName();

                    token = parser.nextToken();
                    if (JsonToken.START_OBJECT.equals(token)) {
                        continue;
                    }
                    value = parser.getText();
                    lines.add(key + "=" + value);

                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = key.substring(0, dotOffset);
                    }
                    value = null;
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = key.substring(0, dotOffset);
                    } else {
                        key = "";
                        lines.add("");
                    }
                }
                token = parser.nextToken();
            }
            parser.close();

            System.out.println(lines);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void properties2YmlByFilePath(String path) {
        JsonParser parser;
        JavaPropsFactory factory = new JavaPropsFactory();
        try {
            parser = factory.createParser(
                    new InputStreamReader(new FileInputStream(path), Charset.forName(ENCODING)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLGenerator generator = yamlFactory.createGenerator(new OutputStreamWriter(new FileOutputStream(path), Charset.forName(ENCODING)));
            properties2Yml(parser, generator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将properties的内容转换为yml格式的内容
     * @param content 要转换的内容
     * @return
     */
    public static String properties2YmlByContent(String content) {
        JsonParser parser;
        JavaPropsFactory factory = new JavaPropsFactory();
        String ymlStr;
        try {
            parser = factory.createParser(content);
            YAMLFactory yamlFactory = new YAMLFactory();
            OutputStream outputStream = new ByteArrayOutputStream();
            YAMLGenerator generator = yamlFactory.createGenerator(outputStream);
            properties2Yml(parser, generator);
            ymlStr = outputStream.toString();
            if (ymlStr.startsWith("---\n")) {
                ymlStr = ymlStr.substring(4);
            }
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ymlStr;
    }

    private static void properties2Yml(JsonParser parser, YAMLGenerator generator) {
        try {
            JsonToken token = parser.nextToken();
            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    generator.writeStartObject();
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    generator.writeFieldName(parser.getCurrentName());
                } else if (JsonToken.VALUE_STRING.equals(token)) {
                    generator.writeString(parser.getText());
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    generator.writeEndObject();
                }
                token = parser.nextToken();
            }
            parser.close();
            generator.flush();
            generator.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
