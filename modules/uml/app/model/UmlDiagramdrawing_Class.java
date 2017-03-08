package model;

import java.util.ArrayList;
import java.util.Iterator;

public class UmlDiagramdrawing_Class {
	public String name;
	public ArrayList<String> methods;
	public ArrayList<String> attributes;
	
	public UmlDiagramdrawing_Class(String name, ArrayList<String> methods, ArrayList<String> attributes) {
		this.name=name;
		this.methods=methods;
		this.attributes=attributes;
	}

	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public ArrayList<String> getMethods(){
		return this.methods;
	}
	
	public void setMethods(ArrayList<String> methods){
		this.methods=methods;
	}
	
	public ArrayList<String> getAttributes(){
		return this.attributes;
	}
	
	public void setAttributes(ArrayList<String> attributes){
		this.attributes=attributes;
	}
	
	public String getMethodsAsString(){
		String ret="";
		ArrayList<String> s = this.methods;
		java.util.Collections.sort(s);
		for (Iterator iterator = s.iterator(); iterator.hasNext();) {
				ret += (String) iterator.next() +"\n";	
		}
		return ret;
	}
	
	public String getAttributesAsString(){
		String ret="";
		ArrayList<String> s = this.attributes;
		java.util.Collections.sort(s);
		for (Iterator iterator = s.iterator(); iterator.hasNext();) {
			ret += (String) iterator.next()+"\n";	
		}
		return ret;
	}

}
