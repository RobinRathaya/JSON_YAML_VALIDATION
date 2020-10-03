package com.kycres.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class PhoneDirectoryBusiness {
	
	public List<String> validateTheJSON(File yamlFile, File inputFile){
		String yamlJsonStr ;
		List<String> valMessages = new ArrayList<String>();
		try {
			yamlJsonStr = convertYAMLToJSON(yamlFile);
			valMessages = dataModelValidation(inputFile,yamlJsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return valMessages;
	}

	/**
	 * Convert YAML to JSON.
	 *
	 * @return the JSON object
	 */
	private String convertYAMLToJSON(File yamlFile) {
		
		ObjectMapper objectMapper;
		Object userModel;
		ObjectMapper jsonWriter = new ObjectMapper();
		JSONObject dataModelJSON = new JSONObject();
		JSONObject dataModel = new JSONObject();
		HashMap<String, Object> propertiesMap = new HashMap<>();
		JSONArray required = new JSONArray();
		try {
			objectMapper = new ObjectMapper(new YAMLFactory());
			userModel = objectMapper.readValue(yamlFile,Object.class);
			dataModelJSON = new JSONObject(jsonWriter.writeValueAsString(userModel));
			
			dataModel.put("$schema", "http://json-schema.org/draft-04/schema#");
			Iterator<?> iterator = dataModelJSON.keys();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Object value = dataModelJSON.get(key.toString());
				
				if("entity".equalsIgnoreCase(key.toString())) {
					dataModel.put("title", value);
					dataModel.put("type", "object");
				}else if("attribute".equalsIgnoreCase(key.toString())) {
					for (int i = 0; i < ((JSONArray) value).length(); i++) {
						JSONObject jsonObject = ((JSONArray) value).getJSONObject(i);
						buildPropertiesArray(propertiesMap,jsonObject);
						if(jsonObject.has("Mandatory") && "YES" .equalsIgnoreCase(jsonObject.getString("Mandatory"))) {
							required.put(jsonObject.get("name"));
						}
					}
					dataModel.put("properties", propertiesMap);
					dataModel.put("required", required);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataModel.toString();
	}
	
	/**
	 * Data model validation.
	 *
	 * @param dataModel the data model
	 * @return 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> dataModelValidation(File inputFile,String dataModel) throws IOException{
		List<String> result = new ArrayList<String>();
		try (InputStream jsonStream = FileUtils.openInputStream(inputFile))
            {
               JSONObject rawSchema = new JSONObject(dataModel);
               Schema schema = SchemaLoader.load(rawSchema);
               schema.validate(new JSONObject(new JSONTokener(jsonStream)));
               result.add("SUCCESS");
           }catch (ValidationException val) {
              result.addAll(val.getAllMessages());
              return result;
           }
		return result;
	}
	
	/**
	 * Builds the properties array.
	 *
	 * @param propertiesMap the properties map
	 * @param jsonObject the json object
	 */
	private void buildPropertiesArray(Map<String,Object> propertiesMap,JSONObject jsonObject) {
		JSONObject fieldJson = new JSONObject();
		if(jsonObject.has("type"))
			fieldJson.put("type", jsonObject.get("type").toString().toLowerCase());
		if(jsonObject.has("Maxlength"))
			fieldJson.put("maxLength", jsonObject.getInt("Maxlength"));
		if(jsonObject.has("Minvalue"))
			fieldJson.put("minimum", jsonObject.getInt("Minvalue"));
		if(jsonObject.has("MaxValue"))
			fieldJson.put("maximum", jsonObject.getInt("MaxValue"));
		if(jsonObject.has("name"))
			propertiesMap.put(jsonObject.getString("name"), fieldJson);
	}
}
