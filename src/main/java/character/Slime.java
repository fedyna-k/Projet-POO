package character;

import geometry.Vector2D;

// Classe représentant un petit monstre Slime dans le jeu
public class Slime extends Entity{
    // Nom du Slime
    private String nom;

    // Points de vie actuels du Slime
    private int pointDeVie;

    // Force d'attaque du Slime
    private int force;

    // Constructeur pour initialiser un Slime avec un nom, des points de vie et une force donnés
    public Slime(String nom, int pointDeVie, int force) {
        this.nom = nom;
        this.pointDeVie = pointDeVie;
        this.force = force;
    }

    // Méthodes d'accès pour obtenir et définir le nom du Slime
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Méthodes d'accès pour obtenir et définir les points de vie du Slime
    public int getPointDeVie() {
        return pointDeVie;
    }

    public void setPointDeVie(int pointDeVie) {
        this.pointDeVie = pointDeVie;
    }

    // Méthodes d'accès pour obtenir et définir la force d'attaque du Slime
    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    // Méthode pour simuler l'attaque du Slime
    public void attaquer() {
        System.out.println("Le Slime attaque avec une force de " + force);
    }

    // Méthode pour simuler les dégâts reçus par le Slime et gérer son état en conséquence
    public void recevoirDegats(int degats) {
        pointDeVie -= degats;
        if (pointDeVie <= 0) {
            System.out.println("Le Slime est vaincu !");
        } else {
            System.out.println("Le Slime a maintenant " + pointDeVie + " points de vie");
        }
    }

    // Méthode abstraite à implémenter pour obtenir le décalage du Slime
    @Override
    public Vector2D getOffset() {
        return null;
    }
}
