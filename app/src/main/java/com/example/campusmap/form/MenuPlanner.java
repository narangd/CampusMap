package com.example.campusmap.form;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.campusmap.R;

import org.jsoup.helper.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuPlanner implements Serializable {
//    private static final String[] HEADER = {
//            "조식", "중식", "석식", "교직원"
//    };
    private String date;
    private ArrayList<Meal> meals = new ArrayList<>();

    public MenuPlanner(String date) {
        //breakfast lunch dinner
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void addMeal(int index, String header, String ... menus) {
        meals.add(new Meal(header, menus));
    }

    public List<Meal> getMeals() {
        return meals;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("날짜 : ").append(date).append("\n");
        for (Meal meal : meals) {
            builder.append(meal.toString()).append("\n\n");
        }
        return builder.toString();
    }

    public static class Meal implements Serializable {
        private String header;
        private ArrayList<String> menuArray = new ArrayList<>();

        public Meal(String header, String ... menus) {
            this.header = header;
            Collections.addAll(menuArray, menus);
        }

        public void setMenus(String ... menus) {
            menuArray.clear();
            Collections.addAll(menuArray, menus);
        }

        public String getHeader() {
            return header;
        }
        public ArrayList<String> getMenus() {
            return menuArray;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(header).append(">\n");
            for (String menu : menuArray) {
                builder.append(menu).append("\n");
            }
            return builder.toString();
        }
    }

    public static class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int HEADER_VIEW = 1;

        private MenuPlanner menuPlanner;
        private List<Meal> meals;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public View root;
            public TextView title;
            public TextView menu;

            public ViewHolder(View view) {
                super(view);
                root = view;
                title = (TextView) view.findViewById(R.id.title);
                menu = (TextView) view.findViewById(R.id.menu);
            }
        }
        public static class HeaderHolder extends RecyclerView.ViewHolder {
            public TextView header;

            public HeaderHolder(View view) {
                super(view);
                header = (TextView) view.findViewById(R.id.header);
            }
        }

        public MealAdapter(MenuPlanner menuPlanner) {
            this.menuPlanner = menuPlanner;
            this.meals = menuPlanner.getMeals();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_VIEW) {
                View header = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_planer_card_header, parent, false);
                return new HeaderHolder(header);
            }
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_planer_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof  HeaderHolder) {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.header.setText(menuPlanner.getDate());
            } else if (holder instanceof ViewHolder) {
                position --;
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.title.setText(meals.get(position).getHeader());
                viewHolder.menu.setText(StringUtil.join(meals.get(position).getMenus(), "\n"));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_VIEW;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return meals.size()+1;
        }
    }
}
