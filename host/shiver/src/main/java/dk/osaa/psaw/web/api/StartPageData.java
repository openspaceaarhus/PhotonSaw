package dk.osaa.psaw.web.api;

import java.util.ArrayList;

import lombok.Data;

@Data
public class StartPageData {		
	String hello;
	
	ArrayList<ArrayList<String>> rows = new ArrayList<>();

}
