package com.example.foodiefast.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodiefast.R;
import com.example.foodiefast.activities.RecipeDetailActivity;
import com.example.foodiefast.database.DBHelper;
import com.example.foodiefast.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends BaseAdapter {

    private Activity context;
    private List<Recipe> list;
    private DBHelper dbHelper;

    public RecipeAdapter(Activity context, List<Recipe> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_recipe, parent, false);
        }

        ImageView img = convertView.findViewById(R.id.imgRecipe);
        TextView title = convertView.findViewById(R.id.tvTitle);
        ImageView like = convertView.findViewById(R.id.btnLike);
        ImageView save = convertView.findViewById(R.id.btnSave);
        ImageView share = convertView.findViewById(R.id.btnShare);

        Recipe recipe = list.get(position);

        title.setText(recipe.name);


        if (recipe.imageUrl != null && !recipe.imageUrl.trim().isEmpty()) {

            if (recipe.imageUrl.startsWith("http")) {
                // 🌐 Load from internet
                Picasso.get()
                        .load(recipe.imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(img);

            } else {

                try {
                    img.setImageURI(Uri.parse(recipe.imageUrl));
                } catch (Exception e) {
                    img.setImageResource(R.drawable.ic_launcher_background);
                }
            }

        } else if (recipe.image != 0) {
            img.setImageResource(recipe.image);
        } else {
            img.setImageResource(R.drawable.ic_launcher_background);
        }


        recipe.liked = dbHelper.isLiked(recipe.id);
        recipe.saved = dbHelper.isSaved(recipe.id);


        like.setImageResource(recipe.liked ?
                R.drawable.ic_heart_filled :
                R.drawable.ic_heart_outline);


        save.setImageResource(recipe.saved ?
                R.drawable.ic_bookmark_filled :
                R.drawable.ic_bookmark_outline);


        like.setOnClickListener(v -> {

            recipe.liked = !recipe.liked;

            if (recipe.liked) {
                dbHelper.likeRecipe(recipe.id);
            } else {
                dbHelper.unlikeRecipe(recipe.id);
            }

            notifyDataSetChanged();
            animate(v);
        });


        save.setOnClickListener(v -> {

            recipe.saved = !recipe.saved;

            if (recipe.saved) {
                dbHelper.saveRecipe(recipe.id);
            } else {
                dbHelper.unsaveRecipe(recipe.id);
            }

            notifyDataSetChanged();
            animate(v);
        });


        share.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,
                    recipe.name + "\n\n" +
                            recipe.ingredients + "\n\n" +
                            recipe.procedure);

            context.startActivity(Intent.createChooser(intent, "Share via"));
        });

        convertView.setOnClickListener(v -> {

            Intent intent = new Intent(context, RecipeDetailActivity.class);

            intent.putExtra("id", recipe.id);
            intent.putExtra("position", position);
            intent.putExtra("name", recipe.name);
            intent.putExtra("image", recipe.image);
            intent.putExtra("imageUrl", recipe.imageUrl);
            intent.putExtra("ingredients", recipe.ingredients);
            intent.putExtra("procedure", recipe.procedure);
            intent.putExtra("youtube", recipe.youtubeLink);
            intent.putExtra("liked", recipe.liked);
            intent.putExtra("saved", recipe.saved);

            context.startActivityForResult(intent, 100);
        });

        return convertView;
    }

    private void animate(View v) {
        ScaleAnimation scale = new ScaleAnimation(
                0.8f, 1.2f,
                0.8f, 1.2f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(200);
        scale.setRepeatCount(1);
        scale.setRepeatMode(ScaleAnimation.REVERSE);
        v.startAnimation(scale);
    }
}
