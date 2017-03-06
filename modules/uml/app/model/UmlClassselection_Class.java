package model;

import java.util.ArrayList;

public class UmlClassselection_Class {
	public String name;
	public ArrayList<String> methods;
	public ArrayList<String> attributes;
	
	public UmlClassselection_Class(String name, ArrayList<String> methods, ArrayList<String> attributes) {
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
	/*
	public String getMethodsAsString(){
		String[] s = this.methods;
		Arrays.sort(s);
		String ret="";
		for (int i = 0; i < s.length; i++) {
			ret+=s[i]+"\n";
		}
		return ret;
	}
	
	public String getAttributesAsString(){
		String[] s = this.attributes;
		Arrays.sort(s);
		String ret="";
		for (int i = 0; i < s.length; i++) {
			ret+=s[i]+"\n";
		}
		return ret;
	}
*/	
}
