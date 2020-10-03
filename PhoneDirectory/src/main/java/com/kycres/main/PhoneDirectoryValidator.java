package com.kycres.main;

import java.io.File;

import com.kycres.service.PhoneDirectoryBusiness;

/**
 * The Class PhoneDirectoryValidator.
 */
public class PhoneDirectoryValidator {
	
	/** The Constant YAML_FILE_PATH. */
	private static final String YAML_FILE_PATH = "src/main/resources/userDataModel.yml";
	
	/** The Constant JSON_FILE_PATH. */
	private static final String JSON_FILE_PATH = "src/main/resources/user.json";

	public static void main(String[] args) {
		
		File yamlFile = new File(YAML_FILE_PATH);
		File inputFile = new File(JSON_FILE_PATH);
		if(yamlFile.exists() && inputFile.exists()) {
			System.out.println(new PhoneDirectoryBusiness().validateTheJSON(yamlFile, inputFile));
		}
		else
			System.out.print("Please configure the files in the location");
		}
	}
