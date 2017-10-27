package model.mindmap.basics;

/**
 * Container class which holds all information needed to display images inside
 * MindManager nodes.
 * 
 * @author Magnus Lechner
 *
 */
public class Image {

	private double height;
	private double width;
	private String type;
	private String base64;

	/**
	 * The height of the image displayed inside a node of a MindManager map.
	 * 
	 * @return the height of the image
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * The height of the image displayed inside a node of a MindManager map.
	 * 
	 * @param height The specific height of the image
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * The width of the image displayed inside a node of a MindManager map.
	 * 
	 * @return the width of the image
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * The width of the image displayed inside a node of a MindManager map.
	 * 
	 * @param width The specific width of the image
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * The type of the image. This is the value of the CustomImageType attribute
	 * and differs from "normal" types. As example one type is "public.jpeg".
	 * 
	 * @return the MindManager specific type of the image as String
	 */
	public String getType() {
		return type;
	}

	/**
	 * The type of the image. This is the value of the CustomImageType attribute
	 * and differs from "normal" types. As example one type is "public.jpeg". 
	 * 
	 * @param type The MindManager specific type of the image as String
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The image encoded by base64. MindManager is able to decode this by itself.
	 * 
	 * @return The encoded image.
	 */
	public String getBase64() {
		return base64;
	}

	/**
	 * The image encoded by base64 and represented as String. 
	 * 
	 * @param base64 The encoded image
	 */
	public void setBase64(String base64) {
		this.base64 = base64;
	}
}
