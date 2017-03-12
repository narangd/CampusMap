package com.example.campusmap.form;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.campusmap.R;

import org.jsoup.helper.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuPlanner implements Serializable {
// 세트마다

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

    public void addMeal(int index, String mealType, String ... menus) {
        meals.add(new Meal(mealType, menus));
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
        private String mealType;
        private ArrayList<String> menuArray = new ArrayList<>();

        public Meal(String mealType, String ... menus) {
            this.mealType = mealType;
            for (String menu : menus) {
                int index = menu.indexOf("</");
                if (index >= 0) {
                    menu = menu.substring(0, index);
                }
                menuArray.add(menu);
            }
//            Collections.addAll(menuArray, menus);
        }

        public String getMealType() {
            return mealType;
        }
        public ArrayList<String> getMenus() {
            return menuArray;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(mealType).append(">\n");
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

        public static class CardHolder extends RecyclerView.ViewHolder {
            TextView menuTypeView;
            public TextView menu;

            public CardHolder(View view, ViewGroup parent) {
                super(view); // not ViewGroup

                menuTypeView = (TextView) view.findViewById(R.id.menu_type);
                LinearLayout cardListLayout = (LinearLayout) view.findViewById(R.id.card_list);
                View cardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_menu, parent, false);
                cardListLayout.addView(cardView);

                menu = (TextView) cardView.findViewById(R.id.menu);
            }

            void setMenu(String... menus) {

            }

//            void setMeals()
        }
        public static class HeaderHolder extends RecyclerView.ViewHolder {
            public TextView dateTextView;

            public HeaderHolder(View view) {
                super(view);
                dateTextView = (TextView) view.findViewById(R.id.date);
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
                        .inflate(R.layout.card_header_menu_planer, parent, false);
                return new HeaderHolder(header);
            }
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_menu_planer, parent, false);
            return new CardHolder(view, parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof  HeaderHolder) {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.dateTextView.setText(menuPlanner.getDate());
            } else if (holder instanceof CardHolder) {
                position --;
                CardHolder cardHolder = (CardHolder) holder;
                cardHolder.menuTypeView.setText(meals.get(position).getMealType());
                cardHolder.menu.setText(StringUtil.join(meals.get(position).getMenus(), "\n"));
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
