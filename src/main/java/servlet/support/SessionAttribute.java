/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.support;

import java.util.HashMap;

/**
 *
 * @author itu
 */
public class SessionAttribute {

	private HashMap<String, Object> data;
	
	public SessionAttribute() {
        // Initialisation du HashMap si nécessaire
        data = new HashMap<>();
    }

    // Getter pour la propriété data
    public HashMap<String, Object> getData() {
        return data;
    }

    // Setter pour la propriété data
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    /**
     * Adds a key-value pair to the session attribute.
     *
     * @param key The key to associate the value with.
     * @param value The value to store.
     */
   public void add(String key, Object value) {
       data.put(key, value);
   }

   /**
     * Retrieves the value associated with the given key.
     *
     * @param key The key to retrieve the value for.
     * @return The value associated with the key, or null if not found.
     */
   public Object get(String key) {
      return data.get(key);
   }

   /**
     * Removes the key-value pair associated with the given key.
     *
     * @param key The key to remove the value for.
     * @return The value that was removed, or null if the key was not found.
     */
   public void remove(String key) {
      data.remove(key);
   }

    
}
