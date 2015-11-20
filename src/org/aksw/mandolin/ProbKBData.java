package org.aksw.mandolin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.aksw.mandolin.NameMapperProbKB.Type;

import com.opencsv.CSVWriter;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class ProbKBData {
	
	private final static int ENT_LENGTH = Type.ENTITY.name().length();
	private final static int CLS_LENGTH = Type.CLASS.name().length();
	private final static int REL_LENGTH = Type.RELATION.name().length();
	
	private static String base;
	private static NameMapperProbKB map;
	
	public static void buildCSV(NameMapperProbKB theMap, String theBase) throws IOException {
		
		base = theBase;
		map = theMap;
		
		allNodes();
		
		entClasses();
		relClasses();
		relationships();
		functionals();
		
	}
	
	
	private static void functionals() throws IOException {
		
		CSVWriter writer = new CSVWriter(new FileWriter(new File(base + "/relations.csv"))); 
		
		// TODO
		
		writer.close();

	}


	private static void allNodes() throws IOException {
		
		CSVWriter entWriter = new CSVWriter(new FileWriter(new File(base + "/entities.csv"))); 
		CSVWriter clsWriter = new CSVWriter(new FileWriter(new File(base + "/classes.csv"))); 
		CSVWriter relWriter = new CSVWriter(new FileWriter(new File(base + "/relations.csv"))); 
		
		HashMap<String, String> hmap = map.getNamesToURIs();
		
		for(String key : hmap.keySet()) {
			String id = "";
			if(key.startsWith(Type.ENTITY.name())) {
				id = key.substring(ENT_LENGTH);
				entWriter.writeNext(new String[] {id, hmap.get(key)});
			}
			if(key.startsWith(Type.CLASS.name())) {
				id = key.substring(CLS_LENGTH);
				clsWriter.writeNext(new String[] {id, hmap.get(key)});
			}
			if(key.startsWith(Type.RELATION.name())) {
				id = key.substring(REL_LENGTH);
				relWriter.writeNext(new String[] {id, hmap.get(key)});
			}
		}
		
		relWriter.close();
		clsWriter.close();
		entWriter.close();

	}


	private static void entClasses() throws IOException {
		
		CSVWriter writer = new CSVWriter(new FileWriter(new File(base + "/entClasses.csv"))); 
		
		for(String line : map.getEntClasses()) {
			String[] arr = line.split("#");
			// entity_id+"|"+class_id
			String id1 = arr[0].substring(ENT_LENGTH);
			String id2 = arr[1].substring(CLS_LENGTH);
			writer.writeNext(new String[] {id1, id2});
		}
		
		writer.close();
		
	}
	
	
	private static void relClasses() throws IOException {
		
		CSVWriter writer = new CSVWriter(new FileWriter(new File(base + "/relClasses.csv"))); 
		
		for(String line : map.getRelClasses()) {
			String[] arr = line.split("#");
			// class_id+"|"+class_id
			String id1 = arr[0].substring(CLS_LENGTH);
			String id2 = arr[1].substring(CLS_LENGTH);
			writer.writeNext(new String[] {id1, id2});
		}
		
		writer.close();
		
	}

	
	private static void relationships() throws IOException {
		
		CSVWriter writer = new CSVWriter(new FileWriter(new File(base + "/relationships.csv"))); 
		
		Iterator<String> it = map.getRelationships().iterator();
		for(int i=0; it.hasNext(); i++) {
			String line = it.next();
			String[] arr = line.split("#");
			// relation_id+"|"+entity_id+"|"+entity_id
			String id1 = arr[0].substring(REL_LENGTH);
			String id2 = arr[1].substring(ENT_LENGTH);
			String id3 = arr[2].substring(ENT_LENGTH);
			
			// TODO get class IDs...
			writer.writeNext(new String[] {"" + i, id1, id2, "0", id3, "0", "1.0"});
		}
		
		writer.close();
		
	}


}