package model;

import java.util.Arrays;

public class UmlClassselection_Class {
	public String name;
	public String[] methods;
	public String[] attributes;
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String[] getMethods(){
		return this.methods;
	}
	
	public void setMethods(String[] methods){
		this.methods=methods;
	}
	
	public String[] getAttributes(){
		return this.attributes;
	}
	
	public void setAttributes(String[] attributes){
		this.attributes=attributes;
	}
	
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
	
}
