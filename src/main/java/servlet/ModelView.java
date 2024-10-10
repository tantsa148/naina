package servlet;

import java.util.HashMap;

/**
 *
 * @author itu
 */
public class ModelView {
    
    // URL qui renvoie vers le path de la vue
    private String url;
    private HashMap<String, Object> data;
    
    public ModelView() {
        // Initialisation du HashMap si nécessaire
        data = new HashMap<>();
    }
    
    // Méthode pour ajouter un objet au HashMap data
    public void addObject(String key, Object value) {
        if (key!= null && value!= null) {
            data.put(key, value);
        } else {
            System.out.println("Erreur : clé ou valeur nulle lors de l'ajout d'un objet.");
        }
    }

    // Getter pour la propriété url
    public String getUrl() {
        return url;
    }

    // Setter pour la propriété url
    public void setUrl(String url) {
        this.url = url;
    }

    // Getter pour la propriété data
    public HashMap<String, Object> getData() {
        return data;
    }

    // Setter pour la propriété data
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
