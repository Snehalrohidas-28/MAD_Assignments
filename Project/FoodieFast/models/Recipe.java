package com.example.foodiefast.models;

public class Recipe {

    public String id;
    public String name;
    public String category;

    public int image;          
    public String imageUrl;   

    public String ingredients;
    public String procedure;
    public String youtubeLink;

    public boolean liked;
    public boolean saved;

   
    public Recipe() {}

 
    public Recipe(String id, String name, String category,
                  int image,
                  String ingredients,
                  String procedure,
                  String youtubeLink) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;

        this.imageUrl = "";
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.youtubeLink = youtubeLink;

        this.liked = false;
        this.saved = false;
    }

  
    public Recipe(String id, String name, String category,
                  String imageUrl,
                  String ingredients,
                  String procedure,
                  String youtubeLink,
                  boolean liked,
                  boolean saved) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.image = 0;

        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.youtubeLink = youtubeLink;

        this.liked = liked;
        this.saved = saved;
    }
}
